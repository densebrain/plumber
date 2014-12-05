package org.plumber.common.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.ToString
import org.plumber.client.domain.Job
import org.plumber.client.domain.OSDetails

/**
 * Created by jglanz on 11/19/14.
 */

@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
class Worker {

	String id

	String hostname, ip, name

	String host

	String os

	List<Job> jobs = []

	OSDetails osDetails

	Date lastUpdated
}
