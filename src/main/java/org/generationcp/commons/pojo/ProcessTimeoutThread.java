
package org.generationcp.commons.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTimeoutThread extends Thread {

	static final Logger LOG = LoggerFactory.getLogger(ProcessTimeoutThread.class);

	private final Process process;
	private final long timeout;

	public ProcessTimeoutThread(final Process process, final long timeout) {
		this.process = process;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(this.timeout);
			this.process.destroy();
		} catch (final InterruptedException e) {
			ProcessTimeoutThread.LOG.error(e.getMessage(), e);
		}
	}

}
