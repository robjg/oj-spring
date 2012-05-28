package org.oddjob.spring;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * An Oddjob Job that loads a Spring application context and runs
 * a single {@link Runnable} within that application context. The name
 * of the bean that is this {@code Runnable} must be provided.
 * The Spring XML 
 * configuration may be specified either as files or class path resources.
 * <p>
 * Spring may access properties and references from Oddjob within
 * the configuration files but unlike {@link SpringBeans} and 
 * {@link SpringService} the Spring bean are not available outside
 * of this component because the application context is closed once
 * the job has executed.
 * 
 * @author rob
 *
 */
public class SpringJob extends SpringBase 
implements Runnable {
	
	/** The name of the Spring bean that is the Runnable. Note that
	 * a bean name must be provided. */
	private String beanName;
		
	@Override
	public void run() {
		if (beanName == null) {
			throw new IllegalStateException("No beanName to run.");
		}
		
		ConfigurableApplicationContext applicationContext = 
				loadApplicationContext();
		
		
		Runnable main = applicationContext.getBean(beanName, 
				Runnable.class);
		
		main.run();
		
		applicationContext.close();
	}

	/**
	 * Getter for the name of the Spring bean that is the Runnable.
	 * 
	 * @return The name of the bean.
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * Setter for the name of the bean that is the Runnable.
	 * 
	 * @param beanName The name the bean.
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	
}
