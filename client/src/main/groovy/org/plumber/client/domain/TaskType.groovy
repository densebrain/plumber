package org.plumber.client.domain

/**
 * Created by jglanz on 11/17/14.
 */
interface TaskType {

    String name()

    TaskExecutor executor()

    Requirements requirements();
}