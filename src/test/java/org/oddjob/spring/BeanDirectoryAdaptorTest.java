package org.oddjob.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanDirectoryAdaptorTest {

	public static class MyBean {
		
		public int getMyInteger() {
			return 42;
		}
		
		public String getMyString() {
			return "Hello";
		}
		
		public String getMyNumberAsString() {
			return "42";
		}
	}
	
	@Test
	public void testLookups() throws ArooaConversionException {
		
		ConfigurableApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext(
				"org/oddjob/spring/BeanDirectoryAdaptorTest.spg.xml");
		
		ArooaSession session = new StandardArooaSession();
		
		BeanDirectoryAdaptor test = new BeanDirectoryAdaptor(
				applicationContext, session);
		
		Object object = test.lookup("my-bean");

		assertEquals(true, object instanceof MyBean);
		
		MyBean myBean = null;
		
		Iterable<MyBean> iterable = test.getAllByType(MyBean.class);
		
		for (MyBean b : iterable) {
			if (myBean == null) {
				myBean = b;
			}
			else {
				fail("Only one expected.");
			}
		}
		
		assertEquals(myBean, object);
		
		assertEquals(myBean, test.lookup("my-bean", MyBean.class));
		
		assertEquals(42, test.lookup("my-bean.myInteger"));
		assertEquals("Hello", test.lookup("my-bean.myString"));
		
		assertEquals(new Integer(42), 
				test.lookup("my-bean.myNumberAsString", int.class));
	}
}
