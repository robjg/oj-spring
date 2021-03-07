package org.oddjob.spring.examples.service;

import org.junit.Test;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ServiceTest {

	@Test
	public void testSpringServiceBeingUsedInOddjob() throws FailedToStopException, ArooaConversionException {

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(getClass()
				.getResource("/examples/service/Service.oj.xml").getFile()));

		oddjob.run();

		assertThat(oddjob.lastStateEvent().getState(), is(ParentState.STARTED));

		String text = new OddjobLookup(oddjob).lookup("echo.text", String.class);

		assertThat(text, is("Count is 2"));

		int count = new OddjobLookup(oddjob).lookup("spring/counting-service.count", int.class);

		assertThat(count, is(3));

		oddjob.stop();

		assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));
	}
}
