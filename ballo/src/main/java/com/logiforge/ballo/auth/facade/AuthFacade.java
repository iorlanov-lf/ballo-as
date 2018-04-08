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
    String WF_REGISTER_USER = "AuthFacade.RegisterUser";
    String WF_REGISTER_APP = "AuthFacade.RegisterApp";
    String WF_REGISTER_APP_FACEBOOK = "AuthFacade.RegisterAppFacebook";
    String WF_REGISTER_APP_GOOGLE = "AuthFacade.RegisterAppGoogle";

    void init() throws Exception;
    String getAppId();

    RegistrationOperationResult registerUser(
            Context context, ApiCallBack callBack, RegisterUserParams registerUserParams, Workflow parentWorkflow) throws Exception;
    RegistrationOperationResult resumeRegisterUser(
            Context context, ApiCallBack callBack, Workflow parentWorkflow) throws Exception;

    RegistrationOperationResult registerApp(
            Context context, ApiCallBack callBack, RegisterAppParams registerAppParams, Workflow parentWorkflow) throws Exception;
    RegistrationOperationResult resumeRegisterApp(
            Context context, ApiCallBack callBack, Workflow parentWorkflow) throws Exception;

    RegistrationOperationResult registerAppWithFacebook(
            Context context, ApiCallBack callBack, RegisterAppWithFacebookParams params, Workflow parentWorkflow) throws Exception;
    RegistrationOperationResult resumeRegisterAppWithFacebook(
            Context context, ApiCallBack callBack, Workflow parentWorkflow) throws Exception;

    RegistrationOperationResult registerAppWithGoogle(
            Context context, ApiCallBack callBack, RegisterAppWithGoogleParams params, Workflow parentWorkflow) throws Exception;
    RegistrationOperationResult resumeRegisterAppWithGoogle(
            Context context, ApiCallBack callBack, Workflow parentWorkflow) throws Exception;

    void registerEventHandler(AuthEventHandler authEventHandler);

    boolean isCloudRegistered();

    void addApiAuthenticationData(PostRequest postRequest);

    boolean onAuthenticatedCallFailure(Context context, int apiAuthenticationOutcome) throws Exception;
}
