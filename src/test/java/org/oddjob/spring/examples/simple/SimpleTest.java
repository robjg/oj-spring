package org.oddjob.spring.examples.simple;

import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.state.ParentState;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimpleTest {

	@Test
	public void testSpringServiceBeingUsedInOddjob() throws ArooaConversionException {

		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(getClass()
				.getResource("/examples/simple/Simple.oj.xml").getFile()));

		oddjob.run();

		assertThat(oddjob.lastStateEvent().getState(), is(ParentState.COMPLETE));

		int count = new OddjobLookup(oddjob).lookup("spring/hello.runCount", int.class);

		assertThat(count, is(1));


		oddjob.destroy();
	}
}
