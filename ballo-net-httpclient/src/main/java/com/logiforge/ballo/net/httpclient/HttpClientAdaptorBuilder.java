package com.logiforge.ballo.net.httpclient;

import android.net.http.AndroidHttpClient;

import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.HttpAdaptorBuilder;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Created by iorlanov on 8/29/17.
 */

public class HttpClientAdaptorBuilder implements HttpAdaptorBuilder {
    public static final String PNAME_INSTANCE_NAME = "instance.name";
    public static final String PNAME_CONNECTION_STALECHECK = "http.connection.stalecheck";
    public static final String PNAME_CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String PNAME_SOCKET_TIMEOUT = "http.socket.timeout";

    String instanceName = "httpClientInstance";
    boolean httpConnectionStalecheck = true;
    int httpConnectionTimeout = 5 * 1000;
    int httpSocketTimeout = 1 * 60 * 1000 + 5 * 1000;
    boolean useCookies = false;

    @Override
    public HttpAdaptorBuilder parameter(String name, String value) {
        if(name.equalsIgnoreCase(PNAME_INSTANCE_NAME)) {
            instanceName = value;
        }

        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, int value) {
        if(name.equalsIgnoreCase(PNAME_CONNECTION_TIMEOUT)) {
            httpConnectionTimeout = value;
        } else if(name.equalsIgnoreCase(PNAME_SOCKET_TIMEOUT)) {
            httpSocketTimeout = value;
        }

        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, long value) {
        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, boolean value) {
        if(name.equalsIgnoreCase(PNAME_CONNECTION_STALECHECK)) {
            httpConnectionStalecheck = value;
        }

        return this;
    }

    @Override
    public HttpAdaptorBuilder useCookies() {
        useCookies = true;
        return this;
    }

    @Override
    public HttpAdaptor build() {

        HttpClient httpClient = AndroidHttpClient.newInstance(instanceName);
        httpClient.getParams().setBooleanParameter(PNAME_CONNECTION_STALECHECK, httpConnectionStalecheck);
        httpClient.getParams().setIntParameter(PNAME_CONNECTION_TIMEOUT, httpConnectionTimeout);
        httpClient.getParams().setIntParameter(PNAME_SOCKET_TIMEOUT, httpSocketTimeout);

        HttpClientAdaptor httpClientAdaptor = new HttpClientAdaptor(httpClient);
        if(useCookies) {
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            httpClientAdaptor.setHttpContext(httpContext);
        }

        return httpClientAdaptor;
    }
}
