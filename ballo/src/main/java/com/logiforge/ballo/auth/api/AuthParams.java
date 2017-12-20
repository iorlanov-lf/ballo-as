package com.logiforge.ballo.auth.api;

public interface AuthParams {
	String getAuthUrl(String op);
	String getGcmSenderId();
}
