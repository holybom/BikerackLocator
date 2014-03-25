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
	private boolean status = true;
	private boolean xfbml = true;	
	private Timer refreshFacebook;
	private int FACEBOOK_REFRESH =1000;
	private Bikerack main;
	private boolean isFbLoggedIn = false;
	
	public FacebookService(Bikerack main) {
		this.main = main;
		String APPID = "1483880728501371";
		fbCore = GWT.create(FBCore.class);
		fbEvent = GWT.create(FBEvent.class);
		fbCore.init(APPID, status, xfbml);
		fbEvent.subscribe("auth.statusChange",new LoginStatusCallback());
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
	class LoginStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			//LoginInfo loginInfo = main.getClientLoginInfo();
			boolean newIsFbLoggedIn = isFbLoggedIn;
			if (jso.get("status").compareTo("connected") == 0) {
				fbCore.api ( "/me" , new UserCallback());
				newIsFbLoggedIn = true;
			}
			else if (jso.get("status").compareTo("not_authorized") == 0) {
				newIsFbLoggedIn = true;
			}
			else if (main.getClientLoginInfo().isFacebookUser()) {
				main.clientRequest("logout", null);
				newIsFbLoggedIn = false;
			}
			if (newIsFbLoggedIn ^ isFbLoggedIn) {
				isFbLoggedIn = newIsFbLoggedIn;
				FBXfbml.parse();
			}
		}
	}
	
	class LogoutStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			//LoginInfo loginInfo = main.getClientLoginInfo();
			boolean newIsFbLoggedIn = isFbLoggedIn;
			if (jso.get("status").compareTo("connected") == 0 || jso.get("status").compareTo("not_authorized") == 0) {
				newIsFbLoggedIn = true;
			}
			else if (main.getClientLoginInfo().isFacebookUser()) {
				main.clientRequest("logout", null);
				newIsFbLoggedIn = false;
			}
			if (newIsFbLoggedIn ^ isFbLoggedIn) {
				isFbLoggedIn = newIsFbLoggedIn;
				FBXfbml.parse();
			}
		}
	}
	
	public void checkLogggedOut() {
		fbCore.getLoginStatus(new LogoutStatusCallback());
	}
}
