package org.plumber.manager.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by jglanz on 11/19/14.
 */

@Document
class Job extends org.plumber.client.domain.Job implements Persistent {

	void preUpdate() {
		if (maxRetries == null)
			maxRetries = 0


	}
}
