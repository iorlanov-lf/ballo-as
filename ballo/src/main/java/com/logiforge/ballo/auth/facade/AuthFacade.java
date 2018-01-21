package com.logiforge.ballo.auth.facade;

import android.content.Context;

import com.logiforge.ballo.api.ApiCallBack;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.net.PostRequest;

/**
 * Created by iorlanov on 10/21/17.
 */

public interface AuthFacade {
    void init() throws Exception;
    String getAppId();

    RegistrationOperationResult registerUser(Context context, ApiCallBack callBack, String userName, String email, String displayName, String password) throws Exception;
    RegistrationOperationResult registerApp(Context context, ApiCallBack callBack, String userName, String password) throws Exception;

    RegistrationOperationResult registerAppWithFacebook(Context context, ApiCallBack callBack, String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT) throws Exception;
    RegistrationOperationResult registerAppWithGoogle(Context context, ApiCallBack callBack, String googleId, String googleEmail, String googleDisplayName, String googleAT) throws Exception;

    void registerEventHandler(AuthEventHandler authEventHandler);

    boolean isCloudRegistered();

    void addApiAuthenticationData(PostRequest postRequest);

    boolean onAuthenticatedCallFailure(Context context, int apiAuthenticationOutcome) throws Exception;
}
