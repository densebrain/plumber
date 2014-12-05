package org.plumber.worker.config

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Created by jglanz on 11/17/14.
 */
@Component
@ConditionalOnProperty(name='manager', matchIfMissing = true, havingValue = 'false')
class JerseyWorkerConfig extends ResourceConfig implements JerseyConfig {

	JerseyWorkerConfig() {
		packages(true, "org.plumber.manager.controllers")
		property("com.sun.jersey.api.json.POJOMappingFeature", "true");
	}
}
