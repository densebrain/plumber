package org.plumber.common.services

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import java.lang.reflect.Modifier

/**
 * Created by jglanz on 11/18/14.
 */

@Service
@Slf4j
class BeanService {
	@Autowired
	ApplicationContext context;

	Object get(Class<?> clazz) {
		if (Modifier.isAbstract(clazz.getModifiers()))
			return null

		Object o = null
		try {
			o = context.getBean(clazz);
		} catch (Exception e) {
			try {
				o = context.autowireCapableBeanFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true)
			} catch (Exception e2) {
				try {
					o = clazz.newInstance()
				} catch (Exception e3) {
					log.debug("Unavailable in spring context or classloader: ${e3.message}");
//					log.debug("Failed to initialize [spring]", e2);
//					log.debug("Failed to initialize ", e3);
				}
			}
		}

		return o
	}

}
