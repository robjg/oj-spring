package org.oddjob.spring;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * Intended as a base class for to provide Spring Oddjob integration
 * functionality. This class isn't abstract so could be used independently
 * too.
 * 
 * @author rob
 *
 */
public class SpringBase 
implements ArooaSessionAware {
	
	private static final Logger logger = Logger.getLogger(SpringBase.class);
	
	/** 
     * The name of the component. Can be any text.
	 */
	private String name;

	/** 
     * Spring XML configuration files.
     * This property is not required if resources are supplied.
     * <p>
     * If the file is absolute it must be prefixed with 'file:'. For more
     * information on this see 
     * <a href="http://forum.springsource.org/archive/index.php/t-37155.html">A Spring Forum Post</a>
     * on the subject.
	 */
	private String[] files;
	
	/** 
     * Spring XML configuration class path resources.
     * This property is not required if files are supplied.
	 */
	private String[] resources;

	/** Provided by Oddjob. */
	private ArooaSession session;

	/** 
     * The class loader passed to Spring for its DefaultResourceLoader.
     * If not supplies the class loader for this class will be used.
	 */
	private ClassLoader classLoader;
	
	/**
	 * Getter for the name.
	 * 
	 * @return The name or null if one hasn't been set.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name.
	 * 
	 * @param name The name of this component.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the configuration files. 
	 * 
	 * @return Array of configuration files or null if none have been set.
	 */
	public String[] getFiles() {
		return files;
	}

	/**
	 * Setter for the configuration files.
	 * 
	 * @param files The configuration files.
	 */
	public void setFiles(String[] files) {
		this.files = files;
	}

	/**
	 * Getter for the resources.
	 * 
	 * @return Array of resource strings or null if none have been set.
	 */
	public String[] getResources() {
		return resources;
	}

	/**
	 * Setter for resources.
	 * 
	 * @param resources An array of class path resources to use.
	 */
	public void setResources(String[] resources) {
		this.resources = resources;
	}

	/**
	 * Getter for the class loader being used to load the resources.
	 * 
	 * @return The class loader or null if one hasn't been specified.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Set the class loader to load resources with.
	 * 
	 * @param classLoader The class loader to use to load resources.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.life.ArooaSessionAware#setArooaSession(org.oddjob.arooa.ArooaSession)
	 */
	@ArooaHidden
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	/**
	 * Allow sub classes access to the session.
	 * 
	 * @return The session. Should never be null when used in Oddjob.
	 */
	protected ArooaSession getArooaSession() {
		return session;
	}

	/**
	 * Load a Spring Application Context from either resources or files.
	 * It is up to the calling code to ensure this context is closed.
	 *  
	 * @return The Application Context.
	 */
	public ConfigurableApplicationContext loadApplicationContext() {
	
		ClassLoader loader = classLoader;
		if (loader == null) {
			loader = getClass().getClassLoader();
		}
		
		ConfigurableApplicationContext parent = 
				new OddjobApplicationContext(session);
		parent.refresh();
		
		ConfigurableApplicationContext applicationContext;
		if (files != null) {
			logger.info("Creating an ApplicationContext from file(s) " +
					Arrays.toString(files));
			
			applicationContext = new FileSystemXmlApplicationContext(
					files, false, parent) {
			};
		}
		else if (resources != null) {
			logger.info("Creating an ApplicationContext from resources(s) " +
					Arrays.toString(resources));
			
			applicationContext = new ClassPathXmlApplicationContext(
					resources, false, parent);
		}
		else {
			throw new IllegalStateException("No config specified.");
		}
		
		((DefaultResourceLoader) applicationContext).setClassLoader(loader);
		
		applicationContext.addBeanFactoryPostProcessor(
				new OddjobPropertyConfigurer(session));
		
		applicationContext.refresh();
		
		return applicationContext;
	}
	
	@Override
	public String toString() {
		if (name == null) {
			return getClass().getSimpleName();
		}
		else {
			return name;
		}
	}
}
