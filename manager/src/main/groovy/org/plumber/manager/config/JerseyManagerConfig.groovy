package org.plumber.manager.config

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import org.plumber.worker.config.JerseyConfig

import org.springframework.stereotype.Component

/**
 * Created by jglanz on 11/17/14.
 */
@Component
class JerseyManagerConfig extends ResourceConfig implements JerseyConfig {

	JerseyManagerConfig() {
		packages(true, "org.plumber.manager.controllers")
		register(new JacksonJsonProvider());
		property("com.sun.jersey.api.json.POJOMappingFeature", "true");
		register(JacksonFeature.class)
	}


}
