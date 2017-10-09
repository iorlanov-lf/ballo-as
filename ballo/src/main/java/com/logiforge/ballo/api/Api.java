package com.logiforge.ballo.api;

import android.content.Context;

import com.google.gson.Gson;
import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.model.api.LogContext;

public class Api {
	private static HttpAdaptor httpAdaptor = null;
	
	protected Context context;
	protected Gson gson = null; 
	protected LogContext logContext = null;

	protected Api(Context context, LogContext logContext, ApiObjectFactory factory) {
		this.context = context;
		this.logContext = logContext;
		this.gson = factory.getGson();
		if(httpAdaptor == null) {
			httpAdaptor = factory.getHttpAdapter();
		}
	}
	
	protected HttpAdaptor getHttpAdaptor() {
		return httpAdaptor;
	}
	
	protected Gson getGson() {
		return gson;
	}
}
