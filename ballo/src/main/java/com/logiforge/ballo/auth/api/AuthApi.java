package com.logiforge.ballo.auth.api;

import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.api.AuthTokens;
import com.logiforge.ballo.auth.model.api.RegisterAppParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithFacebookParams;
import com.logiforge.ballo.auth.model.api.RegisterAppWithGoogleParams;
import com.logiforge.ballo.auth.model.api.RegisterUserParams;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.net.JsonWebCall;
import com.logiforge.ballo.net.PostRequest;

import android.content.Context;

public class AuthApi extends Api {
    private static final String MODE_LF_ID = "lf_id";
    private static final String MODE_GOOGLE = "google";
    private static final String MODE_FACEBOOK = "facebook";
    private static final String MODE_TOKEN = "token";

    private static final String OP_REGISTER_APP = "register_app";
	private static final String OP_REGISTER_USER_AND_APP = "register_user_and_app";
    private static final String OP_GET_ACCESS_TOKEN = "get_access_token";

    private AuthParams authParams;

	public AuthApi(Context context, LogContext logContext, ApiObjectFactory apiFactory, AuthParams authParams) {
		super(context, logContext, apiFactory);
		this.authParams = authParams;
	}

    public AuthTokens getAccessToken(String appId, String refreshToken) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_USER_AND_APP), 1);

        req.addStringPart("mode", MODE_TOKEN);
        req.addStringPart("op", OP_GET_ACCESS_TOKEN);
        req.addStringPart("appId", appId);
        req.addStringPart("refreshToken", refreshToken);

        JsonWebCall<AuthTokens> webCall = new JsonWebCall<AuthTokens>(getHttpAdaptor(), getGson(), AuthTokens.class);
        return webCall.makeCall(req);
    }

	@SuppressWarnings("unchecked")
	public UserAuthResult registerUserAndApp(RegisterUserParams registerUserParams) throws Exception {
		PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_USER_AND_APP), 1);

		req.addStringPart("mode", MODE_LF_ID);
		req.addStringPart("op", OP_REGISTER_USER_AND_APP);
		req.addStringPart("userName", registerUserParams.userName);
		req.addStringPart("displayName", registerUserParams.displayName);
		req.addStringPart("email", registerUserParams.email);
		req.addStringPart("password", registerUserParams.password);
		req.addStringPart("appId", registerUserParams.appId);
		req.addStringPart("gcmId", registerUserParams.gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerApp(RegisterAppParams registerAppParams) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_LF_ID);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("userName", registerAppParams.userName);
        req.addStringPart("password", registerAppParams.password);
        req.addStringPart("appId", registerAppParams.appId);
        req.addStringPart("gcmId", registerAppParams.gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithFacebook(RegisterAppWithFacebookParams params) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_FACEBOOK);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("facebookId", params.facebookId);
        req.addStringPart("facebookEmail", params.facebookEmail);
        req.addStringPart("facebookDisplayName", params.facebookDisplayName);
        req.addStringPart("facebookAT", params.facebookAT);
        req.addStringPart("appId", params.appId);
        req.addStringPart("gcmId", params.gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithGoogle(RegisterAppWithGoogleParams params) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_GOOGLE);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("googleId", params.googleId);
        req.addStringPart("googleEmail", params.googleEmail);
        req.addStringPart("googleDisplayName", params.googleDisplayName);
        req.addStringPart("googleAT", params.googleAT);
        req.addStringPart("appId", params.appId);
        req.addStringPart("gcmId", params.gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
}
