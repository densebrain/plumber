package org.plumber.manager.domain



/**
 * Created by jglanz on 11/19/14.
 */


class Job extends org.plumber.client.domain.Job implements Persistent {

	void preUpdate() {
		if (maxRetries == null)
			maxRetries = 0


	}
}
