package org.oddjob.spring;

public class SomeRunnable implements Runnable {

	boolean ran;

	public boolean isRan() {
		return ran;
	}
	
	public void run() {
		ran = true;
	}
}
