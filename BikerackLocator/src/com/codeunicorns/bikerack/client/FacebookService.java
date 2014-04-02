package com.codeunicorns.bikerack.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.gwtfb.sdk.FBCore;
import com.gwtfb.sdk.FBEvent;
import com.gwtfb.sdk.FBXfbml;
import com.gwtfb.sdk.JSOModel;

public class FacebookService {

	private FBCore fbCore;
	private FBEvent fbEvent;
	private boolean status = false;
	private boolean xfbml = true;	
	private Timer refreshFacebook;
	private int FACEBOOK_REFRESH =5000;
	private Bikerack main;
	private boolean isFbLoggedIn = false;
	private FbStatusCallback fbStatusCallback = new FbStatusCallback();
	private LoginCallback loginCallback = new LoginCallback();
	private boolean startLogin = false;
	private int loginCheckTries = 3;
	//private Timer startLoginTimer;
	
	public FacebookService(Bikerack main) {
		this.main = main;
		final String APPID = "1483880728501371";
		Timer fbInitTimer = new Timer() {
			@Override
			public void run() {
				fbCore = GWT.create(FBCore.class);
				fbEvent = GWT.create(FBEvent.class);
				fbInit(APPID);
			}};
		fbInitTimer.schedule(5000);
	}

	private void fbInit(String APPID) {
		fbCore.init(APPID, status, xfbml);
		fbEvent.subscribe("auth.statusChange",fbStatusCallback);
//		fbEvent.subscribe("auth.login", fbStatusCallback);
//		fbEvent.subscribe("auth.logout", logoutCheckCallback);
		refreshFacebook = new Timer() {
			@Override
			public void run() {
				// periodically check if the user is still logged in FB
				checkLogggedOut();
			}
		};
		refreshFacebook.scheduleRepeating(FACEBOOK_REFRESH);
	}
	
	/**
	* Callback used to retrieve detailed info of the logged in user
	*/
	class UserCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			if (main.getClientLoginInfo().getType() == 1) {
				//Window.alert("Loggin in");
				String [] request = {jso.get("id"), jso.get("name")};
				main.clientRequest("register", request);
			}
		}
	}
	
	/** Callback used for periodic check if the user has logged out of facebook in-app or somewhere else
	*/
	class LoginCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			FBXfbml.parse();
			isFbLoggedIn = false;
			if (jso.get("status").compareTo("connected") == 0) {
				fbCore.api ( "/me" , new UserCallback());
				isFbLoggedIn = true;
				loginCheckTries = 0;
			}
			else if (jso.get("status").compareTo("not_authorized") == 0) {
				isFbLoggedIn = true;
			}
		}
	}
	
	class FbStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			boolean newIsFbLoggedIn = false;
			if (jso.get("status").compareTo("connected") == 0) {
				newIsFbLoggedIn = true;
			}
			else if (main.getClientLoginInfo().isFacebookUser()) {
				Window.alert("You logged out of Facebook or App was de-authorized");
				main.clientRequest("logout", null);
			}
			if (newIsFbLoggedIn ^ isFbLoggedIn) {
				isFbLoggedIn = newIsFbLoggedIn;
				FBXfbml.parse();
			}
		}
	}
	
	public void checkLogggedOut() {
		if (loginCheckTries <= 0) fbCore.getLoginStatus(fbStatusCallback);
		else {
			loginCheckTries--;
			fbCore.getLoginStatus(loginCallback);
		}
	}

	public void login() {
		loginCheckTries = 3;
		fbCore.login(loginCallback);
	}
}
