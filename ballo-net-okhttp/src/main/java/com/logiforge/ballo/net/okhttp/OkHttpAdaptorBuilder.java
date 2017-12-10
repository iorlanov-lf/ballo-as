package com.logiforge.ballo.net.okhttp;

import com.logiforge.ballo.net.HttpAdapter;
import com.logiforge.ballo.net.HttpAdaptorBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by iorlanov on 8/29/17.
 */

public class OkHttpAdaptorBuilder implements HttpAdaptorBuilder {
    boolean useCookies = false;

    @Override
    public HttpAdaptorBuilder parameter(String name, String value) {

        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, int value) {

        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, long value) {
        return this;
    }

    @Override
    public HttpAdaptorBuilder parameter(String name, boolean value) {

        return this;
    }

    @Override
    public HttpAdaptorBuilder useCookies() {
        useCookies = true;
        return this;
    }

    @Override
    public HttpAdapter build() {
        OkHttpClient.Builder rawBuilder = new OkHttpClient.Builder();
        if(useCookies) {
            rawBuilder.cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
                    cookieStore.put(httpUrl, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            });
        }
        OkHttpClient client = rawBuilder.build();

        return new OkHttpAdapter(client);
    }
}
