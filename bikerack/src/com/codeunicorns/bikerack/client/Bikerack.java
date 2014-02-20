package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.codeunicorns.bikerack.client.LoginInfo;
import com.codeunicorns.bikerack.client.LoginService;
import com.codeunicorns.bikerack.client.LoginServiceAsync;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.Cookies;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Bikerack implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	//private VerticalPanel mainPanel = new VerticalPanel();
	private final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
	private HorizontalPanel loggedInPanel = new HorizontalPanel();
	private HorizontalPanel notLoggedInPanel = new HorizontalPanel();
	private VerticalPanel logInStatusPanel = new VerticalPanel();
	private Label siteLabel = new Label("Bike Racks Locator");
	private Label loginLabel = new Label(
			"Sign in or create an account to save your favorites."); 
	private Label loginLabel2 = new Label("or");
	private PushButton loginButton = new PushButton("Sign in");
	private PushButton signupButton = new PushButton("Register");
	private PushButton logoutButton = new PushButton("Log out");
	private LoginInfo loginInfo = null;
	private VerticalPanel userPanel = new VerticalPanel();
	private VerticalPanel profilePanel = new VerticalPanel();
	private VerticalPanel favoritePanel = new VerticalPanel();
	private VerticalPanel rackPanel = new VerticalPanel();
	private Label welcomeLabel = new Label("Welcome!");
	
	/**
	 * This is the entry point method. Where everything starts
	 */
	public void onModuleLoad() {
		// load app title on top
		loadAppTitle();
		// load login prompt with signin and register button, or welcome message, at bottom 
		loadLoginStatusPanel();
		getLoginInfo();
		setLoginStatusPanel(loginInfo);
		// load pane with list of bike racks and search results on left side
		loadRackPanel();
		// load user login/register form, user profile, favorites and logout button on the right side
		loadUserPanel();
		// Lastly, load the google map at the center of the screen
		Maps.loadMapsApi("", "2", false, new Runnable() {
		      public void run() {
		        loadMapView();
		      }
		    });
		// Last step is add the entire thing to HTML host page
		RootLayoutPanel.get().add(mainPanel);
	}

	/**
	 * This loads the objects regarding App title on top, for cosmetics purpose
	 * Can add a picture, etc. later
	 */
	private void loadAppTitle() {
		mainPanel.addNorth(siteLabel, 100);
	}
	
	/**
	 * This loads the line right below the map
	 * Both "Welcome!" label and signout button, and "Signin or Register to view...." label
	 * (with signup and signin buttons next to it) are loaded, but both are set to invisible
	 * on top of each other 
	 */	
	private void loadLoginStatusPanel() {
		welcomeLabel = new Label("Welcome!");
		loggedInPanel.add(welcomeLabel);
		loggedInPanel.add(logoutButton);
	  
		notLoggedInPanel.add(loginLabel);
		notLoggedInPanel.add(loginButton);
		notLoggedInPanel.add(loginLabel2);
		notLoggedInPanel.add(signupButton);
		
		logInStatusPanel.add(loggedInPanel);
		logInStatusPanel.add(notLoggedInPanel);
		
		mainPanel.addSouth(logInStatusPanel, 100);
		
		// Listen for mouse events on the Login, logout and signup button.
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO: change argument to login to pass info from login form
				login(null);
			}
		});
		logoutButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				logout();
			}
		});		
		signupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			// TODO: implements signup button
			}
		});
	}

	/**
	 * Everything about the user including login form, user profile, favorites, etc.
	 * on the right side of the map (east of mainPanel)
	 * NOTE : uses TabLayout for all of these different pages/functionalities
	 */
	private void loadUserPanel() {
		// TODO Auto-generated method stub
		
		
	}
	
	/**
	 * Simple rack list or table view on the left side of the map (west of mainPanel),  
	 * NOTE: uses TabLayout for future implementation of Searching
	 */
	private void loadRackPanel() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * And finally, this loads the map at the center
	 */
	private void loadMapView() {
		 buildMapView();
	}
	
	/**
	 * basically does what loadMapView does for now, split because might need to add
	 * intense markers making and loading later in this function
	 */
	private void buildMapView() {
	    // Open a map centered on Vancouver, Canada
	    LatLng vancouverCity = LatLng.newInstance(49.247,-123.114);
	    final MapWidget map = new MapWidget(vancouverCity, 2);
	    map.setSize("50%", "75%");
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());
	    map.setZoomLevel(11);
	    // Add a marker
	    map.addOverlay(new Marker(vancouverCity));
	    // Add an info window to highlight a point of interest
	    map.getInfoWindow().open(map.getCenter(),
	        new InfoWindowContent("World's best city"));	   
	    mainPanel.add(map);
	  }
	
	/**
	 * this function determines which one of the loginStatusPanels will be visible,
	 * the notLoggedIn or the loggedIn. 
	 * Depending on the loginInfo passed to it
	 * @param loginInfo if null then user is not logged in, if not null then user is logged in
	 */
	private void setLoginStatusPanel(LoginInfo loginInfo) {
		if (loginInfo != null) {
			notLoggedInPanel.setVisible(false);
			loggedInPanel.setVisible(true);
		}
		else {
			notLoggedInPanel.setVisible(true);
			loggedInPanel.setVisible(false);
		}
	}
	
	/*
	 * Send login Info to be checked by the server, if server sends a non-null login info, then
	 * set the cookie and set status of client to be logged in with that loginInfo. Server sents back
	 * a null if info in login form is not a valid user
	 * @param is an array of string containing user and password (simple implementation for now) 
	 * to be sent to server for authentication
	 */
	protected void login(String[] loginRequest) {
	// TODO: change argument to getlogininfo to link with form filled in after clicking signup button
	//   TODO: haven't implemented the server side stuff so no login check for now, 
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(loginRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}

					public void onSuccess(LoginInfo result) {
							loginInfo = result;
							if (loginInfo != null) {
								// set cookie
								Cookies.setCookie("bikeracklocator", loginInfo.getNickname());
								}
							setLoginStatusPanel(loginInfo);
						}
				});
	}
	
	/*
	 * logout process, remove cookie from browser and set client login status to loggedout
	 */
	protected void logout() {
		Cookies.removeCookie("bikeracklocator");
		setLoginStatusPanel(loginInfo = null);
	}
	
	/**
	 * Retrieve login info from browser by mean of cookie 
	 */
	private void getLoginInfo() {
		String cookieVal = Cookies.getCookie("bikeracklocator");
		if (cookieVal == null) {
		loginInfo = null;
		}
		else {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setNickname(cookieVal);
			this.loginInfo = loginInfo;
		}
	}
		
	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
	}
}

