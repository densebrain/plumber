package org.plumber.client.domain

import groovy.transform.ToString

/**
 * Created by jglanz on 11/17/14.
 */

@ToString
class TaskResult {

    Code code

    String message

    Throwable throwable

    Map<String,?> extra

    static enum Code {
        FAILURE, SUCCESS
    }
}
