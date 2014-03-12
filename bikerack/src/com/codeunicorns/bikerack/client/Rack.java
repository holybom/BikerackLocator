package com.codeunicorns.bikerack.client;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Rack implements Serializable {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private int streetNum;
	@Persistent
	private String streetName;
	@Persistent
	private String streetSide;
	@Persistent
	private String bIA;
	@Persistent
	private String skytrain;
	@Persistent
	private int numRacks;
	@Persistent
	private double lat;
	@Persistent
	private double lng;
	@Persistent
	private Date createDate; 

	public Rack() {
		this.createDate = new Date();
	}
	
	public Rack(int streetNum, String streetName, String streetSide, String skytrain,
			String bIA, int numRacks, double lat, double lng) {
		this.streetNum = streetNum;
		this.streetName = streetName;
		this.streetSide = streetSide;
		this.bIA = bIA;
		this.skytrain = skytrain;
		this.numRacks = numRacks;
		this.lat = lat;
		this.lng = lng;
	}

	public Long getId() {
		return this.id;
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
