package org.plumber.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 11/9/12
 * Time: 2:06 PM
 *
 * @author jglanz
 */
public abstract class ManagedThread extends Thread {

	private Logger logger;

	private volatile boolean running = false;

	/**
	 * Create the managed thread
	 */
	public ManagedThread() {
		super();

		logger = LoggerFactory.getLogger(ManagedThread.class);
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Start the thread
	 */
	public void start() {
		running = true;

		super.start();
	}

	/**
	 * Threads control function
	 */
	public void run() {

		while(true && !Thread.interrupted()) {
			if (!running)
				break;

			work();
		}
	}

	/**
	 * Implementing work function
	 */
	public abstract void work();


	/**
	 * Used to kill the thread
	 *
	 * @param millis how long to wait for the thread to exit
	 */
	public void kill(long millis) {
		logger.info("Shutting down: " + getClass().getSimpleName());
		synchronized (this) {
			if (!running)
				return;

			try {
				running = false;
				interrupt();

				// if not self-death then join
				if (millis != -1 && !Thread.currentThread().equals(this)) {
					join(millis);
					logger.info("Joined: " + getClass().getSimpleName());
				}

			} catch (Exception e) {
				logger.error("Failed to stop thread", e);
			}
		}

		logger.info("Killed: " + getClass().getSimpleName());
	}

}
