package com.codeunicorns.bikerack.client.ui;

import com.codeunicorns.bikerack.client.Bikerack;
import com.codeunicorns.bikerack.client.ui.MyClickEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Everything about the user including login form, user profile, favorites, etc.
 * on the right side of the map (east of mainPanel)
 * There are two layers, displayed and hidden according to user's login status:
 * 1/ Account Access Panel with login form and register form implemented as a panel with two tabs
 * 2/ Account Info Panel with profile and favorite window implemented as a panel with two tabs
 */
public class UserPanel extends LayoutPanel {
	private static UserPanel panelInstance = null;
	private TabLayoutPanel accountAccessPanel = new TabLayoutPanel(20, Unit.PX);
	private TabLayoutPanel accountInfoPanel = new TabLayoutPanel(20, Unit.PX);
	private VerticalPanel profilePanel = new VerticalPanel();
	private VerticalPanel favoritePanel = new VerticalPanel();
	private VerticalPanel loginPanel = new VerticalPanel();
	private VerticalPanel registerPanel = new VerticalPanel();
	private HorizontalPanel userNamePanel = new HorizontalPanel();
	private HorizontalPanel passwordPanel = new HorizontalPanel();
	private HorizontalPanel userNamePanel2 = new HorizontalPanel();
	private HorizontalPanel passwordPanel2 = new HorizontalPanel();
	private HorizontalPanel confirmPasswordPanel = new HorizontalPanel();
	private HorizontalPanel emailPanel = new HorizontalPanel();
	private HorizontalPanel nickNamePanel = new HorizontalPanel();
	private HorizontalPanel adminCodePanel = new HorizontalPanel();
	private HorizontalPanel registerFormButtonsPanel = new HorizontalPanel();
	private PushButton loginButton = new PushButton("Sign in");
	private PushButton signupButton = new PushButton("Register");
	private PushButton clearButton = new PushButton("Clear");
	private PushButton logoutButton = new PushButton("Log out");
	private Label loginTitleLabel = new Label("ACCOUNT LOGIN");
	private Label registerTitleLabel = new Label("REGISTER NEW ACCOUNT");
	private Label favoritePanelLabel = new Label("FAVORITES");
	private Label profilePanelLabel = new Label("PROFILE");
	private Label userNameLabel = new Label("Username");
	private Label passwordLabel = new Label("Password");
	private Label userNameLabel2 = new Label("Username");
	private Label passwordLabel2 = new Label("Password");
	private Label confirmPasswordLabel = new Label("Retype password");
	private Label nickNameLabel = new Label("Display name");
	private Label emailLabel = new Label("Email");
	private Label adminCodeLabel = new Label("Auth code");
	private Label adminCodeLabel2 = new Label ("(for registering as admin only)");
	private TextBox userNameTextbox = new TextBox();
	private TextBox userNameTextbox2 = new TextBox();
	private TextBox emailTextbox = new TextBox();
	private TextBox nickNameTextbox = new TextBox();
	private TextBox adminCodeTextbox = new TextBox();
	private PasswordTextBox passwordTextbox = new PasswordTextBox();
	private PasswordTextBox passwordTextbox2 = new PasswordTextBox();
	private PasswordTextBox confirmPasswordTextbox = new PasswordTextBox();
	private Widget fbLoginButton = new HTML(
			"<div class='fb-login-button' data-max-rows='1'" 
			+ "data-size='medium' data-show-faces='false'" 
			+ "data-auto-logout-link='true'></div>");
	
	//Widget fbLoginButton2;
	private UIController uiController;
	
	public static UserPanel getInstance(UIController uiController) {
		if (panelInstance == null) panelInstance = new UserPanel(uiController);
		return panelInstance;
	}
	
	private UserPanel() {};
	
	private UserPanel(UIController uiController) {
		this.uiController = uiController;
		// load the access panel
		loadUserAccessPanel();
		// load the info panel
		loadUserInfoPanel();		
		// Putting everything together
		this.add(accountAccessPanel);
		this.add(accountInfoPanel);
	}

