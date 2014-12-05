package org.plumber.client.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.glassfish.jersey.jackson.JacksonFeature
import org.plumber.client.domain.Job

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

import org.glassfish.jersey.client.ClientConfig

/**
 * Created by jglanz on 12/2/14.
 */
class Plumber {

	private String host

	private Client client

	@Provider
	public static class JacksonConfigurator implements ContextResolver<ObjectMapper> {

		private final ObjectMapper mapper;

		public JacksonConfigurator() {
			mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}

		@Override
		public ObjectMapper getContext(Class<?> type) {
			return mapper;
		}

	}

	static Client createClient() {
		ClientConfig config = new ClientConfig()
		config.register(JacksonConfigurator.class)
		config.register(JacksonFeature.class)
		config.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);


		Client client = ClientBuilder.newClient(config)

		return client
	}

	/**
	 * Instantiate a new plumber client with a specific host:port
	 *
	 * @param host
	 */
	Plumber(String host) {
		this.host = host
		this.client = createClient()
	}


	private WebTarget createTarget(String path) {
		return client.target("http://${host}/api/${path}")
	}
	private Invocation.Builder createRequest(String path, MediaType type) {
		Invocation.Builder request = createTarget(path).request(type)
		return request
	}

	/**
	 * Create a job for processing
	 *
	 * @param job
	 * @return
	 */
	Job create(Job job) {
		Invocation.Builder req = createRequest('job', MediaType.APPLICATION_JSON_TYPE)
		job = req.post(Entity.entity(job, MediaType.APPLICATION_JSON_TYPE), Job.class)

		return job
	}

	Job get(String id) {
		WebTarget target = createTarget("job").path(id)
		//Invocation.Builder req = createRequest("job", MediaType.APPLICATION_JSON_TYPE)
		Job job = target.request().get(Job.class)

		return job
	}
}
