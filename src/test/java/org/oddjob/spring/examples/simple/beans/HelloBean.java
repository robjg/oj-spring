package org.oddjob.spring.examples.simple.beans;

import java.util.Optional;

public class HelloBean implements Runnable {

	private int runCount;

	private String who;

	@Override
	public void run() {
		String who = Optional.ofNullable(this.who).orElse("World");
		System.out.println("Hello " + who);
		++runCount;
	}

	public int getRunCount() {
		return runCount;
	}

	public String toString() {
		return getClass().getSimpleName();
	}
}
