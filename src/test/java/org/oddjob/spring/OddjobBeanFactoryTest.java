package org.oddjob.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class OddjobBeanFactoryTest {
	
	@Test
	public void testContains() {
		
		String bean = new String("A");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
				
		assertEquals(true, test.containsBean("my-bean"));
		assertEquals(false, test.containsBean("another-bean"));

	}
	
	@Test
	public void testGetByType() {
		
		String bean = new String("A");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
				
		assertEquals(bean, test.getBean(String.class));		
		assertEquals(bean, test.getBean(CharSequence.class));

		try {
			test.getBean(Integer.class);
			fail("Shouldn't be a bean of this type.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}
		
		session.getBeanRegistry().register("another-bean", new String("B"));
		
		try {
			test.getBean(String.class);		
			fail("There should now be two beans of this type.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}
	}
	
	@Test
	public void testGetBeanByName() {
		
		String bean = new String("A");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
		
		assertEquals(bean, test.getBean("my-bean"));
		
		try {
			test.getBean("another-bean");		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}
	}
	
	@Test
	public void testBeanByNameAndType() {
		
		String bean = new String("42");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
		
		assertEquals(bean, test.getBean("my-bean", (Class<?>) null));
		assertEquals(bean, test.getBean("my-bean", String.class));
		assertEquals(bean, test.getBean("my-bean", CharSequence.class));
		
		try {
			test.getBean("another-bean", String.class);		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}
		
		assertEquals(new Integer(42), test.getBean("my-bean", Integer.class));
		
		try {
			test.getBean("my-bean", Date.class);		
			fail("Bean isn't of this type.");
		}
		catch (BeanNotOfRequiredTypeException e) { 
			// expected.
		}
	}
	
	public void testGetType() {
		
		String bean = new String("A");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
		
		assertEquals(String.class, test.getType("my-bean"));
		
		try {
			test.getType("another-bean");		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}		
	}
	
	public void testIsPrototypeOrSingleton() {
		
		String bean = new String("A");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
		
		assertEquals(true, test.isSingleton("my-bean"));
		assertEquals(false, test.isPrototype("my-bean"));
		
		try {
			test.isSingleton("another-bean");		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}		
		
		try {
			test.isPrototype("another-bean");		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}		
	}
	
	public void testIsTypeMatch() {
		
		String bean = new String("42");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobBeanFactory test = new OddjobBeanFactory(session);
		
		assertEquals(true, test.isTypeMatch("my-bean", String.class));
		assertEquals(true, test.isTypeMatch("my-bean", CharSequence.class));
		assertEquals(false, test.isTypeMatch("my-bean", Integer.class));
		
		try {
			test.isTypeMatch("another-bean", Object.class);		
			fail("There shouldn't be this bean.");
		}
		catch (NoSuchBeanDefinitionException e) { 
			// expected.
		}		
	}

}
