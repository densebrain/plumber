package org.plumber.manager.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.client.domain.State
import org.plumber.common.Constants
import org.plumber.common.domain.Worker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/19/14.
 */

@Service
@Slf4j
class WorkerManagerService implements Constants {

	@Autowired
	JobManagerService jobManagerService

	final Map<String, Worker> workers = [:]

	Worker set(Worker worker) {
		if (!workers.containsKey(worker.id))
			log.trace("Register worker: ${worker}")


		worker.lastUpdated = new Date()
		synchronized (workers) {
			workers.remove(worker.id)
			workers[worker.id] = worker
		}

		List<Job> workerJobs = worker.jobs.clone()
		workerJobs.each { job ->
			updateJob(worker, job)
		}

		return worker
	}

	Worker get(String id) {
		return workers[id]
	}

	void updateJob(Worker worker, Job job) {
		worker = get(worker.id)

		//If the current execution is done then remove it
		//from the workers current jobs
		if (job.executions.size() && job.executions.last.state != State.EXECUTING)
			worker.jobs.remove(job)

		jobManagerService.update(job)


	}



	@Scheduled(fixedRate = 15000L)
	private void prune() {
		Map<String, Worker> workers = this.workers.clone()
		def idsToRemove = []
		workers.values().each { worker ->
			if (System.currentTimeMillis() - worker.lastUpdated.time > WORKER_TIMEOUT)
				idsToRemove += worker.id
		}

		if (idsToRemove.size()) {
			synchronized (this.workers) {
				idsToRemove.each { key ->
					log.info("Worker ${key} has not reported for more than ${WORKER_TIMEOUT} - removing from action")
					this.workers.remove(key)
				}
			}
		}

	}


}
