package com.codeunicorns.bikerack.server;

public class Rack {
	private int streetNum;
	private String streetName;
	private char streetSide;
	private String bIA;
	private String skytrain;
	private int numRacks;
	
	public Rack(int streetNum, String streetName, char streetSide, String bIA,
			String skytrain, int numRacks) {
		this.streetNum = streetNum;
		this.streetName = streetName;
		this.streetSide = streetSide;
		this.bIA = bIA;
		this.skytrain = skytrain;
		this.numRacks = numRacks;
	}

	public int getStreetNum() {
		return streetNum;
	}

	public void setStreetNum(int streetNum) {
		this.streetNum = streetNum;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public char getStreetSide() {
		return streetSide;
	}

	public void setStreetSide(char streetSide) {
		this.streetSide = streetSide;
	}

	public String getBIA() {
		return bIA;
	}

	public void setBIA(String bIA) {
		this.bIA = bIA;
	}

	public String getSkytrain() {
		return skytrain;
	}

	public void setSkytrain(String skytrain) {
		this.skytrain = skytrain;
	}

	public int getNumRacks() {
		return numRacks;
	}

	public void setNumRacks(int numRacks) {
		this.numRacks = numRacks;
	}	
}
