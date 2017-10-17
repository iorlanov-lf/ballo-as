package com.logiforge.ballo.auth.model.api;

public enum UserAuthOutcome {
	Success(0, "SUCCESS"),
	InternalError(1, "Internal error"),
	UserExists(2, "User already exists"),
	InvalidCredentials(3, "User name or password is invalid"),
	DuplicateEmail(4, "Duplicate email");

	public int getErrorNumber() {
		return errorNumber;
	}

	public String getMessage() {
		return message;
	}

	private int errorNumber;
	private String message;
	
	UserAuthOutcome(int errorNumber, String message) {
		this.errorNumber = errorNumber;
		this.message = message;
	}
}