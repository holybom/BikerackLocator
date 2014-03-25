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
	private int FACEBOOK_REFRESH =10000;
	//private LoginStatusCallback loginStatus;
	//private SessionChangeCallback sessionChangeCallback;
	//private UserCallback userCallback;
	private Bikerack main;
	
	public FacebookService(Bikerack main) {
		this.main = main;
		String APPID = "1483880728501371";
		fbCore = GWT.create(FBCore.class);
		fbEvent = GWT.create(FBEvent.class);
		fbCore.init(APPID, status, xfbml);
//		sessionChangeCallback = new SessionChangeCallback ();
		fbEvent.subscribe("auth.statusChange",new LoginStatusCallback());
//		userCallback = new UserCallback();
		refreshFacebook = new Timer() {
			@Override
			public void run() {
				// Get facebook Info
				getLoginStatus();
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
			String userId = jso.get("id");	
			LoginInfo loginInfo = main.getClientLoginInfo();
			if (loginInfo.getType() == 1) {
				//Window.alert("logginin");
				String[] request = {userId};
				//main.clientRequest("logout", null);
				main.clientRequest("register", request);
				loginInfo = new LoginInfo(jso.get("email"),jso.get("name"),userId, 2);
				main.setLoginStatus(loginInfo);	
			}
			//Window.alert(userId + " " + jso.get("name") + " " + jso.get("email"));
		}
	}
	
	/** Callback used for periodic check if the user has logged out of facebook in-app or somewhere else
	*/
	class LoginStatusCallback extends FacebookCallback<JavaScriptObject> {
		public void onSuccess ( JavaScriptObject response ) {
			//Window.alert("Retrieved loginInfo");
			JSOModel jso = response.cast();
			LoginInfo loginInfo = main.getClientLoginInfo();
			if (jso.get("status").compareTo("connected") == 0) {
				//Window.alert("connected");
				fbCore.api ( "/me" , new UserCallback());
				if (!loginInfo.isFacebook()) FBXfbml.parse();
			}
			else if (jso.get("status").compareTo("not_authorized") == 0) {
				if (!loginInfo.isFacebook()) FBXfbml.parse();
			}
			else if (main.getClientLoginInfo().isFacebook()) {
				//Window.alert("not logged in");
				main.clientRequest("logout", null);
				FBXfbml.parse();
			}
		}
	}
	
//	/** Callback used to handle user clicking the login button
//	 */
//	class LoginCallback extends FacebookCallback<JavaScriptObject> {
//		public void onSuccess ( JavaScriptObject response ) {
//			
//		}
//	};
//	
//	/** Callback used to handle user clicking the logout button
//	 */
//	class LogoutCallback extends FacebookCallback<JavaScriptObject> {};
	
	public void getLoginStatus() {
		fbCore.getLoginStatus(new LoginStatusCallback());
	}
}
