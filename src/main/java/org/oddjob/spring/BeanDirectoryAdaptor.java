package org.oddjob.spring;

import java.util.Map;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.arooa.registry.BeanDirectoryOwner;
import org.oddjob.arooa.registry.PathBreakdown;
import org.springframework.context.ApplicationContext;

/**
 * Adapts a Spring Application Context to be an Oddjob 
 * {@link BeanDirectory}.
 * <p>
 * This allows Oddjob components to access beans and properties within
 * Spring using the convention ${oddjob-spring-component/spring-bean}
 * to access a bean in spring.
 * 
 * @See SpringBeans
 * 
 * @author rob
 *
 */
public class BeanDirectoryAdaptor implements BeanDirectory {
	
	/** The Spring Application Context. */
	private final ApplicationContext applicationContext;
	
	/** The session. needed for the property accessor. */
	private final ArooaSession session;
	
	/**
	 * Constructor.
	 * 
	 * @param applicationContext The Spring Application Context.
	 * @param session The Oddjob Arooa Session.
	 */
	public BeanDirectoryAdaptor(ApplicationContext applicationContext,
			ArooaSession session) {
		if (applicationContext == null) {
			throw new NullPointerException("No ApplicationContext.");
		}
		if (session == null) {
			throw new NullPointerException("No ArooaSession");
		}
		
		this.applicationContext = applicationContext;
		this.session = session;
	}
	
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
}
