package com.logiforge.ballo.net;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;


public class JsonWebCall<T> {
	static final String TAG = "JsonWebCall";
	
	private HttpAdaptor httpClient;
	JsonResponseParser<T> responceParser;
	
	public JsonWebCall(HttpAdaptor httpClient, JsonResponseParser<T> responceParser) {
		this.httpClient = httpClient;
		this.responceParser = responceParser;
	}
	
	public JsonWebCall(HttpAdaptor httpClient, Gson gson, Class<T> classOfT) {
		this.httpClient = httpClient;
		this.responceParser = new DefaultJsonResponseParser<T>(classOfT, gson);
	}
	
	public JsonWebCall(HttpAdaptor httpClient, Gson gson, Type type) {
		this.httpClient = httpClient;
		this.responceParser = new DefaultJsonResponseParser<T>(type, gson);
	}
	
	public T makeCall(PostRequest req) throws IOException {
		T response = null;
		
		Response rawResponse = httpClient.execute(req);

        if(rawResponse.getStringResponse() != null) {
            response = responceParser.parseJsonContent(rawResponse.getStringResponse());
        } else {
            throw new IOException("Missing text content");
        }

        return response;
	}
}
