package example;

public class HelloWorldBean implements Runnable {

	public int runCount;
	
	@Override
	public void run() {
		System.out.println("Hello World");
		++runCount;
	}
	
	public int getRunCount() {
		return runCount;
	}

	public String toString() {
		return getClass().getSimpleName();
	}
}
