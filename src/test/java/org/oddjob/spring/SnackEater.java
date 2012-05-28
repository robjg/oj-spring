package org.oddjob.spring;

public class SnackEater implements Runnable {

	private Apple apple;

	private Biscuit biscuit;
	
	private String state = "empty";

	public Apple getApple() {
		return apple;
	}

	public void setApple(Apple apple) {
		this.apple = apple;
	}
	
	public Biscuit getBiscuit() {
		return biscuit;
	}
	
	public void setBiscuit(Biscuit buscuit) {
		this.biscuit = buscuit;
	}
	
	public void run() {
		System.out.println("I'm eating a " + apple + " and a " + biscuit);
		state = "full";
	}
	
	public String getState() {
		return state;
	}	
}
