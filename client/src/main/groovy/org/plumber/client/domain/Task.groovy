package org.plumber.client.domain
/**
 * Created by jglanz on 11/17/14.
 */
class Task {

	String id

	String type

	LinkedList<Task> tasks

	Map<String, Object> args = new HashMap<>()

}
