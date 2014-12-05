package org.plumber.worker.tests

import groovy.util.logging.Slf4j

import org.junit.runner.RunWith
import org.plumber.worker.Application
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.web.WebAppConfiguration

/**
 * Created by jglanz on 11/19/14.
 */
@RunWith(SpringTestClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Slf4j
abstract class BaseTest implements TestClassListener {
	@Override
	void beforeClassSetup() {

	}

	@Override
	void afterClassSetup() {

	}
}
