package org.plumber.worker

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.ComponentScan

/**
 * Created by jglanz on 11/14/14.
 */

@SpringBootApplication
@SpringApplicationConfiguration
@ComponentScan(basePackages = ["org.plumber", "com.rads"])
@EnableAutoConfiguration(exclude = [
    GroovyTemplateAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
])
class Application extends SpringBootServletInitializer {


    static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}
