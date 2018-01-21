package com.logiforge.ballo.model.api;

public class SimpleResponse {
	public static final int ERR_NO_ERROR = 0;
	public static final int ERR_INTERNAL_ERROR = 1;
	public static final int ERR_AUTH_ERROR = 100;
	
	public boolean success = false;
	public int errorCode = ERR_NO_ERROR;
	public String message = null;
	
	public SimpleResponse() {
		
	}
	
	public SimpleResponse(boolean success, int errorCode, String message) {
		this.success = success;
		this.errorCode = errorCode;
		this.message = message;
	}
}