package com.codeunicorns.bikerack.client;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	//private boolean loggedIn = false;
	private String emailAddress;
	private String nickname;

//	public boolean isLoggedIn() {
//		return loggedIn;
//	}
//
//	public void setLoggedIn(boolean loggedIn) {
//		this.loggedIn = loggedIn;
//	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}