package org.plumber.manager.services

import org.plumber.client.domain.Execution
import org.plumber.common.domain.Worker
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

import javax.annotation.PostConstruct

import static org.plumber.client.domain.State.*

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.client.domain.State
import org.plumber.common.services.JobService
import org.plumber.manager.repo.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.Resource

/**
 * Created by jglanz on 11/19/14.
 */

@Service
@Slf4j
class JobManagerService {


	@Autowired
	private JobService jobService

	@Resource
	private JobRepository jobRepository

	@Value('${local.server.port:0}')
	private int localPort

	@Value('${server.port:0}')
	private int serverPort

	@PostConstruct
	void setup() {
		if (localPort > 0)
			serverPort = localPort

		log.info("Started Manager on ${serverPort}")
	}

	private final LinkedList<JobHolder> pendingJobs = new LinkedList<>()

	List<Job> getAll() {
		return jobRepository.findAll(new PageRequest(0,250,Sort.Direction.DESC, 'modifiedAt')).content
	}

	Job get(String id) {
		Job job = jobRepository.findOne(id)
		return job
	}

	Job add(Job job) {
		job = jobRepository.save(job)

		synchronized (pendingJobs) {
			pendingJobs.addLast(new JobHolder(job: job, state: READY))
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

					jobRepository.save(holder.job)

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

		//Update job in repo
		jobRepository.save(job)

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

				if (job.state.ordinal() >=  COMPLETE.ordinal())
					pendingJobs.remove(job)
				else
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
