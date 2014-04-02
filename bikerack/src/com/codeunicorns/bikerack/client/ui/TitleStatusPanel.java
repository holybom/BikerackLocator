package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gwtfb.sdk.AppImageBundle;

/**
 * This loads the objects regarding App title on top, for cosmetics purpose
 * Can add a picture, etc. later
 */
public class TitleStatusPanel extends HorizontalPanel {
	private static TitleStatusPanel panelInstance = null;
	//private Widget siteLogo = new HTML("<img src='images/logo.png'/>");
	private Label welcomeLabel = new Label("");
	private SimplePanel logoPanel = new SimplePanel();
	private HorizontalPanel loggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel notLoggedInLabelPanel = new HorizontalPanel();
	private Label loginLabel = new Label("Sign in or create an account to save your favorites.");
	
	public static TitleStatusPanel getInstance() {
		if (panelInstance == null) panelInstance = new TitleStatusPanel();
		return panelInstance;
	}
	
	private TitleStatusPanel() {
		// create logo panel with the site's image logo
		AppImageBundle aib = GWT.create(AppImageBundle.class);
		logoPanel.add(new Image (aib.logo()));
 		// What displayed when user is logged in
 		loggedInLabelPanel.add(welcomeLabel);
 		// What displayed when user isn't logged in
 		notLoggedInLabelPanel.add(loginLabel);
 		// Put everything together
 		this.add(logoPanel);
 		this.add(loggedInLabelPanel);
 		this.add(notLoggedInLabelPanel);
	}
	
	Label getWelcomeLabel() {
		return welcomeLabel;
	}

	void setWelcomeLabel(String text) {
		welcomeLabel.setText(text);
	}

	HorizontalPanel getLoggedInLabelPanel() {
		return loggedInLabelPanel;
	}

	HorizontalPanel getNotLoggedInLabelPanel() {
		return notLoggedInLabelPanel;
	}

	Label getLoginLabel() {
		return loginLabel;
	}
}
