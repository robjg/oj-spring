package org.oddjob.spring;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

/**
 * Adapt a {@link BeanRegistry} from an {@link ArooaSession} provided by 
 * Oddjob to be a Spring {@link BeanFactory}.
 * 
 * This allows Spring to access components from Oddjob.
 * 
 * @see OddjobApplicationContext
 *  
 * @author rob
 *
 */
public class OddjobBeanFactory implements BeanFactory {

	/** The session. */
	private final ArooaSession session;

	/**
	 * Constructor.
	 * 
	 * @param session A session. Provided by Oddjob.
	 */
	public OddjobBeanFactory(ArooaSession session) {
		this.session = session;
	}
	
	/**
	 * Does the Oddjob {@link BeanRegistry} contain a bean of the given 
	 * name (id).
	 * 
	 * @param name The id of the bean in Oddjob.
	 * 
	 * @return true if it does, false if it doesn't.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#containsBean(java.lang.String)
	 */
	@Override
	public boolean containsBean(String name) {
		return session.getBeanRegistry().lookup(name) != null;
	}

	/**
	 * Get a bean of the given id for Oddjob's {@link BeanRegistry}.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * 
	 * @return The bean.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean in Oddjob
	 * with the specified id.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String)
	 */
	@Override
	public Object getBean(String name) throws BeansException {
		return getBean(name, (Class<?>) null);
	}

	/**
	 * Return the bean from Oddjob's {@link BeanRegistry} that uniquely 
	 * matches the given object type, if any.
	 * 
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * 
	 * @return an instance of the single bean matching the required type
	 * 
	 * @throws NoSuchBeanDefinitionException if there is not exactly 
	 * one matching bean found
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.Class)
	 */
	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		
		BeanDirectory registry = session.getBeanRegistry();
		
		Iterable<T> iterable = registry.getAllByType(requiredType);
		
		List<T> beans = new ArrayList<T>();
		
		for (T bean : iterable) {
			beans.add(bean);
		}
		if (beans.size() == 1) {
			return beans.get(0); 
		}
		
		throw new NoSuchBeanDefinitionException(requiredType, 
				"Not exactly one bean found.");
	}
	
	/**
	 * This is calls {@link #getBean(Class)} as only an existing bean will be 
	 * returned. The arguments for bean creation are ignored.
	 * 
	 * @param requiredType The required class.
	 * @param args Ignored.
	 */
	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return getBean(requiredType);
	}

	/**
	 * Aliases are not supported.
	 * 
	 * @param name The bean name.
	 * 
	 * @return Always an empty array.
	 */
	@Override
	public String[] getAliases(String name) {
		return new String[0];
	}

	/**
	 * Get a bean from Oddjob's {@link BeanRegistry} of the given name (id)
	 * and type. Oddjob will attempt to convert the bean to be of the
	 * given type using it's internal conversions.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * @param requiredType If specified the type of the bean. May be null. 
	 * 
	 * @return A bean from Oddjob of the given id and type if specified.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Class)
	 * 
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {

		if (requiredType == null) {
			Object object = session.getBeanRegistry().lookup(name);
			
			if (object == null) {
				throw new NoSuchBeanDefinitionException(name);
			}
			
			return (T) object;
		}
		else {		
			try {
				T object = session.getBeanRegistry().lookup(name, requiredType);
				
				if (object == null) {
					throw new NoSuchBeanDefinitionException(name);
				}
				
				return object;
			} catch (ArooaConversionException e) {
				throw new BeanNotOfRequiredTypeException(
						name, requiredType, getType(name));
			}
		}
	}

	/**
	 * This method is unsupported.
	 * 
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the type of the bean in Oddjob's {@link BeanRegistry}.
	 * 
	 * @param The id of the bean in Oddjob.
	 * 
	 * @return The type.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		Object bean = getBean(name);
		return bean.getClass();
	}

	/**
	 * Is the bean a prototype? Always false because all beans in Oddjob
	 * are singletons in Spring terms.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * 
	 * @return Always false if the bean exists.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#isPrototype(java.lang.String)
	 */
	@Override
	public boolean isPrototype(String name)
			throws NoSuchBeanDefinitionException {
		getBean(name);
		
		return false;
	}

	/**
	 * Is the bean a singleton? Always true because all beans in Oddjob
	 * are singletons in Spring terms.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * 
	 * @return Always true if the bean exists.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#isSingleton(java.lang.String)
	 */
	@Override
	public boolean isSingleton(String name)
			throws NoSuchBeanDefinitionException {
		getBean(name);
		
		return true;
	}

	/**
	 * Check the bean in Oddjob's {@link BeanRegistry} is of this
	 * type. The check will not check to see if Oddjob could provide
	 * a conversion to this type.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * @param targetType The type to check the bean is of.
	 * 
	 * @return true if the bean is of the target type.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#isTypeMatch(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean isTypeMatch(String name, Class<?> targetType)
			throws NoSuchBeanDefinitionException {
		Object bean = getBean(name);
		
		return targetType.isInstance(bean);
	}

	/**
	 * Check the bean in Oddjob's {@link BeanRegistry} is of this
	 * type. The check will not check to see if Oddjob could provide
	 * a conversion to this type.
	 * 
	 * @param name The id of the bean in Oddjob.
	 * @param typeToMatch The type to check the bean is of.
	 * 
	 * @return true if the bean is of the target type.
	 * 
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * 
	 * @see org.springframework.beans.factory.BeanFactory#isTypeMatch(String, ResolvableType)
	 */
	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		Object bean = getBean(name);
		
		return typeToMatch.isInstance(bean);
	}

}
