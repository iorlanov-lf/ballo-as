package com.logiforge.balloapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.DefaultAuthFacade;
import com.logiforge.ballo.auth.model.api.RegisterAppParams;
import com.logiforge.ballo.auth.model.api.RegisterUserParams;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.dao.WorkflowDao;
import com.logiforge.ballo.model.api.SimpleResponse;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.model.db.Workflow;
import com.logiforge.ballo.net.HttpAdapter;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.net.Response;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.facade.SyncAuthEventHandler;
import com.logiforge.ballo.sync.facade.SyncFacade;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.balloapp.dao.PostalCodeDao;
import com.logiforge.balloapp.dao.PostalCodesDao;
import com.logiforge.balloapp.dao.UserPostalCodeDao;
import com.logiforge.balloapp.facade.RegistrationFacade;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCode;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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

    final String WF_BALLOAPP_REGISTER_USER = "BalloApp.RegisterUser";
    final String WF_BALLOAPP_REGISTER_APP = "BalloApp.RegisterApp";

    private static Context appContext;
    private static HttpAdapter httpAdapterWithCookies;

    private static RegisterUserParams registerIorlanovParams = new RegisterUserParams("iorlanov", "iorlanov@comcast.net", "Igor Orlanov", "1111111i");
    private static RegisterAppParams registerIorlanovAppParams = new RegisterAppParams("iorlanov", "1111111i");

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

        AppSubscriptionDao appSubscriptionDao = Ballo.db().getDao(AppSubscription.class);
        List<AppSubscription> subscriptions = appSubscriptionDao.getAllSubscriptions();
        assertEquals(0, subscriptions.size());
    }

    @Test
    public void registerUser() throws Exception {
        final int WFS_REGISTERED_USER = 10;
        final int WFS_PERSISTED_USER_POSTAL_CODES = 20;

        deleteUser("iorlanov");

        WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
        workflowDao.deleteAll();
        Workflow appWorkflow = new Workflow(WF_BALLOAPP_REGISTER_USER, null, null, null);

        // REGISTER USER
        RegistrationOperationResult result = Ballo.authFacade().registerUser(
                appContext, null, registerIorlanovParams, appWorkflow);

        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);
        assertNotNull(result.workflow);
        assertTrue(result.success);

        appWorkflow.state = WFS_REGISTERED_USER;

        // auth wflow
        Workflow transientRegUserWorkflow = appWorkflow.findChildWorkflow(AuthFacade.WF_REGISTER_USER);
        assertNotNull(transientRegUserWorkflow);
        Workflow persistedRegUserWorkflow = workflowDao.find(AuthFacade.WF_REGISTER_USER);
        assertNotNull(persistedRegUserWorkflow);
        assertEquals(transientRegUserWorkflow.state, persistedRegUserWorkflow.state);
        assertEquals(transientRegUserWorkflow.state, (Integer) DefaultAuthFacade.RegisterUserState.INVOKED_EVENT_HANDLERS);

        // sync event handler wflow
        Workflow transientSyncOnRegUserWorkflow = appWorkflow.findChildWorkflow(SyncAuthEventHandler.WF_ON_REGISTER_USER);
        assertNotNull(transientSyncOnRegUserWorkflow);
        Workflow persistentSyncOnRegUserWorkflow = workflowDao.find(SyncAuthEventHandler.WF_ON_REGISTER_USER);
        assertNotNull(persistentSyncOnRegUserWorkflow);
        assertEquals(transientSyncOnRegUserWorkflow.state, persistentSyncOnRegUserWorkflow.state);
        assertEquals(transientSyncOnRegUserWorkflow.state, (Integer) SyncAuthEventHandler.OnRegisterUserState.GOT_SUBSCRIPTION_DATA);

        // database
        AppSubscriptionDao appSubscriptionDao = Ballo.db().getDao(AppSubscription.class);
        List<AppSubscription> subscriptions = appSubscriptionDao.getAllSubscriptions();
        assertEquals(1, subscriptions.size());

        PostalCodesDao postalCodesDao = new PostalCodesDao();
        assertEquals(1, postalCodesDao.count());

        PostalCodeDao postalCodeDao = new PostalCodeDao();
        assertEquals(42613, postalCodeDao.count());

        // PERSIST USER POSTAL CODES
        RegistrationFacade registrationFacade = new RegistrationFacade();
        List<String> userPostalCodes = new ArrayList<>();
        userPostalCodes.add("30092");
        boolean isSuccess = registrationFacade.persistUserPostalCodes("iorlanov", userPostalCodes);
        assertTrue(isSuccess);
        appWorkflow.state = WFS_PERSISTED_USER_POSTAL_CODES;
        workflowDao.save(appWorkflow);

        // SYNCHRONIZE USER POSTAL CODES
        SyncFacade syncFacade = Ballo.syncFacade();


        // SUBSCRIBE FOR FACILITIES
        //SyncFacade syncFacade = Ballo.syncFacade().subscribe(PostalCodeFacilities.class.getSimpleName(), "30092");


    }

    @Test
    public void registerApp() throws Exception {
        deleteUser("iorlanov");

        // set the app id
        AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
        appIdentityDao.setAppId("firstAppId");
        Ballo.authFacade().init();

        WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
        workflowDao.deleteAll();
        Workflow appWorkflow = new Workflow(WF_BALLOAPP_REGISTER_USER, null, null, null);

        RegistrationOperationResult result = Ballo.authFacade().registerUser(appContext, null, registerIorlanovParams, appWorkflow);
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);

        Workflow persistedWorkflow = workflowDao.find(AuthFacade.WF_REGISTER_USER);
        assertNotNull(persistedWorkflow);
        assertEquals(result.workflow.state, persistedWorkflow.state);
        assertEquals(result.workflow.state, (Integer) DefaultAuthFacade.RegisterUserState.INVOKED_EVENT_HANDLERS);

        // change the app id
        appIdentityDao.setAppId("anotherAppId");
        Ballo.authFacade().init();

        // register new app
        workflowDao.deleteAll();
        Workflow appWorkflow2 = new Workflow(WF_BALLOAPP_REGISTER_APP, null, null, null);

        result = Ballo.authFacade().registerApp(appContext, null, registerIorlanovAppParams, appWorkflow2);
        assertNotNull(result);
        assertNotNull(result.authResult);
        assertTrue(result.authResult.success);

        persistedWorkflow = workflowDao.find(AuthFacade.WF_REGISTER_APP);
        assertNotNull(persistedWorkflow);
        assertEquals(result.workflow.state, persistedWorkflow.state);
        assertEquals(result.workflow.state, (Integer) DefaultAuthFacade.RegisterUserState.INVOKED_EVENT_HANDLERS);

        Workflow transientSyncOnRegAppWorkflow = result.workflow.findChildWorkflow(SyncAuthEventHandler.WF_ON_REGISTER_APP);
        assertNotNull(transientSyncOnRegAppWorkflow);
        Workflow persistentSyncOnRegAppWorkflow = workflowDao.find(SyncAuthEventHandler.WF_ON_REGISTER_APP);
        assertNotNull(persistentSyncOnRegAppWorkflow);
        assertEquals(transientSyncOnRegAppWorkflow.state, persistentSyncOnRegAppWorkflow.state);
        assertEquals(transientSyncOnRegAppWorkflow.state, (Integer) SyncAuthEventHandler.OnRegisterAppState.GOT_REMOTE_SUBSCRIPTIONS);

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
