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
	private boolean cookie = true;
	private boolean xfbml = true;	
	private Timer refreshFacebook;
	private int FACEBOOK_REFRESH = 1000;
	private LoginStatusCallback loginStatus;
	private SessionChangeCallback sessionChangeCallback;
	private UserCallback userCallback;
	private Bikerack main;
	
	public FacebookService(Bikerack main) {
		this.main = main;
		String APPID = "1483880728501371";
		fbCore = GWT.create(FBCore.class);
		fbEvent = GWT.create(FBEvent.class);
		fbCore.init(APPID, status, cookie, xfbml);
		sessionChangeCallback = new SessionChangeCallback ();
		fbEvent.subscribe("auth.sessionChange",sessionChangeCallback);
		loginStatus = new LoginStatusCallback();
		userCallback = new UserCallback();
		
		refreshFacebook = new Timer() {
			@Override
			public void run() {
				// Get facebook Info
				fbCore.getLoginStatus(loginStatus);
			}
		};
		refreshFacebook.scheduleRepeating(FACEBOOK_REFRESH);
	}
	
	//
	// Callback used when session status is changed
	//
	class SessionChangeCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			Window.alert("Session changed");
		}
	}
	
	//
	// Callback used to retrieve user info when logged in
	//
	class UserCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			JSOModel jso = response.cast();
			String userId = jso.get("id");	
			LoginInfo loginInfo = main.getClientLoginInfo();
			if ((loginInfo.getType() == 3) || (loginInfo.getType() == 1) || (loginInfo.isFacebook() && (loginInfo.getFacebookId().compareTo(userId) != 0))) { 
				String[] request = {userId};
				main.clientRequest("logout", null);
				main.clientRequest("register", request);
				loginInfo = new LoginInfo(jso.get("email"),jso.get("name"),userId, 2);
//				Window.alert(userId + " " + jso.get("name") + " " + jso.get("email"));
//				Window.alert(loginInfo.getNickname());
				main.setLoginStatus(loginInfo);	
			}
			//Window.alert(userId + " " + jso.get("name") + " " + jso.get("email"));
		}
	}
	
	// Callback used when checking login status
	class LoginStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			if (fbCore.getAuthResponse() != null) {
				fbCore.api ( "/me" , userCallback);
			}
			else if (main.getClientLoginInfo().isFacebook()) main.clientRequest("logout", null);
			FBXfbml.parse();
		}
	}
	
	public void getLoginStatus() {
		fbCore.getLoginStatus(loginStatus);
	}
}
