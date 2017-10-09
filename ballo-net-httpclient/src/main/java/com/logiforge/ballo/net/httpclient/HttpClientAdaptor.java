package com.logiforge.ballo.net.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.logiforge.ballo.net.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by iorlanov on 8/15/17.
 */

public class HttpClientAdaptor implements HttpAdaptor {
    HttpClient client;
    HttpContext httpContext;

    public HttpClientAdaptor(HttpClient client) {
        this.client = client;

    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public Response execute(PostRequest postRequest) throws IOException {

        HttpPost httpPost = new HttpPost(postRequest.getUrl());

        if(postRequest.getBinaryParts() != null) {
            Charset chars = Charset.forName("UTF-8");
            MultipartEntity multiPartEntity = new MultipartEntity();

            if(postRequest.getStringParts() != null) {
                for(Map.Entry<String, String> entry : postRequest.getStringParts().entrySet()) {
                    multiPartEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), chars));
                }
            }

            // binary fields
            for(Map.Entry<String, byte[]> entry : postRequest.getBinaryParts().entrySet()) {
                multiPartEntity.addPart(entry.getKey(), new ByteArrayBody(entry.getValue(), entry.getKey()));
            }

            httpPost.setEntity(multiPartEntity);


        } else {
            List<NameValuePair> params = new ArrayList<>();
            if(postRequest.getStringParts() != null) {
                for (Map.Entry<String, String> entry : postRequest.getStringParts().entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        // Execute HTTP Post Request
        HttpResponse httpResponse = null;
        String contentType = null;

        IOException lastEx = null;
        for(int i = 0; i<postRequest.getAttempts(); i++) {
            try {
                if(httpContext != null) {
                    httpResponse = client.execute(httpPost, httpContext);
                } else {
                    httpResponse = client.execute(httpPost);
                }
                contentType = httpResponse.getEntity().getContentType().getValue();
                lastEx = null;
                break;
            } catch (IOException e) {
                if(httpResponse != null) {
                    HttpEntity entity = httpResponse.getEntity();
                    if(entity != null) {
                        entity.consumeContent();
                    }
                    httpResponse = null;
                }
                lastEx = e;
                try {
                    Thread.sleep(3000);
                } catch(InterruptedException ex) {
                    throw new IOException("Unable to retry a post request - Thread.sleep interrupted.");
                }
            }
        }
        if(lastEx != null) {
            throw lastEx;
        }

        // process response
        Response response = null;

        if(contentType.equals("application/json; charset=UTF-8")) {
            response = new Response(new BasicResponseHandler().handleResponse(httpResponse));
        } else if(contentType.equals("application/octet-stream")) {
            response = new Response(httpResponse.getEntity().getContent());
        } else {
            httpResponse.getEntity().consumeContent();
            throw new IOException("Unknown content type: " + contentType);
        }

        return response;
    }
}
