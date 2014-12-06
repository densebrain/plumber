package org.plumber.worker.config

import groovy.util.logging.Slf4j
import org.plumber.common.domain.ContextHolder
import org.plumber.common.services.FileService
import org.plumber.common.util.Functions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader

/**
 * Created by jglanz on 12/5/14.
 */

@Configuration
@Slf4j
class LoaderConfig {

	@Autowired
	private FileService fileService

	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext applicationContext;

	@Value('${extraPath:}')
	String extraPath





	@Bean()
	public ContextHolder loadContexts(@Qualifier("PlumbingLoader") URLClassLoader loader) {
		ArrayList<AnnotationConfigApplicationContext> contexts = new ArrayList<>()

		for (URL url : loader.findResources('plumber.properties')) {
			Properties props = new Properties()
			props.load(url.openStream())

			String scanPackage = props.getProperty("scan", null)
			log.info("Going to scan package ${scanPackage}")
			if (scanPackage) {
				AnnotationConfigApplicationContext subContext = new AnnotationConfigApplicationContext(scanPackage)
				subContext.setParent(applicationContext)
				subContext.start()

				contexts.add(subContext)
			}
		}

		return new ContextHolder(contexts:contexts)
	}

	@Bean(name="PlumbingLoader")
	public URLClassLoader loader() {
		log.debug("Application context is of type ${applicationContext.class.name}")

		List<String> paths = new ArrayList<>()
		paths.add('extras')
		for (String path : extraPath.split(':')) {
			if (path && !paths.contains(path))
				paths.add(path)
		}

		List<URL> urls = new ArrayList<>()
		for (String path : paths) {
			File file = new File(path)
			if (file.exists())
				urls.add(file.toURL())
		}


		URLClassLoader loader = new URLClassLoader(Functions.toArray(urls, URL.class), applicationContext.getClassLoader())
		Thread.currentThread().setContextClassLoader(loader)
		applicationContext.setResourceLoader(new DefaultResourceLoader(loader))




		return loader

	}

}
