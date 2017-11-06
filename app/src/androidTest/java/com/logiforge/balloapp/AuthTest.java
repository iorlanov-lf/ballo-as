package com.logiforge.balloapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.auth.AuthParams;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.DefaultAuthFacade;
import com.logiforge.ballo.auth.model.api.SimpleResponse;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.DaoContext;
import com.logiforge.ballo.dao.sqlite.SqliteDaoContext;
import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;
import com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static junit.framework.Assert.fail;
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

    private static final String GCM_SENDER_ID = "662722394825";

    private static final String HTTP_ADAPTER_CLASS_NAME = "com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder";

    private static Context appContext;
    private static HttpAdaptor httpAdaptorWithCookies;

    @BeforeClass
    public static void classStartUp() {
        try {
            appContext = InstrumentationRegistry.getTargetContext();
            Assert.assertNotNull(appContext);
            assertEquals("com.logiforge.balloapp", appContext.getPackageName());

            BalloAppSQLiteOpenHelper balloAppSQLiteOpenHelper = new BalloAppSQLiteOpenHelper(appContext);
            SQLiteDatabase sqliteDb = balloAppSQLiteOpenHelper.getWritableDatabase();
            DaoContext daoContext = new SqliteDaoContext(sqliteDb);

            ApiObjectFactory apiObjFactory = new ApiObjectFactory() {
                @Override
                public HttpAdaptor getHttpAdapter() {
                    HttpAdaptorBuilder httpAdaptorBuilder = new OkHttpAdaptorBuilder();
                    return httpAdaptorBuilder.build();
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
                public String getGcmSenderId() {
                    return GCM_SENDER_ID;
                }
            };

            AuthFacade authFacade = new DefaultAuthFacade(authParams, apiObjFactory);
            Ballo.init(daoContext, authFacade);

            AppIdentityDao appIdentityDao = daoContext.getDao(AppIdentityDao.class);
            AppIdentity appIdentity = appIdentityDao.getAppIdentity();
            Assert.assertNotNull(appIdentity);
            Assert.assertNotNull(appIdentity.getAppId());

            String appId = Ballo.authFacade().getAppId();
            Assert.assertNotNull(appId);

            httpAdaptorWithCookies = createAdaptor(HTTP_ADAPTER_CLASS_NAME, true);
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
    }

    private static HttpAdaptor createAdaptor(String adaptorBuilderClassName, boolean useCookies) throws Exception {
        Class<?> adaptorClass = Class.forName(adaptorBuilderClassName);
        Constructor<?> ctor = adaptorClass.getConstructor();
        HttpAdaptorBuilder adaptorBuilder =  (HttpAdaptorBuilder)ctor.newInstance();
        if(useCookies) {
            adaptorBuilder.useCookies();
        }
        return adaptorBuilder.build();
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

        RegistrationOperationResult result = Ballo.authFacade().registerUser(appContext, null, "iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i");

        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);
    }

    @Test
    public void registerApp() throws Exception {
        deleteUser("iorlanov");

        RegistrationOperationResult result = Ballo.authFacade().registerUser(appContext, null, "iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i");
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);

        result = Ballo.authFacade().registerApp(appContext, null, "iorlanov", "1111111i");
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);
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


}
