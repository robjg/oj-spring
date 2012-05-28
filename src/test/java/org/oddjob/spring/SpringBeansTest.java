package org.oddjob.spring;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.xml.XMLConfiguration;


public class SpringBeansTest {

	@Test
	public void testExampleInOddjob() throws ArooaConversionException {

		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(
				new XMLConfiguration("org/oddjob/spring/SpringBeansTest.oj.xml",
						getClass().getClassLoader()));
		oddjob.run();
		
		assertEquals("The snack eater is full", 
				new OddjobLookup(oddjob).lookup("echo.text"));
		
	}
	
}
