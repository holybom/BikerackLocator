package com.codeunicorns.bikerack.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This loads the objects regarding App title on top, for cosmetics purpose
 * Can add a picture, etc. later
 */
public class AppTitlePanel extends VerticalPanel {
	private static AppTitlePanel panelInstance = null;
	private Label siteLabel = new Label("Bike Racks Locator");
	
	public static AppTitlePanel getInstance() {
		if (panelInstance == null) panelInstance = new AppTitlePanel();
		return panelInstance;
	}
	
	private AppTitlePanel() {
		this.add(siteLabel);
	}

}
