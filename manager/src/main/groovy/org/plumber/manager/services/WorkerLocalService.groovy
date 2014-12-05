package org.plumber.manager.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.common.domain.Worker
import org.plumber.worker.services.WorkerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 12/3/14.
 */
@Service
@ConditionalOnProperty(name='worker', havingValue = 'true')
@Slf4j
class WorkerLocalService extends WorkerService {

	@Autowired
	WorkerManagerService workerManagerService

	@Autowired
	JobManagerService jobManagerService

	@PostConstruct
	void setup() {
		super.setup()

		log.debug("Initializing manager worker")
	}

	@Override
	void heartbeat() {
		synchronized (managerMutex) {
			Worker updatedWorker = workerManagerService.set(worker)
			updateJobsInternal(updatedWorker)
		}
	}

	@Override
	void register() {
		synchronized (managerMutex) {
			worker = workerManagerService.set(worker)
		}
	}

	@Override
	Job getJob() {
		synchronized (managerMutex) {
			Job job = jobManagerService.next(worker)

			if (job) {
				worker.jobs += job
				heartbeat()
			}

			return job
		}
	}
}
