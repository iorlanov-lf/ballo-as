package com.logiforge.ballo.net.okhttp;

import com.logiforge.ballo.net.*;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

/**
 * Created by iorlanov on 8/16/17.
 */

public class OkHttpAdaptor implements HttpAdaptor {
    private static final MediaType MEDIA_TYPE_BINARY = MediaType.parse("application/octet-stream");

    OkHttpClient client;

    public OkHttpAdaptor (OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(PostRequest postRequest) throws IOException {
        RequestBody requestBody = null;
        Response response = new Response();

        if(postRequest.binaryParts != null && postRequest.binaryParts.size() > 0) {
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);

            if(postRequest.stringParts != null && postRequest.stringParts.size() > 0) {
                for (Map.Entry<String, String> entry : postRequest.stringParts.entrySet()) {
                    bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            for(Map.Entry<String, byte[]> entry : postRequest.binaryParts.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getKey(),
                        RequestBody.create(MEDIA_TYPE_BINARY, entry.getValue()));
            }
            requestBody = bodyBuilder.build();
        } else {
            FormBody.Builder requestBodyBuilder = new FormBody.Builder();
            for(Map.Entry<String, String> entry : postRequest.stringParts.entrySet()) {
                requestBodyBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBody = requestBodyBuilder.build();
        }

        Request okhttpRequest = new Request.Builder()
                .url(postRequest.url)
                .post(requestBody)
                .build();

        okhttp3.Response okhttpResponse = client.newCall(okhttpRequest).execute();

        if (!okhttpResponse.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            String contentType = okhttpResponse.header("Content-Type");
            if(contentType.equals("application/json; charset=UTF-8")) {
                response.stringResponse = okhttpResponse.body().string();
            } else if(contentType.equals("application/octet-stream")) {
                response.binaryResponse = okhttpResponse.body().byteStream();
            } else {
                throw new IOException("Unknown content type: " + contentType);
            }
        }

        return response;
    }
}
