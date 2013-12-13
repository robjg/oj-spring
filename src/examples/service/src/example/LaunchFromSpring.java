package example;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LaunchFromSpring {
	
	public static void main(String... args) {
		
		ConfigurableApplicationContext ctx = 
				new ClassPathXmlApplicationContext(
						"classpath:config/spring/Simple.xml");
		
		Runnable hello = ctx.getBean("hello", Runnable.class);
		
		hello.run();
		
		ctx.close();
	}
}
