package com.logiforge.ballo.net;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;


public class JsonWebCall<T> {
	static final String TAG = "JsonWebCall";
	
	private HttpAdapter httpClient;
	JsonResponseParser<T> responceParser;
	
	public JsonWebCall(HttpAdapter httpClient, JsonResponseParser<T> responceParser) {
		this.httpClient = httpClient;
		this.responceParser = responceParser;
	}
	
	public JsonWebCall(HttpAdapter httpClient, Gson gson, Class<T> classOfT) {
		this.httpClient = httpClient;
		this.responceParser = new DefaultJsonResponseParser<T>(classOfT, gson);
	}
	
	public JsonWebCall(HttpAdapter httpClient, Gson gson, Type type) {
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
