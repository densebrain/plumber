package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Execution
import org.plumber.client.domain.Job
import org.plumber.client.domain.Requirements
import org.plumber.client.domain.State
import org.plumber.client.domain.TaskExecutor
import org.plumber.client.domain.TaskResult
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by jglanz on 11/17/14.
 */

@Service
@Slf4j
class JobService {

	@Autowired
	TaskManagerService taskManagerService

	@Autowired
	PackageManagerService packageManagerService

	@Autowired
	ObjectFactory<JobContext> jobContextFactory

	Job parseJob(Map<?,?> json) {
		def tasksJson = json.tasks
		json.tasks = null

		Job job = new Job(json)
		job.tasks = taskManagerService.parseTasks(tasksJson)

		return job
	}

	void ensureRequirements(Job job) {
		job.tasks.each { task ->
			Requirements req = taskManagerService.getTaskType(task.type)?.requirements()
			req?.packages?.each { it ->
				packageManagerService.ensurePackage(it)
			}
		}
	}

	Execution execute(JobContext context, Job job) {
		Execution exec = new Execution()
		job.executions += exec
		if (!context)
			context = jobContextFactory.object

		context.execution = exec

		try {
			//Make sure the job can be execute on this machine
			ensureRequirements(job)

			exec.state = State.EXECUTING

			job.tasks.each { task ->
				TaskExecutor taskExec = taskManagerService.getTaskType(task.type)?.executor()
				if (!taskExec) {
					throw new Exception("Failed to get task executor for type ${task.type}")

				}

				TaskResult result = null;
				try {
					result = taskExec.execute(context, task);
				} catch (e) {
					log.error("Failed to process task", e)
					result = new TaskResult(
					        code: TaskResult.Code.FAILURE,
							message: e.message
					)
				}

				exec.results += result

				if (!result)
					throw new Exception("No result returned")

				if (result.code != TaskResult.Code.SUCCESS)
					throw new Exception(result.toString())
			}

			exec.state = State.COMPLETE
		} catch (e) {
			log.error("Failed to process job", e)
			exec.state = State.FAILED
		}

		return exec
	}
}
