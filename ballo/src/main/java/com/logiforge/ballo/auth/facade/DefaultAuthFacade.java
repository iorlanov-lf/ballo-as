package com.logiforge.ballo.auth.facade;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.iid.InstanceID;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.BalloPersistentState;
import com.logiforge.ballo.api.ApiCallBack;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.api.AuthApi;
import com.logiforge.ballo.auth.dao.AuthDaoInitializer;
import com.logiforge.ballo.auth.model.api.AuthTokens;
import com.logiforge.ballo.auth.model.api.RegisterAppParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithFacebookParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithGoogleParams;
import com.logiforge.ballo.auth.model.api.RegisterUserParams;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.auth.api.AuthParams;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.dao.WorkflowDao;
import com.logiforge.ballo.model.api.ApiAuthOutcome;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.BalloLog;
import com.logiforge.ballo.model.db.Workflow;
import com.logiforge.ballo.net.PostRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by iorlanov on 10/21/17.
 */

public class DefaultAuthFacade implements AuthFacade {
    static final String TAG = AuthFacade.class.getSimpleName();

    public static final String LJ_AUTH = "Auth";
    public static final String LT_REG_USER = "RegUser";
    public static final String LT_REG_APP = "RegApp";
    public static final String LT_REG_APP_FACEBOOK = "RegAppFacebook";
    public static final String LT_REG_APP_GOOGLE = "RegAppGoogle";

    static public class RegisterUserState {
        public static final int REGISTERED_WITH_GSM = 10;
        public static final int REGISTERED_USER = 20;
        public static final int INVOKED_EVENT_HANDLERS = 30;
    }

    static public class RegisterAppState {
        public static final int REGISTERED_WITH_GSM = 10;
        public static final int REGISTERED_APP = 20;
        public static final int INVOKED_EVENT_HANDLERS = 30;
    }

    static public class RegisterAppFacebookState {
        public static final int REGISTERED_WITH_GSM = 10;
        public static final int REGISTERED_APP = 20;
        public static final int INVOKED_EVENT_HANDLERS = 30;
    }

    static public class RegisterAppGoogleState {
        public static final int REGISTERED_WITH_GSM = 10;
        public static final int REGISTERED_APP = 20;
        public static final int INVOKED_EVENT_HANDLERS = 30;
    }

    protected AuthDaoInitializer daoInitializer;
    protected AppIdentity appIdentity;
    protected AuthParams authParams;
    protected ApiObjectFactory apiObjectFactory;
    protected List<AuthEventHandler> eventHandlers = new ArrayList<>();

    public DefaultAuthFacade(AuthDaoInitializer daoInitializer, AuthParams authParam, ApiObjectFactory apiObjectFactory) {
        this.daoInitializer = daoInitializer;
        this.authParams = authParam;
        this.apiObjectFactory = apiObjectFactory;
    }

    @Override
    public void init() throws Exception {
        daoInitializer.init();

        AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
        appIdentity = appIdentityDao.getAppIdentity();
        if(appIdentity.getAppId() == null) {
            String appId = UUID.randomUUID().toString();
            appIdentityDao.setAppId(appId);
            appIdentity.setAppId(appId);
        }
    }

    @Override
    public String getAppId() {
        return appIdentity.getAppId();
    }

    @Override
    public RegistrationOperationResult registerUser(Context context, ApiCallBack callBack,
                                                    RegisterUserParams registerUserParams) throws Exception {
        registerUserParams.appId = appIdentity.getAppId();
        Workflow workflow = new Workflow(WF_REGISTER_USER, null, null);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_USER);
        OperationContext opContext = new OperationContext(context, logContext, callBack, false, workflow);

