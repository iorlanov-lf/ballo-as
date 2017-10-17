package com.logiforge.ballo.auth.model.api;

public class ApiAuthResult {
	public static final int AA_NOT_AUTHORIZED = 0;
	public static final int AA_EXPIRED = 1;
	public static final int AA_AUTHORIZED = 3;
	
	public int outcome;

	public ApiAuthResult(int outcome) {
		this.outcome = outcome;
	}
}
