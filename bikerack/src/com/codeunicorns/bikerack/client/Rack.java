package com.codeunicorns.bikerack.client;

import java.io.Serializable;

public class Rack implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2096054209667042718L;
	private int streetNum;
	private String streetName;
	private String streetSide;
	private String bIA;
	private String skytrain;
	private int numRacks;
	private double lat;
	private double lng;
	private Long id;
	
	public Rack() {};

	public Rack(int streetNum, String streetName, String streetSide, String skytrain,
			String bIA, int numRacks, double lat, double lng, Long id) {
		this.streetNum = streetNum;
		this.streetName = streetName;
		this.streetSide = streetSide;
		this.bIA = bIA;
		this.skytrain = skytrain;
		this.numRacks = numRacks;
		this.lat = lat;
		this.lng = lng;
		this.id = id;
	}

//	public String getName() {
//		return this.name;
//	}
//	
//	public String setName(String name) {
//		return this.name;
//	}

	public Long getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public String getbIA() {
		return bIA;
	}

	public void setbIA(String bIA) {
		this.bIA = bIA;
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

	public String getStreetSide() {
		return streetSide;
	}

	public void setStreetSide(String streetSide) {
		this.streetSide = streetSide;
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
