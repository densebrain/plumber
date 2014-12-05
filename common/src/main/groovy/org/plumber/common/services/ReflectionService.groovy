package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.reflections.Reflections
import org.reflections.util.ClasspathHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

/**
 * Created by jglanz on 11/20/14.
 */

@Service
@Slf4j
class ReflectionService {

	private Reflections reflections

	@Autowired()
	@Qualifier("PlumbingLoader")
	private URLClassLoader plumberClassLoader

	@PostConstruct
	private void setup() {
		HashSet<URL> urls = new HashSet<>(ClasspathHelper.forJavaClassPath())
		urls.addAll(ClasspathHelper.forClassLoader(plumberClassLoader))

		reflections = new Reflections(urls)
	}

	public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
		return reflections.getSubTypesOf(type)
	}
}
