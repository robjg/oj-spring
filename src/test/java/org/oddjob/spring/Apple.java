package org.oddjob.spring;

public class Apple {

	private String colour;

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
	@Override
	public String toString() {
		return "" + colour + " apple";
	}
}
