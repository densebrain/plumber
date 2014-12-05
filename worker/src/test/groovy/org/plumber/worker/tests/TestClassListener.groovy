package org.plumber.worker.tests

/**
 * Created by jglanz on 11/19/14.
 */
interface TestClassListener {
	void beforeClassSetup() throws Exception;
	void afterClassSetup() throws Exception;
}
