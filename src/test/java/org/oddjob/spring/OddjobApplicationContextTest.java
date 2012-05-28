package org.oddjob.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OddjobApplicationContextTest {

	public static class SomeBean  {
		
		private String one;
		
		private int two;
		
		public void setOne(String one) {
			this.one = one;
		}

		public void setTwo(int two) {
			this.two = two;
		}
	}	
	
	@Test
	public void testBeanMethods() {

		String bean = new String("42");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("my-bean", bean);
		
		OddjobApplicationContext test = new OddjobApplicationContext(session);
		test.refresh();
		
		ConfigurableApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext(new String[]
						{ "org/oddjob/spring/OddjobApplicationContextTest.spg.xml" }, 
						false, test);

		applicationContext.refresh();
		
		SomeBean result = applicationContext.getBean(
				"some-bean", SomeBean.class);
		
		assertEquals("42", result.one);
		assertEquals(42, result.two);
	}
}
