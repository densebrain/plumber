package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.plumber.client.domain.Execution
import org.slf4j.helpers.MessageFormatter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by jglanz on 11/18/14.
 */

@Component
@Scope(value= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j(value="logger")
class JobContext implements org.plumber.client.domain.JobContext{

	@Autowired
	ApplicationContext applicationContext

	private StringBuffer logBuf = new StringBuffer()

	Execution execution

	Map<?,?> values = [:]

	static URL createURL(String url) {
		return new URL(null, url, new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(URL u) throws IOException {
				return null
			}
		})
	}

	public <T> T resolveObject(URL url, Class<T> clazz) {
		if (!url.getProtocol() == 'context')
			return null

		String base = url.getHost()
		def scope = values[base]
		def result = scope

		if (url.getPath() != null && !url.getPath().equals('/')) {
			String[] parts = parsePath(url.getPath())
			parts.each { it ->
				if (!it)
					return
				result = result[it]
			}
		}

		return (T) result
	}

	private String[] parsePath(String path) {
		return path.split("/")
	}

	void log(String msg) {
		log(msg, (Throwable)null)
	}

	void log(String msg, Throwable t) {
		logger.debug(msg)

		logBuf.append(msg).append('\n')
		if (execution) {
			execution.output = logBuf.toString()
		}

		if (t) {
			log(ExceptionUtils.getStackTrace(t), (Throwable) t)
		}
	}

	void log(String format, Object...args) {
		log(MessageFormatter.arrayFormat(format, args).getMessage())
	}

}
