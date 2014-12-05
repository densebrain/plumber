package org.plumber.worker.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.common.domain.Worker
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled

import javax.annotation.PostConstruct
import javax.ws.rs.ProcessingException
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.MediaType

/**
 * Created by jglanz on 11/19/14.
 */


@Slf4j
class WorkerService {



	@Autowired
	Client client

	@Autowired
	ConfigService configService

	@Autowired
	ObjectFactory<WorkerThread> workerFactory

	@Value('${manager:false}')
	boolean isManager

	Worker worker

	List<WorkerThread> workers = []

	protected final Object managerMutex = new Object()

	@PostConstruct
	protected void setup() {

		String hostname = InetAddress.localHost.canonicalHostName
		String ip = InetAddress.localHost.hostAddress
		String name = configService.name

		worker = [
				name: name,
				os: System.properties['os.name'],
				hostname: hostname,
				host: "${hostname}:${configService.serverPort}",
				ip: ip,
				id: configService.config.workerId,
				jobs: [],
				osDetails: configService.osDetails
		]

		register()

		int executorCount = configService.executorCount

		for (i in 1..executorCount) {
			WorkerThread worker = workerFactory.object
			worker.name = "Worker ${i}"
			workers += worker
		}
	}

	private Invocation.Builder createRequest(String path, MediaType type) {
		Invocation.Builder request = client.target("http://${configService.managerHost}/api/${path}").request(type)
		request.header('workerId', worker.id)

		return request
	}

	protected void updateJobsInternal(Worker updatedWorker) {
		List<Job> jobsToRemove = []
		for (Job currentJob : worker.jobs) {
			boolean exists = false
			for (Job job : updatedWorker.jobs) {
				if (job.equals(currentJob)) {
					exists = true
					break
				}
			}

			if (!exists) {
				jobsToRemove += currentJob
			}
		}

		jobsToRemove.each { job ->
			log.info("Removing Job ${job}")
			worker.jobs.remove(job)
		}
	}

	@Scheduled(fixedRate = 10000L)
	void heartbeat() {
		synchronized (managerMutex) {
			Invocation.Builder req = createRequest('worker', MediaType.APPLICATION_JSON_TYPE)
			Worker updatedWorker = req.put(Entity.entity(worker, MediaType.APPLICATION_JSON_TYPE), Worker.class)

			updateJobsInternal(updatedWorker)
		}
	}

	void register() {
		synchronized (managerMutex) {
			Invocation.Builder req = createRequest('worker', MediaType.APPLICATION_JSON_TYPE)
			worker = req.post(Entity.entity(worker, MediaType.APPLICATION_JSON_TYPE), Worker.class)
		}
	}


	Job getJob() {

		Invocation.Builder req = createRequest('worker/job', MediaType.APPLICATION_JSON_TYPE)
		try {
			Job job = null

			synchronized (managerMutex) {
				job = req.get(Job.class)
			}
			if (job) {
				worker.jobs += job
				heartbeat()
			}
			return job
		} catch (ProcessingException pe) {
			if (pe.cause instanceof ConnectException)
				log.warn('Unable to connect to the plumbing manager')
			else
				throw pe
		}


	}




}
