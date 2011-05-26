package org.oddjob.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.jobs.ExecJob;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogLevel;
import org.oddjob.logging.LogListener;

public class ExamplesTest extends TestCase {

	private static final Logger logger = Logger.getLogger(ExamplesTest.class);
	
	String runJar;

	OurDirs dirs = new OurDirs();
	
	@Override
	protected void setUp() throws Exception {

		logger.info("---------------  " + getName() + "  ------------------");
		
		logger.info("Base Dir is: " + dirs);
		
		File runJar = dirs.relative("../oddjob/run-oddjob.jar");
		if (!runJar.exists()) {
			String oddjobHome = System.getProperty("oddjob.home");
			if (oddjobHome == null) {
				oddjobHome = "../oddjob";
			}
			runJar = new File(oddjobHome, "run-oddjob.jar");
		}
		if (!runJar.exists()) {
			throw new IllegalStateException("No run-ddjob.jar");
		}
		
		this.runJar = runJar.getAbsolutePath();
		
		String oddballDir = dirs.relative("work/spring").getPath();
		String examplesClasses = dirs.relative("examples/classes").getPath();
		String libs = dirs.relative("lib").getPath();

		assertTrue("Examples Built",
				new File(examplesClasses).exists());		
		
		String setup = 
			"<oddjob>" +
			" <job>" +
			"  <sequential>" +
			"   <jobs>" +
			"    <mkdir dir='" + oddballDir + "'/>" +
			"    <copy to='" + oddballDir + "'>" +
			"     <from>" +
			"      <file file='" + libs + "'/>" +
			"     </from>" +
			"    </copy>" +
			"    <copy to='" + oddballDir + "'>" +
			"     <from>" +
			"      <file file='" + examplesClasses + "'/>" +
			"     </from>" +
			"    </copy>" +
			"   </jobs>" +
			"  </sequential>" +
			" </job>" +
			"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("XML", setup));
		oddjob.run();
		
	}
	
	final static String EOL = System.getProperty("line.separator");
	
	class Console implements LogListener  {
		List<String> lines = new ArrayList<String>();
		
		public void logEvent(LogEvent logEvent) {
			lines.add(logEvent.getMessage());
		}
	}
		
	public void testOddjob1() {
		
		Console console = new Console();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + runJar + 
				" -ob " + dirs.relative("work") + 
				" -f " + dirs.relative("examples/config/oddjob1.xml").getPath());
		
		exec.consoleLog().addListener(console, LogLevel.INFO, -1, 1000);
		
		exec.run();
		
		dump(console.lines);
		
		assertEquals(0, exec.getExitValue());
		
		assertEquals(1, console.lines.size());

		assertEquals("red", console.lines.get(0).trim());
	}
	
	public void testOddjob2() {
		
		Console console = new Console();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + runJar + 
				" -ob " + dirs.relative("work") + 
				" -f " + dirs.relative("examples/config/oddjob2.xml").getPath());
		
		exec.consoleLog().addListener(console, LogLevel.INFO, -1, 1000);
		
		exec.run();
		
		dump(console.lines);
		
		assertEquals(0, exec.getExitValue());
		
		assertEquals(1, console.lines.size());

		assertEquals("blue", console.lines.get(0).trim());
	}
	
	public void testOddjob3() {
		
		Console console = new Console();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + runJar + 
				" -ob " + dirs.relative("work") + 
				" -f " + dirs.relative("examples/config/oddjob3.xml").getPath());
		
		exec.consoleLog().addListener(console, LogLevel.INFO, -1, 1000);
		
		exec.run();
		
		dump(console.lines);
		
		assertEquals(0, exec.getExitValue());
		
		assertEquals(1, console.lines.size());

		assertEquals("green", console.lines.get(0).trim());
	}
	
	public void testOddjob4() {
		
		Console console = new Console();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + runJar + 
				" -ob " + dirs.relative("work") + 
				" -f " + dirs.relative("examples/config/oddjob4.xml"));
		
		exec.consoleLog().addListener(console, LogLevel.INFO, -1, 1000);
		
		exec.run();
		
		dump(console.lines);
		
		assertEquals(0, exec.getExitValue());
		
		assertEquals(1, console.lines.size());

		assertEquals("true", console.lines.get(0).trim());
	}
	
	void dump(List<String> lines) {
		System.out.println("******************");
		for (String line : lines) {
			System.out.print(line);
		}
		System.out.println("******************");
	}
}
