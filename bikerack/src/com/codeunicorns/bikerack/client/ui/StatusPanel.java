package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This loads the welcome line below the map
 * Both "Welcome!" label and login label are loaded, but both are set to invisible
 * on top of each other 
 */	
public class StatusPanel extends VerticalPanel {
	private static StatusPanel panelInstance = null;
	private Label welcomeLabel = new Label("");
	private HorizontalPanel loggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel notLoggedInLabelPanel = new HorizontalPanel();
	private Label loginLabel = new Label("Sign in or create an account to save your favorites.");

	
	public static StatusPanel getInstance() {
		if (panelInstance == null) panelInstance = new StatusPanel();
		return panelInstance;
	}
	
	private StatusPanel() {
		// Initialize the welcome label, modify according to User's Display name at Login
		welcomeLabel = new Label("");
		// What displayed when user is logged in
		loggedInLabelPanel.add(welcomeLabel);
		// What displayed when user isn't logged in
		notLoggedInLabelPanel.add(loginLabel);
		// Put everything together
		this.add(loggedInLabelPanel);
		this.add(notLoggedInLabelPanel);
	}

	 Label getWelcomeLabel() {
		return welcomeLabel;
	}

	public void setWelcomeLabel(String text) {
		welcomeLabel.setText(text);
	}

	public HorizontalPanel getLoggedInLabelPanel() {
		return loggedInLabelPanel;
	}

	public HorizontalPanel getNotLoggedInLabelPanel() {
		return notLoggedInLabelPanel;
	}

	public Label getLoginLabel() {
		return loginLabel;
	}

}
