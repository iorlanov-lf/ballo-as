package com.logiforge.ballo.auth.model.api;

public class UserAuthResult extends SimpleResponse {

	public UserAuthOutcome outcome;
	public Boolean isRegistered;
	public LFUser lfUser;
	public AuthTokens authTokens;

	public UserAuthResult() {

	}

	public UserAuthResult(UserAuthOutcome outcome, LFUser lfUser) {
		super(outcome == UserAuthOutcome.Success?true:false, outcome.getErrorNumber(), outcome.getMessage());
		this.outcome = outcome;
		this.lfUser = lfUser;
		this.isRegistered = null;
		this.authTokens = null;
	}
	
	public UserAuthResult(UserAuthOutcome outcome, LFUser lfUser, boolean isRegistered) {
		super(outcome == UserAuthOutcome.Success?true:false, outcome.getErrorNumber(), outcome.getMessage());
		this.outcome = outcome;
		this.lfUser = lfUser;
		this.isRegistered = isRegistered;
		this.authTokens = null;
	}
}