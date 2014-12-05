package org.plumber.manager.controllers

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Job
import org.plumber.common.services.JobService
import org.plumber.manager.services.JobManagerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by jglanz on 11/14/14.
 */
@Path("job")
@Component
@Slf4j
class JobController {

    @Autowired
    JobService jobService

    @Autowired
    JobManagerService jobManagerService

    @Autowired
    HttpServletRequest request

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Job post(Job job) {
        job.id = null
        return jobManagerService.add(job)
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Job> get() {
        return jobManagerService.getAll()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    Job get(@PathParam('id') String id) {
        //log.debug("Getting job with id ${id}")
        return jobManagerService.get(id)
    }

}
