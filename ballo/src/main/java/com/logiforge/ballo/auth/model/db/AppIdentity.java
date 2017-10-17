package com.logiforge.ballo.auth.model.db;

import java.io.Serializable;

public class AppIdentity implements Serializable {
	static final long serialVersionUID = 42L;

	private String appId;
	private String deviceId;
    private String userName;
	private String email;
	private String displayName;
	private String accessToken;
	private String refreshToken;

	public AppIdentity(String appId, String deviceId, String userName, String email, String displayName, String accessToken, String refreshToken) {
		this.appId = appId;
		this.deviceId = deviceId;
		this.userName = userName;
		this.email = email;
		this.displayName = displayName;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
