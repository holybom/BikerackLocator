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
import com.google.gwt.user.client.ui.FlexTable;
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
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;
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

	private final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
	private TabLayoutPanel accountAccessPanel = new TabLayoutPanel(20, Unit.PX);
	private TabLayoutPanel accountInfoPanel = new TabLayoutPanel(20, Unit.PX);
	private TabLayoutPanel centralPanel = new TabLayoutPanel(20, Unit.PX);
	private LayoutPanel userPanel = new LayoutPanel();
	private LayoutPanel mapPanel = new LayoutPanel();
	private LayoutPanel tableViewPanel = new LayoutPanel();
	private VerticalPanel logInStatusPanel = new VerticalPanel();
	private VerticalPanel profilePanel = new VerticalPanel();
	private VerticalPanel favoritePanel = new VerticalPanel();
	private VerticalPanel rackPanel = new VerticalPanel();
	private VerticalPanel loginPanel = new VerticalPanel();
	private VerticalPanel registerPanel = new VerticalPanel();
	private HorizontalPanel importPanel = new HorizontalPanel();
	private HorizontalPanel loggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel notLoggedInLabelPanel = new HorizontalPanel();
	private HorizontalPanel userNamePanel = new HorizontalPanel();
	private HorizontalPanel passwordPanel = new HorizontalPanel();
	private HorizontalPanel userNamePanel2 = new HorizontalPanel();
	private HorizontalPanel passwordPanel2 = new HorizontalPanel();
	private HorizontalPanel emailPanel = new HorizontalPanel();
	private HorizontalPanel nickNamePanel = new HorizontalPanel();
	private HorizontalPanel adminCodePanel = new HorizontalPanel();
	private HorizontalPanel registerFormButtonsPanel = new HorizontalPanel();
	private PushButton loginButton = new PushButton("Sign in");
	private PushButton signupButton = new PushButton("Register");
	private PushButton logoutButton = new PushButton("Log out");
	private PushButton clearButton = new PushButton("Clear");
	private PushButton setURLButton = new PushButton("Set URL");
	private PushButton importButton = new PushButton("Import");
	private PushButton getTitlesButton = new PushButton("Get Titles");
	private PushButton loadButton = new PushButton("Load");
	private PushButton hideRegisterButton = new PushButton("Hide register");
	private PushButton showRegisterButton = new PushButton("Show register");
	private Label siteLabel = new Label("Bike Racks Locator");
	private Label loginLabel = new Label("Sign in or create an account to save your favorites.");
	private Label welcomeLabel = new Label("");
	private Label loginTitleLabel = new Label("ACCOUNT LOGIN");
	private Label registerTitleLabel = new Label("REGISTER NEW ACCOUNT");
	private Label favoritePanelLabel = new Label("FAVORITES");
	private Label profilePanelLabel = new Label("PROFILE");
	private Label rackPanelLabel = new Label("BIKE RACKS");
	private Label userNameLabel = new Label("Username");
	private Label passwordLabel = new Label("Password");
	private Label userNameLabel2 = new Label("Username");
	private Label passwordLabel2 = new Label("Password");
	private Label nickNameLabel = new Label("Display name");
	private Label emailLabel = new Label("Email");
	private Label adminCodeLabel = new Label("Auth code");
	private Label adminCodeLabel2 = new Label ("(for registering as admin only)");
	private TextBox userNameTextbox = new TextBox();
	private TextBox userNameTextbox2 = new TextBox();
	private TextBox emailTextbox = new TextBox();
	private TextBox nickNameTextbox = new TextBox();
	private TextBox adminCodeTextbox = new TextBox();
	private TextBox URLTextBox = new TextBox();
	private PasswordTextBox passwordTextbox = new PasswordTextBox();
	private PasswordTextBox passwordTextbox2 = new PasswordTextBox();
	private LoginInfo loginInfo = null;
	private boolean isLoggedIn = false;
	private GoogleMap map;
	private FlexTable racksTable = new FlexTable();
	
	/**
	 * Define our own ClickEvent class for UI Widgets, because for some reason GWT doesn't allow
	 * us to create a new instance of the default ClickEvent class.
	 */
	class MyClickEvent extends ClickEvent {};
	
	/**
	 * This is the entry point method. Where everything starts.
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
	public void onModuleLoad() {
		testSetRacks();
		//testGetRacks();
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
		loadMapView();
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
	 * This loads the welcome line below the map
	 * Both "Welcome!" label and signout button, and "Signin or Register to view...." label
	 * (with signup and signin buttons next to it) are loaded, but both are set to invisible
	 * on top of each other 
	 */	
	private void loadLoginStatusPanel() {
		// Initialize the welcome label, modify according to User's Display name at Login
		welcomeLabel = new Label("");
		// What displayed when user is logged in
		loggedInLabelPanel.add(welcomeLabel);
		loggedInLabelPanel.add(logoutButton);
		// What displayed when user isn't logged in
		notLoggedInLabelPanel.add(loginLabel);
		// Put everything together
		logInStatusPanel.add(loggedInLabelPanel);
		logInStatusPanel.add(notLoggedInLabelPanel);
		// TODO: remove this
		logInStatusPanel.add(hideRegisterButton);
		logInStatusPanel.add(showRegisterButton);
		showRegisterButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				accountAccessPanel.add(registerPanel);
			}
			
		});
		hideRegisterButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				accountAccessPanel.remove(registerPanel);
			}
		});
		//
		mainPanel.addSouth(logInStatusPanel, 100);
	}

	/**
	 * Everything about the user including login form, user profile, favorites, etc.
	 * on the right side of the map (east of mainPanel)
	 * There are two layers, displayed and hidden according to user's login status:
	 * 1/ Account Access Panel with login form and register form implemented as a panel with two tabs
	 * 2/ Account Info Panel with profile and favorite window implemented as a panel with two tabs
	 */
	private void loadUserPanel() {
		// load the access panel
		loadUserAccessPanel();
		// load the info panel
		loadUserInfoPanel();		
		// Putting everything together
		userPanel.add(accountAccessPanel);
		userPanel.add(accountInfoPanel);
		mainPanel.addEast(userPanel, 250);
	}
	
	/**
	 * Simple rack list or table view on the left side of the map (west of mainPanel),  
	 * NOTE: uses TabLayout for future implementation of Searching
	 */
	private void loadRackPanel() {
		// TODO Implement rack list display, only label for now
		rackPanel.add(rackPanelLabel);
		// Put everything together
		mainPanel.addWest(rackPanel, 250);
	}
	
	/**
	 * And finally, this loads the map at the center
	 */
	private void loadMapView() {
		 buildMapView();
		 buildTableView();
		 buildImportView();
		 mainPanel.add(centralPanel);
	}
	
	/**
	 * might need to add intense markers making and loading later
	 */
	private void buildMapView() {
	    // Open a map centered on Vancouver, Canada
	    LatLng vancouverCity = LatLng.create(49.247,-123.114);
	    MapOptions mapOptions = MapOptions.create();	
	    mapOptions.setCenter(vancouverCity);
	    mapOptions.setMapTypeId(MapTypeId.ROADMAP);
	    // Add some controls for the zoom level
	    mapOptions.setZoom(11.0);
	    // finalize the map
	    //GoogleMap map = GoogleMap.create(mapPanel.getElement(),mapOptions);
	    map = GoogleMap.create(mapPanel.getElement(),mapOptions);
	    centralPanel.add(mapPanel);
	    // Add markers, TODO: add the bike racks data here as markers
	    LatLng[] latLngs = new LatLng[1];
	    latLngs[0] = (vancouverCity);
	    setMarkers(latLngs);
	  }
	
	/**
	 * foo
	 */
	private void buildTableView() {
		racksTable.setStyleName("table");
		racksTable.getRowFormatter().setStyleName(0, "tableHeader");
		racksTable.setText(0, 0, "St number");
		racksTable.setText(0, 1, "St Name");
		racksTable.setText(0, 2, "St Side");
		racksTable.setText(0, 3, "Skytrain");
		racksTable.setText(0, 4, "BIA");
		racksTable.setText(0, 5, "# of racks");
		racksTable.addStyleName("table");
		racksTable.getRowFormatter().addStyleName(0, "tableHeader");
		/* table is filled with data in the getRacks method */
		tableViewPanel.add(racksTable);
	    centralPanel.add(tableViewPanel);
	}
	
	/**
	 * foo
	 */
	private void buildImportView() {
		importPanel.add(URLTextBox);
		importButton.setEnabled(false);
		getTitlesButton.setEnabled(false);
		loadButton.setEnabled(false);
		importPanel.add(setURLButton);
		importPanel.add(importButton);
		importPanel.add(getTitlesButton);
		importPanel.add(loadButton);
		centralPanel.add(importPanel);
	}
	
	/**
	 * Create markers for the map, based on the received dataset from the server
	 * @param latLngs list of markers to create, might need to refactor this variable to global
	 */
	private void setMarkers(final LatLng[] latLngs) {
		MarkerOptions markerOptions = MarkerOptions.create();
	    markerOptions.setPosition(latLngs[0]);
	    markerOptions.setMap(map);
	    markerOptions.setTitle("Hello World!");
	    final Marker myMarker = Marker.create(markerOptions);
	    myMarker.addClickListener(new Marker.ClickHandler() {
	      @Override
	      public void handle(MouseEvent event) {
	    	  map.setCenter(myMarker.getPosition());
	    	  map.setZoom(15.0);
	      }
	    });		
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
			// redisplay account/register and login prompt panels,
			// and hide welcome label and account info panels
			notLoggedInLabelPanel.setVisible(true);
			loggedInLabelPanel.setVisible(false);
			userPanel.setWidgetVisible(accountAccessPanel, true);
			userPanel.setWidgetVisible(accountInfoPanel, false);
		}
	}
	
	/**
	 * Send login Info to be checked by the server, if server sends a non-null login info, then
	 * set the cookie and set status of client to be logged in with that loginInfo. Server sents back
	 * a null if info in login form is not a valid user
	 * @param is an array of string containing user and password (simple implementation for now) 
	 * to be sent to server for authentication
	 */
	protected void login(final String[] loginRequest) {
		if (!checkInput(loginRequest)) {
			return;
		}
		AccountServiceAsync loginService = GWT.create(AccountService.class);
		loginService.login(loginRequest,
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}

					public void onSuccess(LoginInfo result) {
							if (result != null) {
								loginInfo = new LoginInfo(result.getEmailAddress(), result.getNickname(), result.isAdmin());
								// set login status
								isLoggedIn = true;
								// set cookie
								String loginInfo = loginRequest[0] + " " + loginRequest[1];
								Cookies.setCookie("bikeracklocator", loginInfo);
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
	
	/**
	 * Register new account process, send request to server. If successful, will perform login operation
	 * with LoginInfo sent back by server for confirmation.
	 * @param formRequest contains {email, nickName, username, password, adminCode};
	 */
	protected void register(final String[] formRequest) {
		if (!checkInput(formRequest)) {
			return;
		}
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
							// Alert user if admin code was wrong
							if (formRequest[4] != "") {
								if (result.isAdmin()) {
									Window.alert("You have been registered as Admin");
								}
								else Window.alert("Invalid admin code, you have been registered as a regular user");
							}
							Window.alert("Please log in using your new username and password");
						}
						else {
							Window.alert("Invalid information or Username already exists");
						}
					}
			});
	}
	
	/**
	 * 
	 * @param request is {username, password} or {email, nickName, username, password, adminCode}
	 * @return whether input is valid
	 */
	private boolean checkInput(String[] request) {
		int i = 0;
		// if request is from register form, then checks email separately and starts from 1 instead
		if (request.length == 5) {	
			if (request.length == 5 && !request[i].matches("^[0-9a-z]+(\\_[0-9a-z]+)*(\\.[0-9a-z]+(\\_[0-9a-z]+)*){0,1}\\@[0-9a-z]+\\.[0-9a-z]+$")) {
				Window.alert("Invalid email");
				return false;
			}
			i = 1;
		}
		// traverse through the request array and checks every remaining textbox.
		while (i < request.length) {
			if (request[i] == "" && i != 4) return false; 
			if (!request[i].matches("^[0-9A-Za-z]{5,15}$")) {
				Window.alert("Input must be 5-15 alphanumeric characters");
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * logout process, remove cookie from browser and set client login status to loggedout
	 */
	protected void logout() {
		loginInfo = null;
		isLoggedIn = false;
		welcomeLabel.setText("You have been logged out.");
		setLoginStatus(false);
		// remove cookie
		Cookies.removeCookie("bikeracklocator");
	}
	
	/**
	 * Retrieve login info from browser on first run by mean of cookie
	 * @return true if already logged in, else false
	 */
	private boolean getLoginInfo() {
		String cookieVal = Cookies.getCookie("bikeracklocator");
		if (cookieVal == null) {
		loginInfo = null;
		return false;
		}
		else {
			login(cookieVal.split(" "));
			//loginInfo.setNickname(cookieVal);
			//this.loginInfo = loginInfo;
			//isLoggedIn = true;
			//setLoginStatus(isLoggedIn);
			//welcomeLabel.setText("Welcome back " + loginInfo.getNickname() + "!");
			return true;
		}
	}
	
	/**
	 * Creating user access panel, including a login form and a register form
	 */
	private void loadUserAccessPanel() {
		// Creating login UI
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userNameTextbox);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextbox);
		
		loginPanel.add(loginTitleLabel);
		loginPanel.add(userNamePanel);
		loginPanel.add(passwordPanel);
		loginPanel.add(loginButton);
		
		// Creating register UI
		userNamePanel2.add(userNameLabel2);
		userNamePanel2.add(userNameTextbox2);
		passwordPanel2.add(passwordLabel2);
		passwordPanel2.add(passwordTextbox2);
		emailPanel.add(emailLabel);
		emailPanel.add(emailTextbox);
		nickNamePanel.add(nickNameLabel);
		nickNamePanel.add(nickNameTextbox);
		adminCodePanel.add(adminCodeLabel);
		adminCodePanel.add(adminCodeTextbox);
		registerFormButtonsPanel.add(signupButton);
		registerFormButtonsPanel.add(clearButton);

		registerPanel.add(registerTitleLabel);
		registerPanel.add(userNamePanel2);
		registerPanel.add(passwordPanel2);
		registerPanel.add(emailPanel);
		registerPanel.add(nickNamePanel);
		registerPanel.add(adminCodePanel);
		registerPanel.add(adminCodeLabel2);
		registerPanel.add(registerFormButtonsPanel);
		
		// Put everything together
		accountAccessPanel.add(loginPanel);
		accountAccessPanel.add(registerPanel);
		
		// Add click and keyboard events for the textboxes and buttons
		loadUserAccessPanelEvents();
	}

	/**
	 * Load UI events of the login and register form, two things to do
	 * 1/ When clicked, the textboxes will switch focus to themselves, allow typing
	 * 2/ When Enter key is pressed while typing in the textboxes, the Login or Signup button 
	 *    will be clicked according to the current form
	 * 3/ Implement the login, signup buttons to send form info to server, signout button 
	 *    and clear button to clear form
	 */
	private void loadUserAccessPanelEvents() {
		// Implementing 1/
		ClickHandler userNameFocus = new ClickHandler() {
			public void onClick(ClickEvent event) {
				userNameTextbox.setFocus(true);
			}
		};
		ClickHandler userNameFocus2 = new ClickHandler() {
			public void onClick(ClickEvent event) {
				userNameTextbox2.setFocus(true);
			}
		};
		ClickHandler passwordFocus = new ClickHandler() {
			public void onClick(ClickEvent event) {
				passwordTextbox.setFocus(true);
			}
		};
		ClickHandler passwordFocus2 = new ClickHandler() {
			public void onClick(ClickEvent event) {
				passwordTextbox2.setFocus(true);
			}
		};
		ClickHandler emailFocus = new ClickHandler() {
			public void onClick(ClickEvent event) {
				emailTextbox.setFocus(true);
			}
		};
		ClickHandler nicknameFocus = new ClickHandler() {
			public void onClick(ClickEvent event) {
				nickNameTextbox.setFocus(true);
			}
		};
		ClickHandler adminCodeFocus = new ClickHandler() {
			public void onClick(ClickEvent event) {
				adminCodeTextbox.setFocus(true);
			}
		};
		// Add the events to appropriate textboxes
		userNameTextbox.addClickHandler(userNameFocus);
		passwordTextbox.addClickHandler(passwordFocus);
		userNameTextbox2.addClickHandler(userNameFocus2);
		passwordTextbox2.addClickHandler(passwordFocus2);
		emailTextbox.addClickHandler(emailFocus);
		nickNameTextbox.addClickHandler(nicknameFocus);
		adminCodeTextbox.addClickHandler(adminCodeFocus);
		// Implementing 2/
		KeyDownHandler loginButtonClick = new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					loginButton.fireEvent(new MyClickEvent());
				};
			}
		};
		KeyDownHandler signupButtonClick = new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					signupButton.fireEvent(new MyClickEvent());
				};
			}
		};
		// Add the events to appropriate textboxes
		userNameTextbox.addKeyDownHandler(loginButtonClick);
		passwordTextbox.addKeyDownHandler(loginButtonClick);
		userNameTextbox2.addKeyDownHandler(signupButtonClick);
		passwordTextbox2.addKeyDownHandler(signupButtonClick);
		emailTextbox.addKeyDownHandler(signupButtonClick);
		nickNameTextbox.addKeyDownHandler(signupButtonClick);
		adminCodeTextbox.addKeyDownHandler(signupButtonClick);
		
		// Implementing 3/ Listen for mouse events on the Login, Logout and Signup and Clear buttons.
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			// Retrieve info from the login form and call login on server
			String username = userNameTextbox.getValue();
			String password = passwordTextbox.getValue();
			String[] request = {username, password};
			login(request);
			}
		});
		logoutButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			// Logout method is local, initiate client log out procedure
				logout();
			}
		});		
		signupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Retrieve info from register form and call register on server
				String username = userNameTextbox2.getValue();
				String password = passwordTextbox2.getValue();
				String email = emailTextbox.getValue();
				String nickName = nickNameTextbox.getValue();
				String adminCode = adminCodeTextbox.getValue();
				String[] request = {email, nickName, username, password, adminCode};
				register(request);
			}
		});
		clearButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Set textbox values to an empty string
				userNameTextbox2.setValue("");
				passwordTextbox2.setValue("");
				emailTextbox.setValue("");
				nickNameTextbox.setValue("");
			}
		});
	}
	
	/**
	 * Load the User Info Panel with two tabs, favorite tab and profile tab
	 */
	private void loadUserInfoPanel() {
		// TODO Implement profile panel and favorite panel for the logged in user
		// Just add the labels for now 
		favoritePanel.add(favoritePanelLabel);
		profilePanel.add(profilePanelLabel);
		
		// Put everything together
		accountInfoPanel.add(favoritePanel);
		accountInfoPanel.add(profilePanel);
	}
		
	/**
	 * Only admins can do this
	 */
	private void testSetRacks() {
		// TODO Add appropriate arguments for loadData to load data to clients, parsed with the given params
		AdminServiceAsync adminService = GWT.create(AdminService.class);
		testImportData(adminService);
//		testGetTitleLine(adminService);
//		testLoadData(adminService);
//		testSetTableView(adminService);
	}


	/**
	 * @param adminService
	 */
	private void testImportData(final AdminServiceAsync adminService) {
		adminService.importData(new AsyncCallback<Boolean>() {
					public void onFailure(Throwable error) {
						handleError(error);
					}

					public void onSuccess(Boolean result) {
						if (!result) {
							//Window.alert("Import failed, try again");
						}
						testGetTitleLine(adminService);
						//else Window.alert("Import successful, please proceed to parse");
					}
		});
	}


	/**
	 * @param adminService
	 */
	private void testGetTitleLine(final AdminServiceAsync adminService) {
		adminService.getTitleLine(new AsyncCallback<String[]>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(String[] result) {
				if (result != null) {
					testLoadData(adminService);
					System.out.println(result[0] + " " + result[1] + " " + result[2] + " " 
									+ result[3] + " " + result[4] + " " + result[5]);
				}
				//else Window.alert("Dataset format changed, cannot get new dataset");
					
			}
		});
	}

	/**
	 * @param adminService
	 */
	private void testLoadData(final AdminServiceAsync adminService) {
		adminService.loadData(null, new AsyncCallback<Boolean>() {
				public void onFailure(Throwable error) {
					handleError(error);
				}

				public void onSuccess(Boolean result) {
					if (!result) {
					//	Window.alert("Parse failed, try again");
					}
					testSetTableView(adminService);
					//else Window.alert("Parse successful, new data loaded");
				}
		});
	}


	/**
	 * @param adminService
	 */
	private void testSetTableView(AdminServiceAsync adminService) {
		adminService.setTableView(null, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(Boolean result) {	
				if (!result) {
					//Window.alert("Saving view settings failed, try again");
				}
				testGetRacks();
				//else Window.alert("Settings saved");
			}
		});
	}

	/**
	 * For testing getting rack information
	 */
	private void testGetRacks() {
		// TODO Auto-generated method stub
		RackServiceAsync rackService = GWT.create(RackService.class);
		rackService.getRacks(new AsyncCallback<Rack[]>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Rack[] result) {
				if (result == null) {
					Window.alert("Error getting bike racks data from server");
				}
				else {
					int row = 1;
					for (Rack rack : result) {
						racksTable.setText(row, 0, Integer.toString(rack.getStreetNum()));
						racksTable.setText(row, 1, rack.getStreetName());
						racksTable.setText(row, 2, rack.getStreetSide());
						racksTable.setText(row, 3, rack.getSkytrain());
						racksTable.setText(row, 4, rack.getBIA());
						racksTable.setText(row, 5, Integer.toString(rack.getNumRacks()));
						racksTable.getRowFormatter().setStyleName(row, "tableContents");
						row++;
					}
				}
			}
		});
	}
	
	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
	}
}

