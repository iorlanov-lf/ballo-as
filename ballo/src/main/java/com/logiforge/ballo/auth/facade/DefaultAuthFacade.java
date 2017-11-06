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
import com.logiforge.ballo.auth.AuthApi;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.auth.AuthParams;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.model.api.UserAuthOutcome;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.BalloLog;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by iorlanov on 10/21/17.
 */

public class DefaultAuthFacade implements AuthFacade {
    static final String TAG = AuthFacade.class.getSimpleName();

    String appId;
    AuthParams authParams;
    ApiObjectFactory apiObjectFactory;

    public DefaultAuthFacade(AuthParams authParam, ApiObjectFactory apiObjectFactory) {
        this.authParams = authParam;
        this.apiObjectFactory = apiObjectFactory;
    }

    @Override
    public void init() throws Exception {
        AppIdentityDao appIdentityDao = Ballo.daoContext().getDao(AppIdentityDao.class);
        AppIdentity appIdentity = appIdentityDao.getAppIdentity();
        if(appIdentity.getAppId() == null) {
            appId = UUID.randomUUID().toString();
            appIdentityDao.setAppId(appId);
        } else {
            appId = appIdentity.getAppId();
        }
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public RegistrationOperationResult registerUser(Context context, ApiCallBack callBack, String userName, String email, String displayName, String password) throws Exception {
        if(callBack == null) {
            return execRegisterUser(context, userName, email, displayName, password);
        } else {
            RegisterUserTask registerUserTask = new RegisterUserTask(context, callBack, userName, email, displayName, password);
            registerUserTask.execute();
            return null;
        }
    }

    @NonNull
    private RegistrationOperationResult execRegisterUser(Context context, String userName, String email, String displayName, String password) {
        RegistrationOperationResult result = new RegistrationOperationResult();

        LogContext logContext = new LogContext("Auth", "RegUser");

        try {
            if(registerWithGcm(context, result)) {
                AuthApi authApi = new AuthApi(context, logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerUser(userName, email, displayName, password, appId, result.gcmId);

                updateAuthenticationData(result);
            } else {
                throw new Exception("Unable to register with GCM");
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(logContext);

        return result;
    }

    @Override
    public RegistrationOperationResult registerApp(Context context, ApiCallBack callBack, String userName, String password) throws Exception {
        if(callBack == null) {
            return execRegisterApp(context, userName, password);
        } else {
            RegisterAppTask registerAppTask = new RegisterAppTask(context, callBack, userName, password);
            registerAppTask.execute();
            return null;
        }
    }

    @NonNull
    private RegistrationOperationResult execRegisterApp(Context context, String userName, String password) {
        RegistrationOperationResult result = new RegistrationOperationResult();

        LogContext logContext = new LogContext("Auth", "RegApp");

        try {
            if(registerWithGcm(context, result)) {
                AuthApi authApi = new AuthApi(context, logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerApp(userName, password, appId, result.gcmId);

                updateAuthenticationData(result);
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(logContext);

        return result;
    }

    @Override
    public RegistrationOperationResult registerAppWithFacebook(Context context, ApiCallBack callBack, String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT) throws Exception {
        if(callBack == null) {
            return execRegisterAppWithFacebook(context, facebookId, facebookEmail, facebookDisplayName, facebookAT);
        } else {
            RegisterAppWithFacebookTask registerAppWithFacebookTask = new RegisterAppWithFacebookTask(context, callBack, facebookId, facebookEmail, facebookDisplayName, facebookAT);
            registerAppWithFacebookTask.execute();
            return null;
        }
    }

    @NonNull
    private RegistrationOperationResult execRegisterAppWithFacebook(Context context, String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT) {
        RegistrationOperationResult result = new RegistrationOperationResult();

        LogContext logContext = new LogContext("Auth", "RegAppWithFB");

        try {
            if(registerWithGcm(context, result)) {
                AuthApi authApi = new AuthApi(context, logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerAppWithFacebook(facebookId, facebookEmail, facebookDisplayName, facebookAT, appId, result.gcmId);

                updateAuthenticationData(result);
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(logContext);

        return result;
    }

    @Override
    public RegistrationOperationResult registerAppWithGoogle(Context context, ApiCallBack callBack, String googleId, String googleEmail, String googleDisplayName, String googleAT) throws Exception {
        if(callBack == null) {
            return execRegisterAppWithGoogle(context, googleId, googleEmail, googleDisplayName, googleAT);
        } else {
            RegisterAppWithGoogleTask registerAppWithGoogleTask = new RegisterAppWithGoogleTask(context, callBack, googleId, googleEmail, googleDisplayName, googleAT);
            registerAppWithGoogleTask.execute();
            return null;
        }
    }

    @NonNull
    private RegistrationOperationResult execRegisterAppWithGoogle(Context context, String googleId, String googleEmail, String googleDisplayName, String googleAT) {
        RegistrationOperationResult result = new RegistrationOperationResult();

        LogContext logContext = new LogContext("Auth", "RegAppWithGoogle");

        try {
            if(registerWithGcm(context, result)) {
                AuthApi authApi = new AuthApi(context, logContext, apiObjectFactory, authParams);
                result.authResult = authApi.registerAppWithGoogle(googleId, googleEmail, googleDisplayName, googleAT, appId, result.gcmId);

                updateAuthenticationData(result);
            }
        } catch (Exception e) {
            String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
            logContext.addLog(BalloLog.LVL_ERROR, errMsg);
            Log.e(getClass().getSimpleName(), errMsg);
        }

        saveLogs(logContext);

        return result;
    }

    private boolean registerWithGcm(Context context, RegistrationOperationResult result) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        result.playServicesAvailabilityError = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if(result.playServicesAvailabilityError == ConnectionResult.SUCCESS) {
            String gcmId = BalloPersistentState.getGcmId(context);

            if (gcmId.isEmpty()) {
                for(int i=0; i<5; i++) {
                    if(i>0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        InstanceID.getInstance(context).deleteToken(authParams.getGcmSenderId(),"GCM");
                        gcmId = InstanceID.getInstance(context).getToken(authParams.getGcmSenderId(),"GCM");
                        break;
                    } catch (IOException ex) {
                        Log.i(TAG, "Unable to register with GCM. " + ex.getMessage() + " Attempt " + i);
                    }
                }

                if(gcmId != null && !gcmId.isEmpty()) {
                    BalloPersistentState.setGcmId(context, gcmId);
                }
                result.gcmId = gcmId;
            } else {
                result.gcmId = gcmId;
            }
        } else {
            result.playServicesAvailabilityErrorString = googleApiAvailability.getErrorString(result.playServicesAvailabilityError);
        }

        return result.gcmId != null && !result.gcmId.isEmpty();
    }

    private void updateAuthenticationData(RegistrationOperationResult result) throws Exception {
        UserAuthResult authResult = result.authResult;
        if(authResult != null) {

            if(authResult.success) {
                AppIdentityDao appIdentityDao = Ballo.daoContext().getDao(AppIdentityDao.class);
                appIdentityDao.updateAuthenticationData(
                        authResult.lfUser.getUserName(), authResult.lfUser.getEmail(), authResult.lfUser.getDisplayName(),
                        authResult.authTokens.getAccessToken(), authResult.authTokens.getRefreshToken());
            } else {
                if(authResult.outcome.equals(UserAuthOutcome.InternalError)) {
                    throw new Exception("Unknown server side error");
                }
            }
        } else {
            throw new Exception("No API call response");
        }
    }

    private void saveLogs(LogContext logContext) {
        if(logContext.logs.size() > 0) {
            BalloLogDao balloLogDao = Ballo.daoContext().getDao(BalloLogDao.class);
            balloLogDao.log(logContext.logs);
        }
    }

    private abstract class RegistrationTask extends AsyncTask<Void, Void, RegistrationOperationResult> {
        protected ProgressDialog dlg;

        protected Context context;
        protected ApiCallBack callBack;

        public RegistrationTask(Context context, ApiCallBack callBack) {
            this.context = context;
            this.callBack = callBack;
        }

        @Override
        protected void onPreExecute() {
            dlg = new ProgressDialog(context);
            dlg.setMessage("Please wait ...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(RegistrationOperationResult result) {
            if(dlg.isShowing()) {
                dlg.dismiss();
            }

            if(callBack != null) {
                callBack.onPostExecute(result);
            }
        }
    }

    private class RegisterUserTask extends RegistrationTask {
        private String userName;
        private String email;
        private String displayName;
        private String password;

        public RegisterUserTask(Context context, ApiCallBack callBack, String userName, String email, String displayName, String password) {
            super(context, callBack);

            this.userName = userName;
            this.email = email;
            this.displayName = displayName;
            this.password = password;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterUser(context, userName, email, displayName, password);
        }
    }

    private class RegisterAppTask extends RegistrationTask {
        private String userName;
        private String password;

        public RegisterAppTask(Context context, ApiCallBack callBack, String userName, String password) {
            super(context, callBack);

            this.userName = userName;
            this.password = password;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterApp(context, userName, password);
        }
    }

    private class RegisterAppWithFacebookTask extends RegistrationTask {
        private String facebookId;
        private String facebookEmail;
        private String facebookDisplayName;
        private String facebookAT;

        public RegisterAppWithFacebookTask(Context context, ApiCallBack callBack, String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT) {
            super(context, callBack);

            this.facebookId = facebookId;
            this.facebookEmail = facebookEmail;
            this.facebookDisplayName = facebookDisplayName;
            this.facebookAT = facebookAT;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterAppWithFacebook(context, facebookId, facebookEmail, facebookDisplayName, facebookAT);
        }
    }

    private class RegisterAppWithGoogleTask extends RegistrationTask {
        private String googleId;
        private String googleEmail;
        private String googleDisplayName;
        private String googleAT;

        public RegisterAppWithGoogleTask(Context context, ApiCallBack callBack, String googleId, String googleEmail, String googleDisplayName, String googleAT) {
            super(context, callBack);

            this.googleId = googleId;
            this.googleEmail = googleEmail;
            this.googleDisplayName = googleDisplayName;
            this.googleAT = googleAT;
        }

        @Override
        protected RegistrationOperationResult doInBackground(Void... params) {
            return execRegisterAppWithGoogle(context, googleId, googleEmail, googleDisplayName, googleAT);
        }
    }
}
