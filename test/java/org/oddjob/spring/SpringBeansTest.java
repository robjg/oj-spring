package org.oddjob.spring;
import junit.framework.TestCase;

import org.oddjob.OddjobLookup;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;


public class SpringBeansTest extends TestCase {

	public void testLookup() throws ArooaConversionException {

		SpringBeans test = new SpringBeans();
		test.setArooaSession(new StandardArooaSession());
		test.setResources(new String[] { "org/oddjob/spring/spring1.xml" });
		
		test.run();
		
		Apple apple = new OddjobLookup(test).lookup("apple", Apple.class);
		
		assertNotNull(apple);
	}
	
	public void testSetProperty() throws ArooaConversionException {

		Apple red = new Apple();
		red.setColour("red");
		
		ArooaSession session = new StandardArooaSession();
		session.getBeanRegistry().register("variables", red);
		
		SpringBeans test = new SpringBeans();
		test.setArooaSession(session);
		test.setResources(new String[] { "org/oddjob/spring/spring2.xml" });
		
		test.run();
		
		Apple apple = new OddjobLookup(test).lookup("apple", Apple.class);
		
		assertEquals("red", apple.getColour());
	}
	
	public void testSetBeanRef() throws ArooaConversionException {

		Apple red = new Apple();
		red.setColour("red");
		
		ArooaSession session = new StandardArooaSession();
		session.getBeanRegistry().register("apple", red);
		
		SpringBeans test = new SpringBeans();
		test.setArooaSession(session);
		test.setResources(new String[] { "org/oddjob/spring/spring3.xml" });
		
		test.run();
		
		Snack snack = new OddjobLookup(test).lookup("snack", Snack.class);
		
		assertEquals("red", snack.getApple().getColour());
	}
}
