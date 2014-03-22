package com.codeunicorns.bikerack.client.ui;

import com.codeunicorns.bikerack.client.Bikerack;
import com.codeunicorns.bikerack.client.LoginInfo;
import com.codeunicorns.bikerack.client.Rack;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UIController {
	
	private static UIController uiInstance = null;
	private final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
	private AppTitlePanel appTitlePanel;
	private StatusPanel statusPanel;
	private UserPanel userPanel;
	private RackPanel rackPanel;
	private DataMappingPanel dataMappingPanel;
	private Bikerack main;
	
	public static UIController getInstance(Bikerack main) {
		if (uiInstance == null) uiInstance = new UIController(main);
		return uiInstance;
	}
	
	private UIController() {};
	/**
	 * Main Panel is implemented as a Dock Panel, in which 5 widgets will be added North, South, East, West
	 * and Center of the Dock. Widgets have to and will be added in the edges first and last one in center.
	 * 			 			  Title (N)
	 * 							I
	 * 							I  
	 * Rack/Search Panel-----Map API (C) -----User/Profile/Favorite Panel
	 * 		(W)					I                  (E)
	 * 							I
	 * 			    Welcome Label/Signout Button (S)	
	 */
	private UIController(Bikerack main) {
		this.main = main;
		// load app title on top
		appTitlePanel = AppTitlePanel.getInstance();
		mainPanel.addNorth(appTitlePanel, 100);
		// load login prompt or welcome message and logout, at bottom 
		statusPanel = StatusPanel.getInstance();
		mainPanel.addSouth(statusPanel, 100);
		// load pane with list of bike racks and search results on left side
		rackPanel = RackPanel.getInstance();
		mainPanel.addWest(rackPanel, 250);
		// load user login/register form, user profile, favorites and maybe logout button on the right side
		userPanel = UserPanel.getInstance(this);
		mainPanel.addEast(userPanel, 250);
		// Lastly, load the google map at the center of the screen
		dataMappingPanel = DataMappingPanel.getInstance(this);
		mainPanel.add(dataMappingPanel);
		// Last step is add the entire thing to HTML host page
		RootLayoutPanel.get().add(mainPanel);
	}
	
	/**
	 *             request                     request
	 * UI Objects ---------->  UIController -------------->   Bikerack          
	 */
	
	/**
	 * Transfer server request from the UI through the main (bikerack) class
	 * @param name name of the request (login, logout, register, import data, load data, set url)
	 * @param request contents of the request
	 */
	void clientRequest(String name, String[] request) {
		main.clientRequest(name, request);
	}
	
	/**
	 * Transfer direct Bikerack class's data request from the UI
	 * @param name which data that the UI wants
	 * @return the requested data
	 */
	Object dataRequest(String name) {
		return main.dataRequest(name);
	}
	
	/**
	 *             request                     request
	 * Bikerack  ----------->  UIController ------------->   UI Objects          
	 */
	
	/**
	 * This function determines which one of the loginStatusPanels will be visible,
	 * the notLoggedIn or the loggedIn. 
	 * Depending on the loginInfo passed to it
	 * @param isLoggedIn 
	 * @param loginInfo if null then user is not logged in, if not null then user is logged in
	 */
	public void setLoginStatus(boolean isLoggedIn, LoginInfo loginInfo) {
		if (isLoggedIn) {
			// set welcome message
			statusPanel.setWelcomeLabel("Welcome back " + loginInfo.getNickname() + "!");
			// select the favorites tab to display first
			//accountInfoPanel.selectTab(favoritePanel);
			// hide the account login/register and login prompt panels, 
			// show welcome label and account info panels
			
			statusPanel.getNotLoggedInLabelPanel().setVisible(false);
			statusPanel.getLoggedInLabelPanel().setVisible(true);
			userPanel.setWidgetVisible(userPanel.getAccountAccessPanel(), false);
			userPanel.setWidgetVisible(userPanel.getAccountInfoPanel(), true);
			if (loginInfo != null && loginInfo.isAdmin()) dataMappingPanel.add(dataMappingPanel.getImportPanel());
			else dataMappingPanel.remove(dataMappingPanel.getImportPanel());
		}
		else {
			// redisplay account/register and login prompt panels,
			// and hide welcome label and account info panels
			statusPanel.getNotLoggedInLabelPanel().setVisible(true);
			statusPanel.getLoggedInLabelPanel().setVisible(false);
			userPanel.setWidgetVisible(userPanel.getAccountAccessPanel(), true);
			userPanel.setWidgetVisible(userPanel.getAccountInfoPanel(), false);
			dataMappingPanel.remove(dataMappingPanel.getImportPanel());
			if (loginInfo != null && loginInfo.isAdmin()) dataMappingPanel.add(dataMappingPanel.getImportPanel());
			dataMappingPanel.remove(dataMappingPanel.getImportPanel());
		}
	}
	
	public void rebuildTableView(Rack[] racks) {
		dataMappingPanel.rebuildTableView(racks);
	}

	public void importSuccessful(boolean isSuccessful) {
		dataMappingPanel.setURLButton(true);
		dataMappingPanel.setImportButton(true);
		dataMappingPanel.setLoadButton(isSuccessful);
	}

	public void drawBikeracks(Rack[] racks) {
		dataMappingPanel.drawBikeracks(racks);
	}
	
	public void setURLBox(String result) {
		dataMappingPanel.setURLTextBox(result);
	}
}
