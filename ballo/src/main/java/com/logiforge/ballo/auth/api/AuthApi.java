package com.logiforge.ballo.auth.api;

import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.api.AuthTokens;
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
	public UserAuthResult registerUserAndApp(String userName, String email, String displayName, String password, String appId, String gcmId) throws Exception {
		PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_USER_AND_APP), 1);

		req.addStringPart("mode", MODE_LF_ID);
		req.addStringPart("op", OP_REGISTER_USER_AND_APP);
		req.addStringPart("userName", userName);
		req.addStringPart("displayName", displayName);
		req.addStringPart("email", email);
		req.addStringPart("password", password);
		req.addStringPart("appId", appId);
		req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerApp(String userName, String password, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_LF_ID);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("userName", userName);
        req.addStringPart("password", password);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithFacebook(
	        String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_FACEBOOK);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("facebookId", facebookId);
        req.addStringPart("facebookEmail", facebookEmail);
        req.addStringPart("facebookDisplayName", facebookDisplayName);
        req.addStringPart("facebookAT", facebookAT);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithGoogle(
	        String googleId, String googleEmail, String googleDisplayName, String googleAT, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", MODE_GOOGLE);
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("googleId", googleId);
        req.addStringPart("googleEmail", googleEmail);
        req.addStringPart("googleDisplayName", googleDisplayName);
        req.addStringPart("googleAT", googleAT);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), getGson(), UserAuthResult.class);
        return webCall.makeCall(req);
	}
}
