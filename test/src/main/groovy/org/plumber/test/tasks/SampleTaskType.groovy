package org.plumber.test.tasks

import groovy.util.logging.Slf4j
import org.plumber.client.domain.JobContext
import org.plumber.client.domain.OSType
import org.plumber.client.domain.OsPackage
import org.plumber.client.domain.Requirements
import org.plumber.client.domain.Task
import org.plumber.client.domain.TaskExecutor
import org.plumber.client.domain.TaskResult
import org.plumber.client.domain.TaskType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 12/5/14.
 */
@Service
@Slf4j
class SampleTaskType implements TaskType {

	@PostConstruct
	void setup() {
		log.info("Loaded SampleTaskType")
	}

	@Override
	String name() {
		return "Sample"
	}

	@Override
	Requirements requirements() {
		return new Requirements(
			osTypes: [OSType.ALL],
			packages: []
		)
	}


	@Override
	TaskExecutor executor() {
		return new TaskExecutor() {
			@Override
			TaskResult execute(JobContext context, Task task) throws Exception {
				System.out.println("Sample echo - ${task.args.text}")
				return new TaskResult(code: TaskResult.Code.SUCCESS)
			}
		}
	}
}
