package org.plumber.worker.tests

import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.InitializationError
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by jglanz on 11/19/14.
 */
class SpringTestClassRunner  extends SpringJUnit4ClassRunner {

	private TestClassListener instanceSetupListener;

	public SpringTestClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected Object createTest() throws Exception {
		Object test = super.createTest();
		// Note that JUnit4 will call this createTest() multiple times for each
		// test method, so we need to ensure to call "beforeClassSetup" only once.
		if (test instanceof TestClassListener && instanceSetupListener == null) {
			instanceSetupListener = (TestClassListener) test;
			instanceSetupListener.beforeClassSetup();
		}
		return test;
	}

	@Override
	public void run(RunNotifier notifier) {
		super.run(notifier);

		if (instanceSetupListener != null)
			instanceSetupListener.afterClassSetup();
	}
}
