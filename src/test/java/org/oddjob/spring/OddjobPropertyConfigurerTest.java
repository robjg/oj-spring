package org.oddjob.spring;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardPropertyLookup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OddjobPropertyConfigurerTest {

	public static class ResultBean {
		
		private List<String> values;
		
		public void setValues(List<String> values) {
			this.values = values;
		}
	}
	
	@Test
	public void testLotsOfExpressions() throws ArooaConversionException {
		
		ArooaSession session = new StandardArooaSession();
		
		Properties props = new Properties();
		
		props.setProperty("favourite.fruit", "apples");
		props.setProperty("favourite.snack", "favourite.fruit");
		
		StandardPropertyLookup lookup = 
				new StandardPropertyLookup(props, "TEST");
		
		session.getPropertyManager().addPropertyLookup(lookup);
				
		ConfigurableApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext(
				"org/oddjob/spring/OddjobPropertyConfigurerTest.spg.xml");
	
		applicationContext.addBeanFactoryPostProcessor(
				new OddjobPropertyConfigurer(session));
	
		applicationContext.refresh();

		ResultBean results = applicationContext.getBean(
				"results", ResultBean.class);
		
		System.out.println(results.values.get(0));
		System.out.println(results.values.get(1));
		System.out.println(results.values.get(2));
		System.out.println(results.values.get(3));
		System.out.println(results.values.get(4));
		
		assertEquals("My ${favourite.fruit} is apples.", 
				results.values.get(0));
		
		
		assertEquals("My ${${favourite.snack}} is also ${favourite.fruit} which is apples.", 
				results.values.get(1));
		
		assertEquals("There's no ${favourite.pizza}.", 
				results.values.get(2));
		
		assertEquals("$${favourite.pizza} still has two $$s.", 
				results.values.get(3));
		
		assertEquals("But ${favourite.pizza} with ${favourite.pizza} looses one $.", 
				results.values.get(4));
	}
	
	@Test
	public void testCustomConfiguration() {
	
		ArooaSession session = new StandardArooaSession();
		
		Properties properties = new Properties();
		properties.setProperty("favourite.fruit", "an apple");
		
		StandardPropertyLookup lookup = 
				new StandardPropertyLookup(properties, "TEST");
		
		session.getPropertyManager().addPropertyLookup(lookup);
		
		ConfigurableApplicationContext context = 
			new ClassPathXmlApplicationContext(
				new String[] {
					"org/oddjob/spring/OddjobPropertyConfigurerTest1.spg.xml"},
				false);

		context.addBeanFactoryPostProcessor(
				new OddjobPropertyConfigurer(session));
		context.refresh();
		
		SomeBean results = context.getBean(SomeBean.class);
		
		assertEquals("My favourite fruit is an apple", 
				results.one);
		
		assertEquals("My favourite colour is ${favourite.colour}", 
				results.two);
		
		context.close();
	}
	
	@Test
	public void testCustomConfigurationAndSprings() {
		
		ArooaSession session = new StandardArooaSession();
		
		Properties properties = new Properties();
		properties.setProperty("favourite.fruit", "an apple");
		
		StandardPropertyLookup lookup = 
				new StandardPropertyLookup(properties, "TEST");
		
		session.getPropertyManager().addPropertyLookup(lookup);
		
		ConfigurableApplicationContext context = 
			new ClassPathXmlApplicationContext(
				new String[] {
					"org/oddjob/spring/OddjobPropertyConfigurerTest2.spg.xml"},
				false);

		context.addBeanFactoryPostProcessor(
				new OddjobPropertyConfigurer(session));
		context.refresh();
		
		SomeBean results = context.getBean(SomeBean.class);
		
		assertEquals("My favourite fruit is an apple", 
				results.one);
		
		assertEquals("My favourite colour is blue", 
				results.two);
		
		context.close();
	}
	
	@Test
	public void testCustomConfigurationSelectedProperties() {
		
		ArooaSession session = new StandardArooaSession();
		
		Properties properties = new Properties();
		properties.setProperty("selector.choice", "Test");
		
		StandardPropertyLookup lookup = 
				new StandardPropertyLookup(properties, "TEST");
		
		session.getPropertyManager().addPropertyLookup(lookup);
		
		ConfigurableApplicationContext context = 
			new ClassPathXmlApplicationContext(
				new String[] {
					"org/oddjob/spring/OddjobPropertyConfigurerTest3.spg.xml"},
				false);

		context.addBeanFactoryPostProcessor(
				new OddjobPropertyConfigurer(session));
		context.refresh();
		
		SomeBean results = context.getBean(SomeBean.class);
		
		assertEquals("My favourite fruit is an orange", 
				results.one);
		
		assertEquals("My favourite colour is blue", 
				results.two);
		
		context.close();
	}
	
	public static class SomeBean  {
		
		private String one;
		
		private String two;
		
		public void setOne(String one) {
			this.one = one;
		}

		public void setTwo(String two) {
			this.two = two;
		}
	}	
}
