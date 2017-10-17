package com.logiforge.ballo.net.okhttp;

import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
        Response response = null;

        if(postRequest.getBinaryParts() != null) {
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);

            if(postRequest.getStringParts() != null) {
                for (Map.Entry<String, String> entry : postRequest.getStringParts().entrySet()) {
                    bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            for(Map.Entry<String, byte[]> entry : postRequest.getBinaryParts().entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getKey(),
                        RequestBody.create(MEDIA_TYPE_BINARY, entry.getValue()));
            }
            requestBody = bodyBuilder.build();
        } else {
            FormBody.Builder requestBodyBuilder = new FormBody.Builder();
            if(postRequest.getStringParts() != null) {
                for (Map.Entry<String, String> entry : postRequest.getStringParts().entrySet()) {
                    requestBodyBuilder.add(entry.getKey(), entry.getValue());
                }
            }
            requestBody = requestBodyBuilder.build();
        }

        Request okhttpRequest = new Request.Builder()
                .url(postRequest.getUrl())
                .post(requestBody)
                .build();

        okhttp3.Response okhttpResponse = client.newCall(okhttpRequest).execute();

        if (!okhttpResponse.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            String contentType = okhttpResponse.header("Content-Type");
            if(contentType.equals("application/json; charset=UTF-8")) {
                response = new Response(okhttpResponse.body().string());
            } else if(contentType.equals("application/octet-stream")) {
                response = new Response(okhttpResponse.body().byteStream());
            } else {
                throw new IOException("Unknown content type: " + contentType);
            }
        }

        return response;
    }
}
