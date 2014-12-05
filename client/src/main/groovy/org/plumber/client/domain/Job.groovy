package org.plumber.client.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.ToString

/**
 * Created by jglanz on 11/17/14.
 */

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Job extends Timestampable {

    String id, trackingId, name

    State state = State.READY

    Integer maxRetries = 0

    List<Task> tasks

    LinkedList<Execution> executions = new LinkedList<>()

    //Time in milliseconds between retries
    Long timeBetweenRetries

    Date lastExecutedOn


    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Job)) return false

        Job job = (Job) o

        if (id != job.id) return false

        return true
    }

    int hashCode() {
        return id.hashCode()
    }
}
