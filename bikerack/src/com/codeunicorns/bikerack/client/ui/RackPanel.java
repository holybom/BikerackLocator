package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Simple rack list or table view on the left side of the map (west of mainPanel),  
 * NOTE: uses TabLayout for future implementation of Searching
 */
public class RackPanel extends VerticalPanel {
	private static RackPanel panelInstance = new RackPanel();
	private Label rackPanelLabel = new Label("BIKE RACKS");	
	
	public static RackPanel getInstance() {
		if (panelInstance == null) panelInstance = new RackPanel();
		return panelInstance;
	}
	
	private RackPanel() {
		// TODO Implement rack list display, only label for now
		this.add(rackPanelLabel);
	};
}