package org.plumber.worker.config

import groovy.util.logging.Slf4j
import org.plumber.common.services.FileService
import org.plumber.common.util.Functions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by jglanz on 12/5/14.
 */

@Configuration
@Slf4j
class LoaderConfig {

	@Autowired
	private FileService fileService

	@Autowired
	private ApplicationContext applicationContext;

	@Value('${extraPath:}')
	String extraPath

	@Bean(name="PlumbingLoader")
	public URLClassLoader loader() {
		List<String> paths = new ArrayList<>()
		paths.add('extras')
		for (String path : extraPath.split(':')) {
			if (path && !paths.contains(path))
				paths.add(path)
		}

		List<URL> urls = new ArrayList<>()
		for (String path : paths) {
			urls.add(new File(path).toURL())
		}


		URLClassLoader loader = new URLClassLoader(Functions.toArray(urls, URL.class), applicationContext.getClassLoader())
		for (URL url : loader.findResources('plumber.properties')) {
			Properties props = new Properties()
			props.load(url.openStream())

			String scanPackage = props.getProperty("scan", null)
			log.info("Going to scan package ${scanPackage}")
		}

		return loader

	}

}
