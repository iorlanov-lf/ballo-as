package com.logiforge.ballo.auth.model;

import java.io.Serializable;

public class LFUser implements Serializable {
	
    private String userName;
	private String email;
	private String displayName;
	private boolean isAdmin;
	private boolean hasValidEmail;

	public LFUser(String userName, String email, String displayName) {
		super();
		this.userName = userName;
		this.email = email;
		this.displayName = displayName;
		this.isAdmin = false;
		this.hasValidEmail = false;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean isHasValidEmail() {
		return hasValidEmail;
	}
	public void setHasValidEmail(boolean hasValidEmail) {
		this.hasValidEmail = hasValidEmail;
	}
}