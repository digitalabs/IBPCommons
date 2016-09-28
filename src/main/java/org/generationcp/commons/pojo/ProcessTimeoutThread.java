package org.generationcp.commons.pojo;

import org.generationcp.commons.spring.util.ToolLicenseUtil;
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
            Thread.sleep(timeout);
            process.destroy();
    	} catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
