package org.plumber.client.domain


/**
 * Created by jglanz on 11/17/14.
 */
interface TaskExecutor {

    TaskResult execute(JobContext context, Task task) throws Exception;
}
