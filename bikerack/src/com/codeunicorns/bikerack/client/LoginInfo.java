package com.codeunicorns.bikerack.client;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

public class LoginInfo implements Serializable {

	//private boolean loggedIn = false;
	private String emailAddress;
	private String nickName;
	private boolean isAdmin;
	
	public LoginInfo() {
		emailAddress = null;
		nickName = null;
		isAdmin = false;
	}
	
	public LoginInfo(String emailAddress, String nickName, boolean isAdmin) {
		this.emailAddress = emailAddress;
		this.nickName = nickName;
		this.isAdmin = isAdmin;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getNickname() {
		return nickName;
	}

	public boolean isAdmin() {
		return isAdmin;
	}
}