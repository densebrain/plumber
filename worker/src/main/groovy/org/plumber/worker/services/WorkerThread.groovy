package org.plumber.worker.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Execution
import org.plumber.client.domain.Job
import org.plumber.common.services.JobService
import org.plumber.common.util.ManagedThread
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by jglanz on 11/19/14.
 */
@Component
@Scope(value= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
class WorkerThread extends ManagedThread {

	@Autowired
	JobService jobService

	@Autowired
	WorkerService workerService

	@PostConstruct
	void setup() {
		start()
	}

	@PreDestroy
	void cleanup() {
		kill(Long.MAX_VALUE)
	}

	@Override
	void work() {
		try {
			Job job = workerService.job
			if (job == null) {
				log.trace("Going to sleep")
				synchronized (this) {
					try {
						wait(1000)
					} catch (Throwable t) {
					}
				}
				return
			}
			log.info("Executing: ${job.id}")
			jobService.execute(null, job)
			workerService.heartbeat()
			log.info("Executed: ${job.id}")
		} catch (Exception e) {
			log.error("Error while working jobs", e)
		}
	}
}
