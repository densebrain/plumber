package org.plumber.worker.services

import groovy.util.logging.Slf4j
import org.plumber.common.Constants
import org.plumber.client.domain.OSDetails
import org.plumber.client.domain.OSType
import org.plumber.common.services.FileService
import org.plumber.common.services.ShellCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by jglanz on 11/19/14.
 */

@Service
@Slf4j
class ConfigService implements Constants, ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	private File configFile

	ConfigObject config

	@Autowired
	FileService fileService

	@Autowired
	ShellCommand shell;

	@Autowired
	EmbeddedWebApplicationContext embeddedServletContainer

	@Value('${manager:}')
	String managerHost

	@Value('${name:}')
	String name

	@Value('${local.server.port:0}')
	int localPort

	@Value('${server.port:0}')
	int serverPort



	OSDetails osDetails

	OSType osType

	@Value('${executors:1}')
	int executorCount

	private OSInfo info = new OSInfo()

	@PostConstruct
	void load() {
		//Get OS Info
		osType = info.isMac() ? OSType.MAC : info.isWindows() ? OSType.WINDOWS : OSType.UNIX

		//Get OS Details
		String osName = osType.toString()
		String version = 'UNKNOWN'

		def props = null
		switch (osType) {
			case OSType.MAC:
				props = parseOSColonResponse('sw_vers')
				break
			case OSType.UNIX:
				props = parseOSColonResponse('lsb_release -a')
				break

		}

		if (props) {
			if (props['Distributor ID'])
				osName = props['Distributor ID']
			else if (props['ProductName'])
				osName = props['ProductName']


			if (props['ProductVersion'])
				version = props['ProductVersion']
			else if (props['Release'])
				version = props['Release']

		}

		osDetails = new OSDetails(name: osName, version: version, type: osType)

		//Now load the config
		String hostname = InetAddress.localHost.canonicalHostName
		name = (name) ? name : hostname

		if (localPort > 0)
			serverPort = localPort

		if (!configFile)
			configFile = new File(fileService.cacheDir, "config.${name}")

		if (configFile.exists())
			config = new ConfigSlurper().parse(configFile.text)
		else
			config = [:]


		//Initialize basics
		if (!config.workerId)
			config.workerId = UUID.randomUUID().toString()

		save()
	}

	@PreDestroy
	void save() {
		configFile.withWriter { writer ->
			config.writeTo(writer)
		}
	}

	private Map<String, String> parseOSColonResponse(cmd) {
		def props = [:]
		ShellCommand.Result result = shell.execute(cmd)
		if (!result.success())
			return null

		String content = result.out
		for(String line : content.split('\n')) {
			String[] parts = line.split(':')
			if (parts.length != 2) {
				continue
			}

			props[parts[0].trim()] = parts[1].trim()
		}

		return props
	}



	class OSInfo {

		static String OS = System.getProperty("os.name").toLowerCase();

		boolean isWindows() {

			return (OS.indexOf("win") >= 0);

		}

		boolean isMac() {

			return (OS.indexOf("mac") >= 0);

		}

		boolean isUnix() {

			return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

		}

		boolean isSolaris() {

			return (OS.indexOf("sunos") >= 0);

		}

	}

	@Override
	void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
		log.info("Worker is up and listening on port ${event.embeddedServletContainer.port}")
	}
}
