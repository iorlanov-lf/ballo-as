package com.logiforge.ballo.net;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

public class DefaultJsonResponseParser<T> implements JsonResponseParser<T> {
	private static final String TAG = DefaultJsonResponseParser.class.getSimpleName();
	
	private Gson gson;
	private Class<T> classOfT;
	private Type type;
	
	public DefaultJsonResponseParser(Class<T> classOfT, Gson gson) {
		this.classOfT = classOfT;
		this.gson = gson;
	}
	
	public DefaultJsonResponseParser(Type type, Gson gson) {
		this.type = type;
		this.gson = gson;
	}

	@Override
	public T parseJsonContent(String content) throws IOException {
		T response = null;
		
		if(content != null && content.length() > 0) {
			if (type == null) {
				response = gson.fromJson(content, classOfT);
			} else {
				response = gson.fromJson(content, type);
			}
		}
		
		return response;
	}
}