package org.plumber.common.util

/**
 * Created by jglanz on 12/5/14.
 */
class Functions {
	public static <T> T[] toArray(List<T> list, Class<T> k) {
		return list.toArray(
			(T[])java.lang.reflect.Array.newInstance(k, list.size()));
	}
}
