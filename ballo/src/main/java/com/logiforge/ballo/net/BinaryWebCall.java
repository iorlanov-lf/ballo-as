package com.logiforge.ballo.net;

import com.google.gson.Gson;
import com.logiforge.ballo.sync.protocol.conversion.ObjectConverter;

import java.io.IOException;
import java.lang.reflect.Type;


public class BinaryWebCall<T> {
	static final String TAG = "JsonWebCall";

	private HttpAdapter httpClient;
	ObjectConverter objectConverter;

	public BinaryWebCall(HttpAdapter httpClient, ObjectConverter objectConverter) {
		this.httpClient = httpClient;
		this.objectConverter = objectConverter;
	}
	
	public T makeCall(PostRequest req) throws IOException {
		T response = null;
		
		Response rawResponse = httpClient.execute(req);

        if(rawResponse.getBinaryResponse() != null) {
            response = (T)objectConverter.fromInputStream(rawResponse.getBinaryResponse());
        } else {
            throw new IOException("Missing binary content");
        }

        return response;
	}
}
