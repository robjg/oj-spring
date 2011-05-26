package org.oddjob.spring;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.xml.XMLConfiguration;

public class SpringBeansOddjobTest extends TestCase {

	String antDir;
	
	@Override
	protected void setUp() throws Exception {
				
		String antFile = System.getProperty("ant.file");
		if (antFile != null) {
			antDir = new File(antFile).getParent() + File.separator;
		}
		if (antDir == null) {
			antDir = "";
		}
	}
	
	public void testInOddjob() throws ArooaConversionException {
		
		String xml = 
			"<oddjob xmlns:spring='http://rgordon.co.uk/spring'>" +
			" <job>" +
			"  <sequential>" +
			"   <jobs>" +
			"    <spring:beans id='spring'>" +
			"     <files>" +
			"      <list>" +
			"       <values>" +
			"        <value value='file:" + antDir + "examples/config/spring4.xml'/>" +
			"       </values>" +
			"      </list>" +
			"     </files>" +
			"    </spring:beans>" +
			"    <run job='${spring/runnable}'/>" +
			"   </jobs>" +
			"  </sequential>" +
			" </job>" +
			"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("XML", xml));
		
		oddjob.run();
		
		SomeRunnable result = new OddjobLookup(oddjob).lookup(
				"spring/runnable", SomeRunnable.class);
		
		assertEquals(true, result.isRan());
	}
	
}
