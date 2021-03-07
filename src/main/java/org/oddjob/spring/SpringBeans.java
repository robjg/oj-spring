package org.oddjob.spring;

import org.oddjob.arooa.life.Destroy;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * An Oddjob job that will load a Spring application context and make
 * it's beans available to Oddjob. The Spring XML configuration
 * may be specified either as files or class path resources.
 * <p>
 * Beans within Spring may be accessed from other Oddjob components using 
 * the notation ${<i>id-of-this</i>/<i>name-of-bean</i>}.
 * <p>
 * Oddjob beans are made available via a parent {@link BeanFactory} so can 
 * be accessed by a reference in Spring.
 * <p>
 * Oddjobs beans and there properties and Oddjob properties can also be 
 * accessed in Spring using the ${<i>id</i>.<i>property</i>} or 
 * ${any.property.name} notation.
 * <p>
 * Note that the SpringApplication context will not be closed until this
 * component is destroyed (i.e. because Oddjob is terminating) or the
 * job is reset. If this is likely to keep resources active longer than
 * desired then consider using a {@link SpringService}.
 * 
 * @author rob
 *
 */
public class SpringBeans extends SpringBase
implements Runnable, BeanDirectoryOwner {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringBeans.class);
	

	/** The resultant ApplicationContext. */
	private ConfigurableApplicationContext applicationContext;
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
	
		logger.info("Loading Application Context.");
		
		applicationContext = 
				loadApplicationContext();	}
	
	/**
	 * @see BeanDirectoryOwner
	 */
	public BeanDirectory provideBeanDirectory() {
		if (applicationContext == null) {
			return null;
		}
		else {
			return new BeanDirectoryAdaptor(applicationContext, 
					getArooaSession());
		}		
	}

	@SoftReset
	@HardReset
	@Destroy
	public void reset() {
		if (applicationContext != null) {
			logger.info("Closing Application Context.");
			applicationContext.close();
		}
	}
}
