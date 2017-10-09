package com.logiforge.ballo.auth.model;

import java.util.Date;

public class AuthTokens {
	private String accessToken;
	private String refreshToken;
	private Date refreshTokenDt;

	public AuthTokens() {
	}

	public AuthTokens(String accessToken, String refreshToken, Date refreshTokenDt) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.refreshTokenDt = refreshTokenDt;
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
	public Date getRefreshTokenDt() {
		return refreshTokenDt;
	}
	public void setRefreshTokenDt(Date refreshTokenDt) {
		this.refreshTokenDt = refreshTokenDt;
	}
}
