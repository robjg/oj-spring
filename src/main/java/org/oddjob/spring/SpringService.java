package org.oddjob.spring;

import org.apache.log4j.Logger;
import org.oddjob.FailedToStopException;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.framework.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * An Oddjob job that provides a Spring application
 * context as a service that can be started and stopped. The Spring XML 
 * configuration may be specified either as files or class path resources.
 * <p>
 * This component is almost identical to {@link SpringBeans} and access
 * to Oddjob properties and Spring beans is identical. The only difference
 * between the two is in the timing of the closing of the application 
 * context. This class closes the application context on stop.
 * 
 * @see SpringBeans
 * 
 * @author rob
 *
 */
public class SpringService extends SpringBase
implements Service, BeanDirectoryOwner {
	
	private static final Logger logger = Logger.getLogger(SpringService.class);
	

	/** The resultant ApplicationContext. */
	private volatile ConfigurableApplicationContext applicationContext;
	
	
	@Override
	public void start() throws Exception {
		logger.info("Loading Application Context.");
		
		applicationContext = 
				loadApplicationContext();	
	}
	
	@Override
	public void stop() throws FailedToStopException {
		if (applicationContext != null) {
			logger.info("Closing Application Context.");
			applicationContext.close();
			applicationContext = null;
		}		
	}
	
	/**
	 * @see BeanDirectoryOwner
	 */
	public BeanDirectory provideBeanDirectory() {
		ApplicationContext applicationContext = this.applicationContext;
		if (applicationContext == null) {
			return null;
		}
		else {
			return new BeanDirectoryAdaptor(applicationContext, 
					getArooaSession());
		}		
	}

}
