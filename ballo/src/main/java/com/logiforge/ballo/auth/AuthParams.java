package com.logiforge.ballo.auth;

public interface AuthParams {
	String getAuthUrl(String op);
	String getGcmSenderId();
}
