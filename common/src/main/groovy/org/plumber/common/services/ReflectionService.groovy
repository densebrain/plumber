package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 11/20/14.
 */

@Service
@Slf4j
class ReflectionService {

	private Reflections reflections

	@PostConstruct
	private void setup() {
		reflections = new Reflections(ClasspathHelper.forJavaClassPath())
	}

	public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
		return reflections.getSubTypesOf(type)
	}
}
