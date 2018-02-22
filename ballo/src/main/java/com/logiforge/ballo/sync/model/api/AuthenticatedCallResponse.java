package com.logiforge.ballo.sync.model.api;

import com.logiforge.ballo.model.api.ApiAuthOutcome;
import com.logiforge.ballo.model.api.SimpleResponse;

public class AuthenticatedCallResponse<T> extends SimpleResponse {
	public int appAuthResult = ApiAuthOutcome.AA_NOT_AUTHENTICATED;
	public T workload = null;
	
	public AuthenticatedCallResponse() {
		super();
	}
	
	public AuthenticatedCallResponse(boolean success, int errorCode, String message, int appAuthResult, T workload) {
		super(success, errorCode, message);
		this.appAuthResult = appAuthResult;
		this.workload = workload;
	}
}
