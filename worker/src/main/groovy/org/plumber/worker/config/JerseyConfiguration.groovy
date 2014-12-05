package org.plumber.worker.config


import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spring.SpringComponentProvider
import org.glassfish.jersey.servlet.ServletContainer
import org.glassfish.jersey.servlet.ServletProperties
import org.plumber.client.services.Plumber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

import org.springframework.core.annotation.Order
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.filter.RequestContextFilter

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRegistration
import javax.ws.rs.client.Client

/**
 * Created by jglanz on 11/14/14.
 */
@Configuration
@ConditionalOnClass([SpringComponentProvider.class, ServletRegistration.class])
@ConditionalOnBean(ResourceConfig.class)
@ConditionalOnWebApplication
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(DispatcherServletAutoConfiguration.class)
class JerseyConfiguration implements WebApplicationInitializer {

    @Autowired
    private JerseyConfig config;


    @Bean
    Client createClient() {
        return Plumber.createClient()
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    @Bean
    @ConditionalOnMissingBean(name = "jerseyServletRegistration")
    public ServletRegistrationBean jerseyServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new ServletContainer(), "/api/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS,
                config.getClass().getName());
        registration.setName("jerseyServlet");

        return registration;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // We need to switch *off* the Jersey WebApplicationInitializer because it
        // will try and register a ContextLoaderListener which we don't need
        servletContext.setInitParameter("contextConfigLocation", "<NONE>");


    }




}
