package com.logiforge.ballo.auth.facade;

import android.content.Context;

import com.logiforge.ballo.api.ApiCallBack;
import com.logiforge.ballo.auth.model.api.RegisterAppParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithFacebookParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithGoogleParams;
import com.logiforge.ballo.auth.model.api.RegisterUserParams;
import com.logiforge.ballo.auth.model.api.RegistrationOperationResult;
import com.logiforge.ballo.model.db.Workflow;
import com.logiforge.ballo.net.PostRequest;

/**
 * Created by iorlanov on 10/21/17.
 */

public interface AuthFacade {
    void init() throws Exception;
    String getAppId();

    RegistrationOperationResult registerUser(Context context, ApiCallBack callBack, RegisterUserParams registerUserParams) throws Exception;
    RegistrationOperationResult resumeRegisterUser(Context context, ApiCallBack callBack, Workflow workflow) throws Exception;

    RegistrationOperationResult registerApp(Context context, ApiCallBack callBack, RegisterAppParams registerAppParams) throws Exception;
    RegistrationOperationResult resumeRegisterApp(Context context, ApiCallBack callBack, Workflow workflow) throws Exception;

    RegistrationOperationResult registerAppWithFacebook(Context context, ApiCallBack callBack, RegisterAppWithFacebookParams params) throws Exception;
    RegistrationOperationResult resumeRegisterAppWithFacebook(Context context, ApiCallBack callBack, Workflow workflow) throws Exception;

    RegistrationOperationResult registerAppWithGoogle(Context context, ApiCallBack callBack, RegisterAppWithGoogleParams params) throws Exception;
    RegistrationOperationResult resumeRegisterAppWithGoogle(Context context, ApiCallBack callBack, Workflow workflow) throws Exception;

    void registerEventHandler(AuthEventHandler authEventHandler);

    boolean isCloudRegistered();

    void addApiAuthenticationData(PostRequest postRequest);

    boolean onAuthenticatedCallFailure(Context context, int apiAuthenticationOutcome) throws Exception;
}
