package com.codeunicorns.bikerack.client;

import com.codeunicorns.bikerack.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.codeunicorns.bikerack.client.LoginInfo;
import com.codeunicorns.bikerack.client.AccountService;
import com.codeunicorns.bikerack.client.AccountServiceAsync;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
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
	private TabLayoutPanel accountAccessPanel = new TabLayoutPanel(20, Unit.PX);
	private TabLayoutPanel accountInfoPanel = new TabLayoutPanel(20, Unit.PX);
	private LayoutPanel userPanel = new LayoutPanel();
	private VerticalPanel logInStatusPanel = new VerticalPanel();
	private VerticalPanel profilePanel = new VerticalPanel();
	private VerticalPanel favoritePanel = new VerticalPanel();
	private VerticalPanel rackPanel = new VerticalPanel();
	private VerticalPanel loginPanel = new VerticalPanel();
	private VerticalPanel registerPanel = new VerticalPanel();
	private HorizontalPanel loggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel notLoggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel userNamePanel = new HorizontalPanel();
	private HorizontalPanel passwordPanel = new HorizontalPanel();
	private HorizontalPanel userNamePanel2 = new HorizontalPanel();
	private HorizontalPanel passwordPanel2 = new HorizontalPanel();
	private HorizontalPanel emailPanel = new HorizontalPanel();
	private HorizontalPanel nickNamePanel = new HorizontalPanel();
	private HorizontalPanel registerFormButtonsPanel = new HorizontalPanel();
	private PushButton loginButton = new PushButton("Sign in");
	private PushButton signupButton = new PushButton("Register");
	private PushButton logoutButton = new PushButton("Log out");
	private PushButton clearButton = new PushButton("Clear");
	private Label siteLabel = new Label("Bike Racks Locator");
	private Label loginLabel = new Label(
			"Sign in or create an account to save your favorites.");
	private Label welcomeLabel = new Label("");
	private Label loginTitleLabel = new Label("ACCOUNT LOGIN");
	private Label registerTitleLabel = new Label("REGISTER NEW ACCOUNT");
	private Label favoritePanelLabel = new Label("FAVORITES");
	private Label profilePanelLabel = new Label("PROFILE");
	private Label userNameLabel = new Label("Username");
	private Label passwordLabel = new Label("Password");
	private Label userNameLabel2 = new Label("Username");
	private Label passwordLabel2 = new Label("Password");
	private Label nickNameLabel = new Label("Display name");
	private Label emailLabel = new Label("Email");
	private TextBox userNameTextbox = new TextBox();
	private TextBox userNameTextbox2 = new TextBox();
	private TextBox emailTextbox = new TextBox();
	private TextBox nickNameTextbox = new TextBox();
	private PasswordTextBox passwordTextbox = new PasswordTextBox();
	private PasswordTextBox passwordTextbox2 = new PasswordTextBox();
	private LoginInfo loginInfo = null;
	private boolean isLoggedIn = false;
	
	class MyClickEvent extends ClickEvent {};
	/**
	 * This is the entry point method. Where everything starts
	 */
	public void onModuleLoad() {
		// load app title on top
		loadAppTitle();
		// load login prompt or welcome message and logout, at bottom 
		loadLoginStatusPanel();
		// load pane with list of bike racks and search results on left side
		loadRackPanel();
		// load user login/register form, user profile, favorites and maybe logout button on the right side
		loadUserPanel();
		// set login status of the web app to set visibility of the panels accordingly
		setLoginStatus(getLoginInfo());
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
		welcomeLabel = new Label("");
		loggedInLabelPanel.add(welcomeLabel);
		loggedInLabelPanel.add(logoutButton);
	  
		notLoggedInLabelPanel.add(loginLabel);
		
		logInStatusPanel.add(loggedInLabelPanel);
		logInStatusPanel.add(notLoggedInLabelPanel);
		
		mainPanel.addSouth(logInStatusPanel, 100);
	}

	/**
	 * Everything about the user including login form, user profile, favorites, etc.
	 * on the right side of the map (east of mainPanel)
	 * NOTE : uses TabLayout for all of these different pages/functionalities
	 */
	private void loadUserPanel() {
		// TODO Auto-generated method stub
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userNameTextbox);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextbox);
		userNamePanel2.add(userNameLabel2);
		userNamePanel2.add(userNameTextbox2);
		passwordPanel2.add(passwordLabel2);
		passwordPanel2.add(passwordTextbox2);
		emailPanel.add(emailLabel);
		emailPanel.add(emailTextbox);
		nickNamePanel.add(nickNameLabel);
		nickNamePanel.add(nickNameTextbox);
		registerFormButtonsPanel.add(signupButton);
		registerFormButtonsPanel.add(clearButton);
		
		loginPanel.add(loginTitleLabel);
		loginPanel.add(userNamePanel);
		loginPanel.add(passwordPanel);
		loginPanel.add(loginButton);
		registerPanel.add(registerTitleLabel);
		registerPanel.add(userNamePanel2);
		registerPanel.add(passwordPanel2);
		registerPanel.add(emailPanel);
		registerPanel.add(nickNamePanel);
		registerPanel.add(registerFormButtonsPanel);
		favoritePanel.add(favoritePanelLabel);
		profilePanel.add(profilePanelLabel);
		
		accountAccessPanel.add(loginPanel);
		accountAccessPanel.add(registerPanel);
		accountInfoPanel.add(favoritePanel);
		accountInfoPanel.add(profilePanel);
		
		//accountAccessContainer.add(accountAccessPanel);
		//accountInfoContainer.add(accountInfoPanel);
		userPanel.add(accountAccessPanel);
		userPanel.add(accountInfoPanel);
		mainPanel.addEast(userPanel, 250);
		
		// Set textboxes so that when mouse clicked will allow typing
		userNameTextbox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				userNameTextbox.setFocus(true);
			}
		});
		passwordTextbox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				passwordTextbox.setFocus(true);
			}
		});
		userNameTextbox2.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				userNameTextbox2.setFocus(true);
			}
		});
		passwordTextbox2.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				passwordTextbox2.setFocus(true);
			}
		});
		emailTextbox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				emailTextbox.setFocus(true);
			}
		});
		nickNameTextbox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				nickNameTextbox.setFocus(true);
			}
		});
		userNameTextbox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					loginButton.fireEvent(new MyClickEvent());
				};
			}
		});
		passwordTextbox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					loginButton.fireEvent(new MyClickEvent());
				};
			}
		});
		userNameTextbox2.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signupButton.fireEvent(new MyClickEvent());
				};
			}
		});
		passwordTextbox2.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signupButton.fireEvent(new MyClickEvent());
				};
			}
		});
		emailTextbox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signupButton.fireEvent(new MyClickEvent());
				};
			}
		});
		nickNameTextbox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signupButton.fireEvent(new MyClickEvent());
				};
			}
		});
		
		// Listen for mouse events on the Login, logout and signup button.
				loginButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
					String username = userNameTextbox.getValue();
					String password = passwordTextbox.getValue();
					if ((username != "") && (password != "")) {
						String[] request = new String[2];
						request[0] = username;
						request[1] = password;
						login(request);
						}
					}
				});
				logoutButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						logout();
					}
				});		
				signupButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						String username = userNameTextbox2.getValue();
						String password = passwordTextbox2.getValue();
						String email = emailTextbox.getValue();
						String nickName = nickNameTextbox.getValue();
						if ((username != "") && (password != "") && (password != "") && (password != "")) {
							String[] request = new String[4];
							request[0] = email;
							request[1] = nickName;
							request[2] = username;
							request[3] = password;
							register(request);
						}
					}
				});
				clearButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						userNameTextbox2.setValue("");
						passwordTextbox2.setValue("");
						emailTextbox.setValue("");
						nickNameTextbox.setValue("");
					}
				});
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
	    map.setSize("90%", "100%");
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
	 * @param isLoggedIn 
	 * @param loginInfo if null then user is not logged in, if not null then user is logged in
	 */
	private void setLoginStatus(boolean isLoggedIn) {
		if (isLoggedIn) {
			// set cookie
			Cookies.setCookie("bikeracklocator", loginInfo.getNickname());
			// set welcome message
			welcomeLabel.setText("Welcome back " + loginInfo.getNickname() + "!");
			// select the favorites tab to display first
			accountInfoPanel.selectTab(favoritePanel);
			// hide the account login/register and login prompt panels, 
			// show welcome label and account info panels
			notLoggedInLabelPanel.setVisible(false);
			loggedInLabelPanel.setVisible(true);
			userPanel.setWidgetVisible(accountAccessPanel, false);
			userPanel.setWidgetVisible(accountInfoPanel, true);
		}
		else {
			// remove cookie
			Cookies.removeCookie("bikeracklocator");
			// redisplay account/register and login prompt panels,
			// and hide welcome label and account info panels
			notLoggedInLabelPanel.setVisible(true);
			loggedInLabelPanel.setVisible(false);
			userPanel.setWidgetVisible(accountAccessPanel, true);
			userPanel.setWidgetVisible(accountInfoPanel, false);
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
		AccountServiceAsync loginService = GWT.create(AccountService.class);
		loginService.login(loginRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}

					public void onSuccess(LoginInfo result) {
							if (result != null) {
								loginInfo = new LoginInfo();
								loginInfo.setEmailAddress(result.getEmailAddress()); 
								loginInfo.setNickname(result.getNickname());
								// set login status
								isLoggedIn = true;
								// reset the login boxes
								userNameTextbox.setValue("");
								passwordTextbox.setValue("");
								}
							else {
								Window.alert("Invalid username or password");
							}
							setLoginStatus(isLoggedIn);
						}
				});
	}
	
	/*
	 * Register new account process, send request to server. If successful, will perform login operation
	 * with LoginInfo sent back by server for confirmation.
	 */
	protected void register(final String[] formRequest) {
		// TODO Auto-generated method stub
		AccountServiceAsync loginService = GWT.create(AccountService.class);
		loginService.register(formRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}
					
					public void onSuccess(LoginInfo result) {
						if (result != null) {
							accountAccessPanel.selectTab(loginPanel);
							clearButton.fireEvent(new MyClickEvent());
							Window.alert("Please log in using your new username and password");
						}
						else {
							Window.alert("Invalid information or Username already exists");
						}
					}
			});
	}
	
	/*
	 * logout process, remove cookie from browser and set client login status to loggedout
	 */
	protected void logout() {
		loginInfo = null;
		isLoggedIn = false;
		welcomeLabel.setText("You have been logged out.");
		setLoginStatus(false);
	}
	
	/**
	 * Retrieve login info from browser by mean of cookie 
	 * @return 
	 */
	private boolean getLoginInfo() {
		String cookieVal = Cookies.getCookie("bikeracklocator");
		if (cookieVal == null) {
		loginInfo = null;
		return false;
		}
		else {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setNickname(cookieVal);
			this.loginInfo = loginInfo;
			isLoggedIn = true;
			setLoginStatus(isLoggedIn);
			welcomeLabel.setText("Welcome back " + loginInfo.getNickname() + "!");
			return true;
		}
	}
		
	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
	}
}