	/**
	 * Creating user access panel, including a login form and a register form
	 */
	private void loadUserAccessPanel() {

		Widget fbLoginButton = new HTML(
				"<div class='fb-login-button' data-max-rows='1'" 
				+ "data-size='medium' data-show-faces='false'" 
				+ "data-auto-logout-link='false'></div>");
		
		// Creating login UI
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userNameTextbox);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextbox);
		
		loginPanel.add(loginTitleLabel);
		loginPanel.add(userNamePanel);
		loginPanel.add(passwordPanel);
		loginPanel.add(loginButton);
		loginPanel.add(fbLoginButton);
		
		// Creating register UI
		userNamePanel2.add(userNameLabel2);
		userNamePanel2.add(userNameTextbox2);
		passwordPanel2.add(passwordLabel2);
		passwordPanel2.add(passwordTextbox2);
		confirmPasswordPanel.add(confirmPasswordLabel);
		confirmPasswordPanel.add(confirmPasswordTextbox);
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
		registerPanel.add(confirmPasswordPanel);
		registerPanel.add(emailPanel);
		registerPanel.add(nickNamePanel);
		registerPanel.add(adminCodePanel);
		registerPanel.add(adminCodeLabel2);
		registerPanel.add(registerFormButtonsPanel);
		//registerPanel.add(fbLoginButton);
		
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
			uiController.clientRequest("login",request);
			// reset the login boxes
			userNameTextbox.setValue("");
			passwordTextbox.setValue("");
			}
		});
		logoutButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
			// Logout method is local, initiate client log out procedure
				uiController.clientRequest("logout", null);
				TitleStatusPanel.getInstance().getWelcomeLabel().setText("You have been logged out.");
			}
		});		
		signupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Retrieve info from register form and call register on server
				String username = userNameTextbox2.getValue();
				String password = passwordTextbox2.getValue();
				String confirmPassword = confirmPasswordTextbox.getValue();
				String email = emailTextbox.getValue();
				String nickName = nickNameTextbox.getValue();
				String adminCode = adminCodeTextbox.getValue();
				String[] request = {email, nickName, username, password, adminCode};
				if (confirmPassword.compareTo(password) != 0) Window.alert("Retype Password must match Password");
				else {
					uiController.clientRequest("register", request);
					clearButton.fireEvent(new MyClickEvent());
				}
			}
		});
		clearButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Set textbox values to an empty string
				userNameTextbox2.setValue("");
				passwordTextbox2.setValue("");
				confirmPasswordTextbox.setValue("");
				emailTextbox.setValue("");
				nickNameTextbox.setValue("");
				adminCodeTextbox.setValue("");
			}
		});
	}

	/**
	 * Load the User Info Panel with two tabs, favorite tab and profile tab
	 */
	private void loadUserInfoPanel() {
		//fbLoginButton2 = new HTML(this.fbLoginButton.getElement().getInnerHTML());
		// TODO Implement profile panel and favorite panel for the logged in user
		// Just add the labels for now 
		favoritePanel.add(favoritePanelLabel);
		profilePanel.add(profilePanelLabel);
		profilePanel.add(logoutButton);
		profilePanel.add(fbLoginButton);
		
		// Put everything together
		accountInfoPanel.add(favoritePanel);
		accountInfoPanel.add(profilePanel);
	}

	TabLayoutPanel getAccountAccessPanel() {
		return accountAccessPanel;
	}

	TabLayoutPanel getAccountInfoPanel() {
		return accountInfoPanel;
	}
	
	PushButton getLogoutButton() {
		return logoutButton; 
	}
	
	VerticalPanel getFavoritePanel() {
		return favoritePanel;
	}
	
	Widget getFbButton() {
		return fbLoginButton;
	}
	
	VerticalPanel getLoginPanel() {
		return loginPanel;
	}
	
}
