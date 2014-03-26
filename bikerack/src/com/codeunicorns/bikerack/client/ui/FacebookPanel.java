package com.codeunicorns.bikerack.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This loads the welcome line below the map
 * Both "Welcome!" label and login label are loaded, but both are set to invisible
 * on top of each other 
 */	
public class FacebookPanel extends ScrollPanel {
	private static FacebookPanel panelInstance = null;
	private HorizontalPanel fbPanel = new HorizontalPanel();
	private Widget fbLikeButton = new HTML(
			"<div class='fb-like' data-href='http://0-dot-bikerack-codeunicorns.appspot.com/'" 
			+ "data-layout='standard' data-action='like' data-show-faces='true' data-share='true'></div>");
	private Widget fbCommentBox = new HTML(
			"<div class='fb-comments' data-href='http://0-dot-bikerack-codeunicorns.appspot.com/'"
			+ "data-numposts='5' data-colorscheme='light'></div>");
	private Widget fbLoginButton = new HTML(
			"<div class='fb-login-button' data-max-rows='1'" 
			+ "data-size='medium' data-show-faces='false'" 
			+ "data-auto-logout-link='true'></div>");
	
	public static FacebookPanel getInstance() {
		if (panelInstance == null) panelInstance = new FacebookPanel();
		return panelInstance;
	}
	
	private FacebookPanel() {
		fbPanel.add(fbLoginButton);
		fbPanel.add(fbLikeButton);
		this.add(fbPanel);
		this.add(fbCommentBox);
	}
}
