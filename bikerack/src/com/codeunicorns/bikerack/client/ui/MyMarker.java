package com.codeunicorns.bikerack.client.ui;

import com.codeunicorns.bikerack.client.Rack;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;

public class MyMarker {
	private boolean isFavorite = false;
	private Marker marker;
	private Rack rack;
	
	public MyMarker(MarkerOptions markerOpts, Rack rack) {
		marker = Marker.create(markerOpts);
		this.rack = rack;
	}

	public Marker getMarker() {
		return marker;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	
	public Rack getRack() {
		return rack;
	}

	public void setRack(Rack rack) {
		this.rack = rack;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
}
