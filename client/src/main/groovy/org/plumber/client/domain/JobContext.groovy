package org.plumber.client.domain

/**
 * Created by jglanz on 12/2/14.
 */
interface JobContext {
	void log(String msg)
	void log(String msg, Throwable t)
	void log(String msg, Object...args)

	Map<?,?> getValues()

	public <T> T resolveObject(URL url, Class<T> clazz)

}