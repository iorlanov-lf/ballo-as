package com.logiforge.ballo.api;

import android.content.Context;

import com.google.gson.Gson;
import com.logiforge.ballo.net.HttpAdapter;
import com.logiforge.ballo.model.api.LogContext;

public class Api {
	private static HttpAdapter httpAdapter = null;
	
	protected Context context;
	protected Gson gson = null; 
	protected LogContext logContext = null;

	protected Api(Context context, LogContext logContext, ApiObjectFactory factory) {
		this.context = context;
		this.logContext = logContext;
		this.gson = factory.getGson();
		if(httpAdapter == null) {
			httpAdapter = factory.getHttpAdapter();
		}
	}
	
	protected HttpAdapter getHttpAdaptor() {
		return httpAdapter;
	}
	
	protected Gson getGson() {
		return gson;
	}
}
