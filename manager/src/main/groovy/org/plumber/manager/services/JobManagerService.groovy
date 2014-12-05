package org.plumber.manager.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.plumber.client.domain.Execution
import org.plumber.common.domain.Worker
import org.plumber.common.services.FileService
import org.springframework.beans.factory.annotation.Value

import javax.annotation.PostConstruct

import static org.plumber.client.domain.State.*

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.client.domain.State
import org.plumber.common.services.JobService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/19/14.
 */

@Service
@Slf4j
class JobManagerService {

	@Autowired
	private JobService jobService

	@Autowired
	private FileService files

	@Autowired
	private ObjectMapper objectMapper

	@Value('${local.server.port:0}')
	private int localPort

	@Value('${server.port:0}')
	private int serverPort

	private File pendingJobsFile


	private final LinkedList<JobHolder> pendingJobs = new LinkedList<>()

	@PostConstruct
	void setup() {
		if (localPort > 0)
			serverPort = localPort

		pendingJobsFile = new File(files.cacheDir, 'jobs.pending.json')
		if (pendingJobsFile.exists()) {
			log.info("Loading persisted incomplete jobs")

			String jobsJson = pendingJobsFile.text
			List<JobHolder> persistedJobs = new Gson().fromJson(jobsJson, new TypeToken<List<JobHolder>>() {}.getType());

			pendingJobs.addAll(persistedJobs)
		}

		log.info("Started Manager on ${serverPort}")
	}

	void persist() {
		synchronized (pendingJobs) {
			String jobsJson = new Gson().toJson(pendingJobs)
			log.debug("Writing ${jobsJson}")
			pendingJobsFile.text = jobsJson

		}
	}

	File getJobFile(String id) {
		return new File(files.cacheDir, "jobs/${id}.json")
	}

	void persist(Job job) {
		File jobFile = getJobFile(job.id)
		jobFile.text = new Gson().toJson(job)
	}

	Job get(String id) {
		File jobFile = getJobFile(id)
		Job job = new Gson().fromJson(jobFile.text)
		return job
	}

	Job add(Job job) {
		synchronized (pendingJobs) {
			if (!job.id)
				job.id = UUID.randomUUID().toString()

			pendingJobs.addLast(new JobHolder(job: job, state: READY))
			persist(job)
			persist()
		}

		return job
	}

	Job next(Worker worker) {
		synchronized (pendingJobs) {
			for (JobHolder holder : pendingJobs) {
				if (holder.state == READY) {
					holder.state = EXECUTING
					holder.worker = worker
					holder.changedAt = System.currentTimeMillis()
					holder.job.state = EXECUTING

					persist(holder.job)
					persist()

					return holder.job
				}
			}
		}
	}

	Job update(Job job) {
		//First check state and make updates
		if (job.state == EXECUTING && job.executions.size() > 0) {
			Execution exec = job.executions.last
			if (exec.state == FAILED) {
				if (job.executions.size() >= job.maxRetries) {
					job.state = FAILED
				} else {
					job.state = READY
				}
			} else if (exec.state == COMPLETE) {
				job.state = COMPLETE
			}
		}

		persist(job)

		synchronized (pendingJobs) {
			JobHolder existing = null
			for (JobHolder holder : pendingJobs) {
				if (holder.job == job) {
					existing = holder
					break
				}
			}

			if (existing) {
				existing.job = job

				if (job.state.ordinal() >=  COMPLETE.ordinal()) {
					pendingJobs.remove(job)
					persist()
				} else
					existing.state = job.state

			}
		}

		return job
	}


	static class JobHolder {
		Job job
		State state
		Worker worker
		long changedAt = 0

		boolean equals(o) {
			if (this.is(o)) return true
			if (!(o instanceof JobHolder)) return false

			JobHolder jobHolder = (JobHolder) o

			if (job != jobHolder.job) return false

			return true
		}

		int hashCode() {
			return job.hashCode()
		}
	}
}
