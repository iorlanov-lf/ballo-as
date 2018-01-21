package com.logiforge.balloapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.model.api.SimpleResponse;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.net.HttpAdapter;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by iorlanov on 8/18/17.
 */
public class AuthTest {
    private static final String PAR_OP = "op";
    private static final String PAR_MODE = "mode";
    private static final String PAR_USER_NAME = "userName";
    private static final String PAR_PASSWORD = "password";

    private static final String HTTP_ADAPTER_CLASS_NAME = "com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder";

    private static Context appContext;
    private static HttpAdapter httpAdapterWithCookies;

    @BeforeClass
    public static void classStartUp() {
        try {
            appContext = InstrumentationRegistry.getTargetContext();
            Assert.assertNotNull(appContext);
            assertEquals("com.logiforge.balloapp", appContext.getPackageName());

            if(Ballo.db() == null) {
                BalloInitializer.init(appContext);
            }

            AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
            AppIdentity appIdentity = appIdentityDao.getAppIdentity();
            Assert.assertNotNull(appIdentity);
            Assert.assertNotNull(appIdentity.getAppId());

            String appId = Ballo.authFacade().getAppId();
            Assert.assertNotNull(appId);

            httpAdapterWithCookies = createAdaptor(HTTP_ADAPTER_CLASS_NAME, true);
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
    }

    private static HttpAdapter createAdaptor(String adaptorBuilderClassName, boolean useCookies) throws Exception {
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

        AppSubscriptionDao appSubscriptionDao = Ballo.db().getDao(AppSubscription.class);
        List<AppSubscription> subscriptions = appSubscriptionDao.getAllSubscriptions();
        assertEquals(1, subscriptions.size());
    }

    @Test
    public void registerApp() throws Exception {
        deleteUser("iorlanov");

        // set the app id
        AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
        appIdentityDao.setAppId("firstAppId");
        Ballo.authFacade().init();

        RegistrationOperationResult result = Ballo.authFacade().registerUser(appContext, null, "iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i");
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);

        // change the app id
        appIdentityDao.setAppId("anotherAppId");
        Ballo.authFacade().init();

        // register new app
        result = Ballo.authFacade().registerApp(appContext, null, "iorlanov", "1111111i");
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);
    }

    private Response signInAsSuperUser() throws java.io.IOException {
        PostRequest postRequest = new PostRequest(BalloInitializer.AUTH_URL, 1);
        postRequest.addStringPart(PAR_OP, "signin");
        postRequest.addStringPart(PAR_MODE, "lf_id");
        postRequest.addStringPart(PAR_USER_NAME, "su");
        postRequest.addStringPart(PAR_PASSWORD, "TC_LF_1963");

        return httpAdapterWithCookies.execute(postRequest);
    }

    private Response deleteUser(String userName) throws java.io.IOException {
        signInAsSuperUser();

        PostRequest postRequest = new PostRequest(BalloInitializer.AUTH_URL, 1);
        postRequest.addStringPart(PAR_OP, "delete_user");
        postRequest.addStringPart(PAR_MODE, "session");
        postRequest.addStringPart(PAR_USER_NAME, userName);

        return httpAdapterWithCookies.execute(postRequest);
    }


}
