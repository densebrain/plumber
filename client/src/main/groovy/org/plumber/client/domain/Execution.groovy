package org.plumber.client.domain

import groovy.transform.ToString

/**
 * Created by jglanz on 11/17/14.
 */
@ToString
class Execution {

    State state = State.DRAFT

    LinkedList<TaskResult> results = new LinkedList<>()

    Date createdAt = new Date(), startedAt, completedAt

    String output



}
