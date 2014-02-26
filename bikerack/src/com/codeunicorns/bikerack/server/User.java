package com.codeunicorns.bikerack.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.codeunicorns.bikerack.client.LoginInfo;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String emailAddress;
	@Persistent
	private String nickName;
	@Persistent
	private String userName;
	@Persistent
	private String password;
	@Persistent
	private Date createDate;   
	
	
	public User() {
		this.createDate = new Date();
	}
	
	public User(String emailAddress, String nickName, String userName, String password) {
		this();
		this.emailAddress = emailAddress;
		this.nickName = nickName;
		this.userName = userName;
		this.password = password;
	}

	
	public Long getId() {
		return id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return userName;
	}	
}