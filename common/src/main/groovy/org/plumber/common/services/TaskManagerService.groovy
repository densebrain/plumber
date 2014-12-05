package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.plumber.client.domain.Task
import org.plumber.client.domain.TaskType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 11/18/14.
 */

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
class TaskManagerService {

	Map<String, TaskType> taskTypes = [:]

	@Autowired
	PackageManagerService packageManagerService

	@Autowired
	private ReflectionService reflectionService

	@Autowired
	BeanService beanService


	@PostConstruct
	void setup() {
		Set<Class<? extends TaskType>> clazzes = reflectionService.getSubTypesOf(TaskType.class)


		clazzes.each { clazz ->
			TaskType taskType = beanService.get(clazz);

			if (taskType != null && !taskTypes.containsValue(taskType)) {
				log.info("Found task type: {}", taskType.name())
				taskTypes[taskType.name()] = taskType
			}

		}
	}

	TaskType getTaskType(String alias) {
		return taskTypes[alias]
	}

	List<Task> parseTasks(json) {
		List<Task> tasks = []

		json?.each { it ->
			TaskType type = taskTypes[(String) it.type]
			if (!type)
				throw new IllegalArgumentException("task.type.unknown.${it.type}")

			tasks += new Task(it)
		}

		return tasks
	}

}