        if(callBack == null) {
            RegistrationOperationResult result = execRegisterUser(opContext, registerUserParams);
            result.workflow = workflow;
            return result;
        } else {
            RegisterUserTask registerUserTask = new RegisterUserTask(opContext, registerUserParams);
            registerUserTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult resumeRegisterUser(Context context, ApiCallBack callBack, Workflow workflow) {

        if(workflow == null) {
            WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
            workflow = workflowDao.find(WF_REGISTER_USER);
        }

        RegisterUserParams registerUserParams = apiObjectFactory.getGson().fromJson(workflow.clob, RegisterUserParams.class);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_USER);
        OperationContext opContext = new OperationContext(context, logContext, callBack, true, workflow);

        if(callBack == null) {
            return execRegisterUser(opContext, registerUserParams);
        } else {
            RegisterUserTask registerUserTask = new RegisterUserTask(opContext, registerUserParams);
            registerUserTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult registerApp(Context context, ApiCallBack callBack, RegisterAppParams registerAppParams) throws Exception {
        registerAppParams.appId = appIdentity.getAppId();
        Workflow workflow = new Workflow(WF_REGISTER_APP, null, null);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP);
        OperationContext opContext = new OperationContext(context, logContext, callBack, false, workflow);

        if(callBack == null) {
            RegistrationOperationResult result = execRegisterApp(opContext, registerAppParams);
            result.workflow = workflow;
            return result;
        } else {
            RegisterAppTask registerAppTask = new RegisterAppTask(opContext, registerAppParams);
            registerAppTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult resumeRegisterApp(Context context, ApiCallBack callBack, Workflow workflow) {

        if(workflow == null) {
            WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
            workflow = workflowDao.find(WF_REGISTER_APP);
        }

        RegisterAppParams registerAppParams = apiObjectFactory.getGson().fromJson(workflow.clob, RegisterAppParams.class);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP);
        OperationContext opContext = new OperationContext(context, logContext, callBack, true, workflow);

        if(callBack == null) {
            return execRegisterApp(opContext, registerAppParams);
        } else {
            RegisterAppTask registerAppTask = new RegisterAppTask(opContext, registerAppParams);
            registerAppTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult registerAppWithFacebook(Context context, ApiCallBack callBack, RegisterAppWithFacebookParams params) throws Exception {
        params.appId = appIdentity.getAppId();
        Workflow workflow = new Workflow(WF_REGISTER_APP_FACEBOOK, null, null);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP_FACEBOOK);
        OperationContext opContext = new OperationContext(context, logContext, callBack, false, workflow);

        if(callBack == null) {
            return execRegisterAppWithFacebook(opContext, params);
        } else {
            RegisterAppWithFacebookTask registerAppWithFacebookTask = new RegisterAppWithFacebookTask(opContext, params);
            registerAppWithFacebookTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult resumeRegisterAppWithFacebook(Context context, ApiCallBack callBack, Workflow workflow) {

        if(workflow == null) {
            WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
            workflow = workflowDao.find(WF_REGISTER_APP_FACEBOOK);
        }

        RegisterAppWithFacebookParams params = apiObjectFactory.getGson().fromJson(workflow.clob, RegisterAppWithFacebookParams.class);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP_FACEBOOK);
        OperationContext opContext = new OperationContext(context, logContext, callBack, true, workflow);

        if(callBack == null) {
            return execRegisterAppWithFacebook(opContext, params);
        } else {
            RegisterAppWithFacebookTask registerAppTask = new RegisterAppWithFacebookTask(opContext, params);
            registerAppTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult registerAppWithGoogle(Context context, ApiCallBack callBack, RegisterAppWithGoogleParams params) throws Exception {
        params.appId = appIdentity.getAppId();
        Workflow workflow = new Workflow(WF_REGISTER_APP_GOOGLE, null, null);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP_GOOGLE);
        OperationContext opContext = new OperationContext(context, logContext, callBack, false, workflow);


        if(callBack == null) {
            return execRegisterAppWithGoogle(opContext, params);
        } else {
            RegisterAppWithGoogleTask registerAppWithGoogleTask = new RegisterAppWithGoogleTask(opContext, params);
            registerAppWithGoogleTask.execute();
            return null;
        }
    }

    @Override
    public RegistrationOperationResult resumeRegisterAppWithGoogle(Context context, ApiCallBack callBack, Workflow workflow) {

        if(workflow == null) {
            WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
            workflow = workflowDao.find(WF_REGISTER_APP_GOOGLE);
        }

        RegisterAppWithGoogleParams params = apiObjectFactory.getGson().fromJson(workflow.clob, RegisterAppWithGoogleParams.class);
        LogContext logContext = new LogContext(LJ_AUTH, LT_REG_APP_GOOGLE);
        OperationContext opContext = new OperationContext(context, logContext, callBack, true, workflow);

        if(callBack == null) {
            return execRegisterAppWithGoogle(opContext, params);
        } else {
            RegisterAppWithGoogleTask registerAppTask = new RegisterAppWithGoogleTask(opContext, params);
            registerAppTask.execute();
            return null;
        }
    }

    @Override
    public void registerEventHandler(AuthEventHandler authEventHandler) {
        eventHandlers.add(authEventHandler);
    }

    @Override
    public boolean isCloudRegistered() {
        return appIdentity.getUserName() != null;
    }

    @Override
    public void addApiAuthenticationData(PostRequest postRequest) {
        postRequest.addStringPart("appId", appIdentity.getAppId());
        postRequest.addStringPart("appAccessToken", appIdentity.getAccessToken());
    }

    @Override
    public boolean onAuthenticatedCallFailure(Context context, int apiAuthenticationOutcome) throws Exception {

        if(apiAuthenticationOutcome == ApiAuthOutcome.AA_EXPIRED) {
            LogContext logContext = new LogContext("Auth", "GetAccessToken");

            AuthApi authApi = new AuthApi(context, logContext, apiObjectFactory, authParams);
            AuthTokens authTokens = authApi.getAccessToken(appIdentity.getAppId(), appIdentity.getRefreshToken());

            if(authTokens != null) {
                AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
                appIdentityDao.updateAuthToken(authTokens.getAccessToken());
                appIdentity.setAccessToken(authTokens.getAccessToken());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @NonNull
    private RegistrationOperationResult execRegisterUser(OperationContext opContext, RegisterUserParams registerUserParams) {
        RegistrationOperationResult result = new RegistrationOperationResult();
        Workflow workflow = opContext.workflow;

        try {
            if(!opContext.skipStep(RegisterUserState.REGISTERED_WITH_GSM)) {
                if (!registerWithGcm(opContext.context, result)) {
                    throw new Exception("Unable to register with GCM");
                } else {
                    registerUserParams.gcmId = result.gcmId;
                    workflow.state = RegisterUserState.REGISTERED_WITH_GSM;
                }
            }

            if(!opContext.skipStep(RegisterUserState.REGISTERED_USER)) {
                AuthApi authApi = new AuthApi(opContext.context, opContext.logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerUserAndApp(registerUserParams);

                if (result.isUserAuthenticated()) {
                    appIdentity = updateAuthenticationData(result);
                    workflow.state = RegisterUserState.REGISTERED_USER;
                } else {
                    throw new Exception("Unable to register user");
                }
            }

            if(!opContext.skipStep(RegisterUserState.INVOKED_EVENT_HANDLERS)) {
                for (AuthEventHandler authEventHandler : eventHandlers) {
                    authEventHandler.onRegisterUser(opContext, appIdentity);
                }
                workflow.state = RegisterUserState.INVOKED_EVENT_HANDLERS;
            }


        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            opContext.logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(opContext.logContext);

        opContext.workflow.clob = apiObjectFactory.getGson().toJson(registerUserParams);
        saveWorkflow(opContext.workflow);

        return result;
    }

    @NonNull
    private RegistrationOperationResult execRegisterApp(OperationContext opContext, RegisterAppParams registerAppParams) {
        RegistrationOperationResult result = new RegistrationOperationResult();
        Workflow workflow = opContext.workflow;

        try {
            if(!opContext.skipStep(RegisterAppState.REGISTERED_WITH_GSM)) {
                if (!registerWithGcm(opContext.context, result)) {
                    throw new Exception("Unable to register with GCM");
                } else {
                    registerAppParams.gcmId = result.gcmId;
                    workflow.state = RegisterAppState.REGISTERED_WITH_GSM;
                }
            }

            if(!opContext.skipStep(RegisterAppState.REGISTERED_APP)) {
                AuthApi authApi = new AuthApi(opContext.context, opContext.logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerApp(registerAppParams);

                if (result.isUserAuthenticated()) {
                    appIdentity = updateAuthenticationData(result);
                    workflow.state = RegisterAppState.REGISTERED_APP;
                } else {
                    throw new Exception("Unable to register user");
                }
            }

            if(!opContext.skipStep(RegisterAppState.INVOKED_EVENT_HANDLERS)) {
                for (AuthEventHandler authEventHandler : eventHandlers) {
                    authEventHandler.onRegisterApp(opContext, appIdentity);
                }
                workflow.state = RegisterAppState.INVOKED_EVENT_HANDLERS;
            }

        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            opContext.logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(opContext.logContext);

        opContext.workflow.clob = apiObjectFactory.getGson().toJson(registerAppParams);
        saveWorkflow(opContext.workflow);

        return result;
    }

    @NonNull
    private RegistrationOperationResult execRegisterAppWithFacebook(OperationContext opContext, RegisterAppWithFacebookParams params) {
        RegistrationOperationResult result = new RegistrationOperationResult();
        Workflow workflow = opContext.workflow;

        try {
            if(!opContext.skipStep(RegisterAppFacebookState.REGISTERED_WITH_GSM)) {
                if (!registerWithGcm(opContext.context, result)) {
                    throw new Exception("Unable to register with GCM");
                } else {
                    params.gcmId = result.gcmId;
                    workflow.state = RegisterAppFacebookState.REGISTERED_WITH_GSM;
                }
            }

            if(!opContext.skipStep(RegisterAppFacebookState.REGISTERED_APP)) {
                AuthApi authApi = new AuthApi(opContext.context, opContext.logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerAppWithFacebook(params);

                if (result.isUserAuthenticated()) {
                    appIdentity = updateAuthenticationData(result);
                    workflow.state = RegisterAppFacebookState.REGISTERED_APP;
                } else {
                    throw new Exception("Unable to register user");
                }
            }

            if(!opContext.skipStep(RegisterAppFacebookState.INVOKED_EVENT_HANDLERS)) {
                for (AuthEventHandler authEventHandler : eventHandlers) {
                    authEventHandler.onRegisterApp(opContext, appIdentity);
                }
                workflow.state = RegisterAppFacebookState.INVOKED_EVENT_HANDLERS;
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            opContext.logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(opContext.logContext);

        opContext.workflow.clob = apiObjectFactory.getGson().toJson(params);
        saveWorkflow(opContext.workflow);

        return result;
    }

    @NonNull
    private RegistrationOperationResult execRegisterAppWithGoogle(OperationContext opContext, RegisterAppWithGoogleParams params) {
        RegistrationOperationResult result = new RegistrationOperationResult();
        Workflow workflow = opContext.workflow;

        try {
            if(!opContext.skipStep(RegisterAppGoogleState.REGISTERED_WITH_GSM)) {
                if (!registerWithGcm(opContext.context, result)) {
                    throw new Exception("Unable to register with GCM");
                } else {
                    params.gcmId = result.gcmId;
                    workflow.state = RegisterAppGoogleState.REGISTERED_WITH_GSM;
                }
            }

            if(!opContext.skipStep(RegisterAppGoogleState.REGISTERED_APP)) {
                AuthApi authApi = new AuthApi(opContext.context, opContext.logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerAppWithGoogle(params);

                if (result.isUserAuthenticated()) {
                    appIdentity = updateAuthenticationData(result);
                    workflow.state = RegisterAppGoogleState.REGISTERED_APP;
                } else {
                    throw new Exception("Unable to register user");
                }
            }

            if(!opContext.skipStep(RegisterAppGoogleState.INVOKED_EVENT_HANDLERS)) {
                for (AuthEventHandler authEventHandler : eventHandlers) {
                    authEventHandler.onRegisterApp(opContext, appIdentity);
                }
                workflow.state = RegisterAppGoogleState.INVOKED_EVENT_HANDLERS;
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            opContext.logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(opContext.logContext);

        opContext.workflow.clob = apiObjectFactory.getGson().toJson(params);
        saveWorkflow(opContext.workflow);

        return result;
    }

    private boolean registerWithGcm(Context context, RegistrationOperationResult result) {
        String gcmId = BalloPersistentState.getGcmId(context);
        if (!gcmId.isEmpty()) {
            result.gcmId = gcmId;
            return true;
        } else {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            result.playServicesAvailabilityError = googleApiAvailability.isGooglePlayServicesAvailable(context);
            if (result.playServicesAvailabilityError == ConnectionResult.SUCCESS) {
                for (int i = 0; i < 5; i++) {
                    if (i > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        InstanceID.getInstance(context).deleteToken(authParams.getGcmSenderId(), "GCM");
                        gcmId = InstanceID.getInstance(context).getToken(authParams.getGcmSenderId(), "GCM");
                        break;
                    } catch (IOException ex) {
                        Log.i(TAG, "Unable to register with GCM. " + ex.getMessage() + " Attempt " + i);
                    }
                }

                if (gcmId != null && !gcmId.isEmpty()) {
                    BalloPersistentState.setGcmId(context, gcmId);
                }

                if(gcmId != null && !gcmId.isEmpty()) {
                    result.gcmId = gcmId;
                    return true;
                } else {
                    return false;
                }

            } else {
                result.playServicesAvailabilityErrorString = googleApiAvailability.getErrorString(result.playServicesAvailabilityError);
                return false;
            }
        }
    }

    private AppIdentity updateAuthenticationData(RegistrationOperationResult result) throws Exception {
        UserAuthResult authResult = result.authResult;

        AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
        appIdentityDao.updateAuthenticationData(
                authResult.lfUser.getUserName(), authResult.lfUser.getEmail(), authResult.lfUser.getDisplayName(),
                authResult.authTokens.getAccessToken(), authResult.authTokens.getRefreshToken());

        AppIdentity newAppIdentity = new AppIdentity(
                appIdentity.getAppId(),
                authResult.lfUser.getUserName(), authResult.lfUser.getEmail(), authResult.lfUser.getDisplayName(),
                authResult.authTokens.getAccessToken(), authResult.authTokens.getRefreshToken());

        return newAppIdentity;
    }

    private void saveLogs(LogContext logContext) {
        if(logContext.logs.size() > 0) {
            BalloLogDao balloLogDao = Ballo.db().getDao(BalloLogDao.class);
            balloLogDao.log(logContext.logs);
        }
    }

    private void saveWorkflow(Workflow workflow) {
        if(workflow != null) {
            WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
            workflowDao.save(workflow);
        }
    }

    private abstract class RegistrationTask extends AsyncTask<Void, Void, RegistrationOperationResult> {
        protected ProgressDialog dlg;

        protected OperationContext opContext;

        public RegistrationTask(OperationContext opContext) {
            this.opContext = opContext;
        }

        @Override
        protected void onPreExecute() {
            dlg = new ProgressDialog(opContext.context);
            dlg.setMessage("Please wait ...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(RegistrationOperationResult result) {
            if(dlg.isShowing()) {
                dlg.dismiss();
            }

            if(opContext.callBack != null) {
                opContext.callBack.onPostExecute(result);
            }
        }
    }

    private class RegisterUserTask extends RegistrationTask {
        RegisterUserParams registerUserParams;

        public RegisterUserTask(OperationContext opContext, RegisterUserParams registerUserParams) {
            super(opContext);
            this.registerUserParams = registerUserParams;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterUser(opContext, registerUserParams);
        }
    }

    private class RegisterAppTask extends RegistrationTask {
        RegisterAppParams registerAppParams;

        public RegisterAppTask(OperationContext opContext, RegisterAppParams registerAppParams) {
            super(opContext);
            this.registerAppParams = registerAppParams;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterApp(opContext, registerAppParams);
        }
    }

    private class RegisterAppWithFacebookTask extends RegistrationTask {
        RegisterAppWithFacebookParams registerAppFacebookParams;

        public RegisterAppWithFacebookTask(OperationContext opContext, RegisterAppWithFacebookParams registerAppFacebookParams) {
            super(opContext);
            this.registerAppFacebookParams = registerAppFacebookParams;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterAppWithFacebook(opContext, registerAppFacebookParams);
        }
    }

    private class RegisterAppWithGoogleTask extends RegistrationTask {
        RegisterAppWithGoogleParams registerAppGoogleParams;

        public RegisterAppWithGoogleTask(OperationContext opContext, RegisterAppWithGoogleParams registerAppGoogleParams) {
            super(opContext);
            this.registerAppGoogleParams = registerAppGoogleParams;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterAppWithGoogle(opContext, registerAppGoogleParams);
        }
    }
}
