package com.codeunicorns.bikerack.client;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.codeunicorns.bikerack.client.Rack;

/**
 * Object storing user access state of the client
 *
 */
public class LoginInfo implements Serializable {

	//private boolean loggedIn = false;
	private String emailAddress;
	private String nickName;
	private int type;
	private String facebookId;
	private Rack[] favorites;
	private Long userId;
	
	public Long getId() {
		return userId;
	}

	public LoginInfo() {
		emailAddress = "";
		nickName = "";
		type = 0;
		facebookId = "";
		favorites = null;
	}
	
	/**
	 * Type 1: not loggedin
	 * Type 2: logged in as a facebook user (non-admin)
	 * Type 3: logged in as a normal user (non-facebook, non-admin)
	 * Type 4: logged in as an admin (non-facebook)
	 */
	public LoginInfo(String email, String nickName, String facebookId, int type, Rack[] favorites, Long id) {
		this.emailAddress = email;
		this.nickName = nickName;
		this.type = type;
		this.facebookId = facebookId;
		this.favorites = favorites;
		this.userId = id;
	}

	public Rack[] getFavorites() {
		return favorites;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getNickname() {
		return nickName;
	}
	
	public void setNickname(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * Type 1: not loggedin
	 * Type 2: logged in as a facebook user (non-admin)
	 * Type 3: logged in as a normal user (non-facebook, non-admin)
	 * Type 4: logged in as an admin (non-facebook)
	 */
	public int getType() {
		return type;
	}
	
	public boolean isLoggedIn() {
		return (type > 1 && type < 5);
	}
	
	public boolean isAdmin() {
		return (type == 4);
	}
	
	public boolean isFacebookUser() {
		return (type == 2);
	}
	
	public String getFacebookId() {
		if (type == 2) return facebookId;
		else return "";
	}
}