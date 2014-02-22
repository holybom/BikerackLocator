package com.codeunicorns.bikerack.client;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

public class LoginInfo implements Serializable {

	//private boolean loggedIn = false;
	private String emailAddress;
	private String nickName;
	
	public LoginInfo() {
		emailAddress = null;
		nickName = null;
	}
	
//	public LoginInfo(String emailAddress, String nickName) {
//		this.emailAddress = emailAddress;
//		this.nickName = nickName;
//	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickName;
	}

	public void setNickname(String nickName) {
		this.nickName = nickName;
	}
}