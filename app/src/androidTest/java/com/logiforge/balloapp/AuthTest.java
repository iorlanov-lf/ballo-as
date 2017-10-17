package com.logiforge.balloapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.AuthApi;
import com.logiforge.ballo.auth.AuthParams;
import com.logiforge.ballo.auth.model.api.SimpleResponse;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by iorlanov on 8/18/17.
 */
public class AuthTest {
    private static final String PAR_OP = "op";
    private static final String PAR_MODE = "mode";
    private static final String PAR_USER_NAME = "userName";
    private static final String PAR_PASSWORD = "password";


    private static final String AUTH_URL = "http://10.0.0.21:8080/auth";
    //private static final String AUTH_URL = "https://ballo-test.appspot.com/auth";

    private static final String HTTP_ADAPTER_CLASS_NAME = "com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder";


    private HttpAdaptor httpAdaptor;
    private HttpAdaptor httpAdaptorWithCookies;

    public AuthTest() throws Exception{
        httpAdaptor = createAdaptor(HTTP_ADAPTER_CLASS_NAME, false);
        httpAdaptorWithCookies = createAdaptor(HTTP_ADAPTER_CLASS_NAME, true);
    }

    @Test
    public void signInAsSuperUserTest() throws Exception {
        Response response = signInAsSuperUser();
        assertNotNull(response.getStringResponse());

        Gson gson = new GsonBuilder().create();
        UserAuthResult authRes = gson.fromJson(response.getStringResponse(), UserAuthResult.class);

        assertTrue(authRes.success);
    }

    @Test
    public void deleteUserTest() throws Exception {
        Response response = signInAsSuperUser();
        assertNotNull(response.getStringResponse());

        response = deleteUser("iorlanov");

        Gson gson = new GsonBuilder().create();
        SimpleResponse simpleResponse = gson.fromJson(response.getStringResponse(), SimpleResponse.class);

        assertTrue(simpleResponse.success);
    }

    @Test
    public void registerUserAndApp() throws Exception {
        deleteUser("iorlanov");

        AuthApi authApi = getAuthApi();
        AuthApi.AuthApiTaskResult authApiTaskResult =
                authApi.registerUser("iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i", "device1", "app1");

        assertTrue(authApiTaskResult.authResult.success);
    }

    @Test
    public void registerApp() throws Exception {
        deleteUser("iorlanov");

        AuthApi authApi = getAuthApi();
        AuthApi.AuthApiTaskResult authApiTaskResult =
                authApi.registerUser("iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i", "device1", "app1");
        assertTrue(authApiTaskResult.authResult.success);

        authApiTaskResult =
                authApi.registerApp("iorlanov", "1111111i", "device2", "app2");
        assertTrue(authApiTaskResult.authResult.success);
    }

    @NonNull
    private AuthApi getAuthApi() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        LogContext logContext = new LogContext("test", "registerUserAndApp");
        ApiObjectFactory apiObjFactory = new ApiObjectFactory() {
            @Override
            public HttpAdaptor getHttpAdapter() {
                return httpAdaptor;
            }

            @Override
            public Gson getGson() {
                return new GsonBuilder().create();
            }
        };

        AuthParams authParams = new AuthParams() {
            @Override
            public String getAuthUrl(String op) {
                return AUTH_URL;
            }

            @Override
            public String getGmsSenderId() {
                return "1234";
            }
        };

        return new AuthApi(appContext, logContext, apiObjFactory, authParams, false, null);
    }

    private Response signInAsSuperUser() throws java.io.IOException {
        PostRequest postRequest = new PostRequest(AUTH_URL, 1);
        postRequest.addStringPart(PAR_OP, "signin");
        postRequest.addStringPart(PAR_MODE, "lf_id");
        postRequest.addStringPart(PAR_USER_NAME, "su");
        postRequest.addStringPart(PAR_PASSWORD, "TC_LF_1963");

        return httpAdaptorWithCookies.execute(postRequest);
    }

    private Response deleteUser(String userName) throws java.io.IOException {
        signInAsSuperUser();

        PostRequest postRequest = new PostRequest(AUTH_URL, 1);
        postRequest.addStringPart(PAR_OP, "delete_user");
        postRequest.addStringPart(PAR_MODE, "session");
        postRequest.addStringPart(PAR_USER_NAME, userName);

        return httpAdaptorWithCookies.execute(postRequest);
    }

    private HttpAdaptor createAdaptor(String adaptorBuilderClassName, boolean useCookies) throws Exception {
        Class<?> adaptorClass = Class.forName(adaptorBuilderClassName);
        Constructor<?> ctor = adaptorClass.getConstructor();
        HttpAdaptorBuilder adaptorBuilder =  (HttpAdaptorBuilder)ctor.newInstance();
        if(useCookies) {
            adaptorBuilder.useCookies();
        }
        return adaptorBuilder.build();
    }
}
