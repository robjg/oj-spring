package org.oddjob.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.arooa.registry.PathBreakdown;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.ParsedExpression;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StringValueResolver;

/**
 * @oddjob.description An Oddjob job that will load a Spring application. The Spring XML configuration
 * may be specified either as files or classpath resources.
 * <p>
 * Beans within Spring may be accessed from other Oddjob components using the notation
 * ${<i>id-of-this</i>/<i>name-of-bean</i>}.
 * <p>
 * Oddjob beans are made available via a parent BeanFactory so can be accessed by 
 * reference.
 * <p>
 * Oddjobs beans and there properties can also be accessed using the 
 * ${<i>id</i>.<i>property</i>} notation within Spring configuration files.
 * 
 * @oddjob.example
 * 
 * See the examples in the <code>examples/config</code> directory. To run 
 * the examples copy the <code>examples/classes</code> directory to it's parent 
 * directory so that Oddjob recognises them as part of the Oddball. 
 * <p>
 * 
 * @author rob
 *
 */
public class SpringBeans 
implements Runnable, BeanDirectoryOwner, ArooaSessionAware {
	
	private static final Logger logger = Logger.getLogger(SpringBeans.class);
	
	/** 
     * @oddjob.property 
     * @oddjob.description The name of the job. Can be any text.
     * @oddjob.required No.
	 */
	private String name;

	/** The resultant ApplicationContext. */
	private ConfigurableApplicationContext applicationContext;

	/** 
     * @oddjob.property 
     * @oddjob.description Spring XML configuration files.
     * @oddjob.required Not if resources are supplied.
	 */
	private String[] files;
	
	/** 
     * @oddjob.property 
     * @oddjob.description Spring XML configuration classpath resources.
     * @oddjob.required Not if files are supplied.
	 */
	private String[] resources;

	/** Provided by Oddjob. */
	private ArooaSession session;

	/** 
     * @oddjob.property 
     * @oddjob.description Passed to Spring for its DefaultResourceLoader.
     * If not supplies the class loader for this class will be used which
     * will be the Oddball class loader when installed as an Oddball.
     * @oddjob.required No.
	 */
	private ClassLoader classLoader;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter.
	 * 
	 * @return
	 */
	public String[] getFiles() {
		return files;
	}

	/**
	 * Setter
	 * 
	 * @param files
	 */
	public void setFiles(String[] files) {
		this.files = files;
	}

	/**
	 * Getter.
	 * 
	 * @return
	 */
	public String[] getResources() {
		return resources;
	}

	/**
	 * Setter.
	 * 
	 * @param resources
	 */
	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Set the class loader to load resource with.
	 * 
	 * @param classLoader
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.life.ArooaSessionAware#setArooaSession(org.oddjob.arooa.ArooaSession)
	 */
	public void setArooaSession(ArooaSession session) {
		this.session = session;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
	
		ClassLoader loader = classLoader;
		if (loader == null) {
			loader = getClass().getClassLoader();
		}
		
		if (files != null) {
			logger.info("Creating an ApplicationContext from file(s) " +
					Arrays.toString(files));
			
			applicationContext = new FileSystemXmlApplicationContext(
					files, false) {
				@Override
				protected void loadBeanDefinitions(
						DefaultListableBeanFactory beanFactory)
						throws IOException {
					beanFactory.setParentBeanFactory(new ArooaBeanFactory());
					super.loadBeanDefinitions(beanFactory);
				}
			};
		}
		else if (resources != null) {
			logger.info("Creating an ApplicationContext from resources(s) " +
					Arrays.toString(resources));
			
			applicationContext = new ClassPathXmlApplicationContext(
					resources, false) {
				@Override
				protected void loadBeanDefinitions(
						DefaultListableBeanFactory beanFactory)
						throws IOException {
					beanFactory.setParentBeanFactory(new ArooaBeanFactory());
					super.loadBeanDefinitions(beanFactory);
				}
			};
		}
		else {
			throw new IllegalStateException("No config specified.");
		}
		((DefaultResourceLoader) applicationContext).setClassLoader(loader);
		
		PostProcessor directoryProcessor = new PostProcessor();
		applicationContext.addBeanFactoryPostProcessor(directoryProcessor);
		
		applicationContext.refresh();
	}
	
	/**
	 * @see BeanDirectoryOwner
	 */
	public BeanDirectory provideBeanDirectory() {
		if (applicationContext == null) {
			return null;
		}
		
		return new BeanDirectory() {
		
			public String getIdFor(Object bean) {
				return null;
			}
		
			public <T> Iterable<T> getAllByType(Class<T> type) {
				Map<?, T> map = applicationContext.getBeansOfType(type);
				return map.values();
			}
			
			public Object lookup(String path) {
				PathBreakdown breakdown = new PathBreakdown(path);
				Object bean = applicationContext.getBean(breakdown.getId());
				if (bean == null) {
					return null;
				}
				if (breakdown.isNested()) {
					if (bean instanceof BeanDirectoryOwner) {
						BeanDirectory next = ((BeanDirectoryOwner) bean).provideBeanDirectory();
						if (next == null) {
							return null;
						}
						return next.lookup(breakdown.getNestedPath());
					}
					else {
						return null;
					}
				}
				else {
					if (breakdown.isProperty()) {
						PropertyAccessor propertyAccessor = session.getTools().getPropertyAccessor();
						return propertyAccessor.getProperty(bean, breakdown.getProperty()); 
					}
					else {
						return bean;
					}
				}
			}
			
			public <T> T lookup(String path, Class<T> required) 
			throws ArooaConversionException {
				PathBreakdown breakdown = new PathBreakdown(path);
				Object bean = applicationContext.getBean(breakdown.getId());
				if (bean == null) {
					return null;
				}
				if (breakdown.isNested()) {
					if (bean instanceof BeanDirectoryOwner) {
						BeanDirectory next = ((BeanDirectoryOwner) bean).provideBeanDirectory();
						if (next == null) {
							return null;
						}
						return next.lookup(breakdown.getNestedPath(), required);
					}
					else {
						return null;
					}
				}
				else {
					Object value;
					if (breakdown.isProperty()) {
						PropertyAccessor propertyAccessor = session.getTools().getPropertyAccessor();
						value = propertyAccessor.getProperty(bean, breakdown.getProperty()); 
					}
					else {
						value = bean;
					}
					ArooaConverter converter = session.getTools().getArooaConverter();
					return converter.convert(value, required);
				}
			}
		};
	}

	/**
	 * Pretend the owning ArooaSession is a parent BeanFactory so that Spring
	 * can access components in the outer Oddjob world.
	 * 
	 */
	class ArooaBeanFactory implements BeanFactory {
		
		@Override
		public boolean containsBean(String arg0) {
			return session.getBeanRegistry().lookup(arg0) != null;
		}

		@Override
		public Object getBean(String name) throws BeansException {
			return getBean(name, (Class<?>) null);
		}

		@Override
		public <T> T getBean(Class<T> arg0) throws BeansException {
			Iterable<T> iterable = session.getBeanRegistry().getAllByType(arg0);
			List<T> beans = new ArrayList<T>();
			for (T bean : iterable) {
				beans.add(bean);
			}
			if (beans.size() == 1) {
				return beans.get(0); 
			}
			
			throw new NoSuchBeanDefinitionException(arg0, 
					"Not exactly one bean found.");
		}
		
		@Override
		public String[] getAliases(String arg0) {
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object getBean(String name, Class requiredType) throws BeansException {
			Object object = session.getBeanRegistry().lookup(name);
			if (object == null) {
				throw new NoSuchBeanDefinitionException(name);
			}

			if (requiredType == null) {
				return object;
			}
			
			if (requiredType.isInstance(object)) {
				return object;
			}
			
			throw new BeanNotOfRequiredTypeException(
					name, requiredType, object.getClass());
		}

		@Override
		public Object getBean(String arg0, Object... arg1) throws BeansException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
			Object bean = getBean(name);
			return bean.getClass();
		}

		@Override
		public boolean isPrototype(String arg0)
				throws NoSuchBeanDefinitionException {
			return false;
		}

		@Override
		public boolean isSingleton(String arg0)
				throws NoSuchBeanDefinitionException {
			return true;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isTypeMatch(String arg0, Class arg1)
				throws NoSuchBeanDefinitionException {
			return false;
		}

	}

	/**
	 * BeanFactoryPostProcessor.
	 *
	 */
	class PostProcessor implements BeanFactoryPostProcessor {
		
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactoryToProcess)
				throws BeansException {
			
			BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(
					new BeanDirectoryResolver());
			
			String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
			for (int i = 0; i < beanNames.length; i++) {

				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanNames[i]);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (BeanDefinitionStoreException ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanNames[i], ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * Resolves ${} expressions as if in Oddjob. 
	 *
	 */
	class BeanDirectoryResolver implements StringValueResolver {

		private final ExpressionParser parser; 
		
		public BeanDirectoryResolver() {
			parser = session.getTools().getExpressionParser();
		}
		
		public String resolveStringValue(String arg0) {
			ParsedExpression expression = parser.parse(arg0);
			try {
				return expression.evaluateAsText(session);
			}
			catch (ArooaConversionException e) {
				throw new RuntimeException(e);
			}
		}
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
