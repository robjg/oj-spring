package org.oddjob.spring;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.input.InputHandler;
import org.oddjob.input.InputRequest;
import org.oddjob.state.ParentState;

public class SpringServiceTest {

	@Test
	public void testMyQuery() {
		
		InputHandler inputHandler = new InputHandler() {
			
			@Override
			public Properties handleInput(InputRequest[] requests) {
				Properties properties = new Properties();
				properties.setProperty("jdbc.username", "sa");
				properties.setProperty("jdbc.password", "");
				return properties;
			}
		};
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				"org/oddjob/spring/SpringServiceExample.xml",
				getClass().getClassLoader()));
		oddjob.setInputHandler(inputHandler);
		
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
	}
}
