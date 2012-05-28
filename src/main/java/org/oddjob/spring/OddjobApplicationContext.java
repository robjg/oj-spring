package org.oddjob.spring;

import org.oddjob.arooa.ArooaSession;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Create a Spring ApplicationContext that can be the parent of 
 * a Spring application context.
 * <p>
 * The <code>refresh</code> method needs to be called before it
 * can be used.
 * 
 * @author rob
 *
 */
public class OddjobApplicationContext extends AbstractApplicationContext {
	
	private final ConfigurableListableBeanFactory beanFactory; 
	
	/**
	 * Constructor.
	 * 
	 * @param session The session to use from Oddjob.
	 */
	public OddjobApplicationContext(ArooaSession session) {
		beanFactory = new DefaultListableBeanFactory(
				new OddjobBeanFactory(session));
	}
	
	@Override
	protected void closeBeanFactory() {
		// Nothing to do
	}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory()
			throws IllegalStateException {
		return beanFactory;
	}

	@Override
	protected void refreshBeanFactory() throws BeansException,
			IllegalStateException {
		// Nothing to do	
	}

	
}
