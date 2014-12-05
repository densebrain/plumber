package org.plumber.manager.controllers

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.common.domain.Worker
import org.plumber.manager.services.JobManagerService
import org.plumber.manager.services.WorkerManagerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by jglanz on 11/19/14.
 */

@Path('worker')
@Component
@Slf4j
class WorkerController {

	@Autowired
	WorkerManagerService workerManagerService

	@Autowired
	JobManagerService jobManagerService

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<Worker> getAll() {
		return workerManagerService.workers.values() as List<Worker>
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Worker post(Worker worker) {
		log.debug("Registering {}", worker)
		return workerManagerService.set(worker)
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Worker put(Worker worker) {
		log.trace("Updating worker {}", worker)
		return workerManagerService.set(worker)
	}

	@GET
	@Path('job')
	@Produces(MediaType.APPLICATION_JSON)
	Job getJob(@HeaderParam('workerId') String workerId) {
		Worker worker = workerManagerService.get(workerId)
		log.trace("Getting job {}", worker)
		return jobManagerService.next(worker)
	}

//	@PUT
//	@Path('job')
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	Job putJob(@HeaderParam('workerId') String workerId, Job job) {
//		Worker worker = workerManagerService.get(workerId)
//		log.debug("Updating Job {} from worker {}", worker)
//		return workerManagerService.updateJob(worker, job)
//	}



}
