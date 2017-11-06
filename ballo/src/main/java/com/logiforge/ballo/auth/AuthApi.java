package com.logiforge.ballo.auth;

import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.net.JsonWebCall;
import com.logiforge.ballo.net.PostRequest;

import android.content.Context;

public class AuthApi extends Api {
    private static final String OP_REGISTER_APP = "register_app";
	private static final String OP_REGISTER = "register";

    private AuthParams authParams;

	public AuthApi(Context context, LogContext logContext, ApiObjectFactory apiFactory, AuthParams authParams) {
		super(context, logContext, apiFactory);
		this.authParams = authParams;
	}

	@SuppressWarnings("unchecked")
	public UserAuthResult registerUser(String userName, String email, String displayName, String password, String appId, String gcmId) throws Exception {
		PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER), 1);

		req.addStringPart("mode", "lf_id");
		req.addStringPart("op", OP_REGISTER);
		req.addStringPart("userName", userName);
		req.addStringPart("displayName", displayName);
		req.addStringPart("email", email);
		req.addStringPart("password", password);
		req.addStringPart("appId", appId);
		req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), gson, UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerApp(String userName, String password, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "lf_id");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("userName", userName);
        req.addStringPart("password", password);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), gson, UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithFacebook(
	        String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "facebook");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("facebookId", facebookId);
        req.addStringPart("facebookEmail", facebookEmail);
        req.addStringPart("facebookDisplayName", facebookDisplayName);
        req.addStringPart("facebookAT", facebookAT);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), gson, UserAuthResult.class);
        return webCall.makeCall(req);
	}
	
	@SuppressWarnings("unchecked")
	public UserAuthResult registerAppWithGoogle(
	        String googleId, String googleEmail, String googleDisplayName, String googleAT, String appId, String gcmId) throws Exception {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "google");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("googleId", googleId);
        req.addStringPart("googleEmail", googleEmail);
        req.addStringPart("googleDisplayName", googleDisplayName);
        req.addStringPart("googleAT", googleAT);
        req.addStringPart("appId", appId);
        req.addStringPart("gcmId", gcmId);

        JsonWebCall<UserAuthResult> webCall = new JsonWebCall<UserAuthResult>(getHttpAdaptor(), gson, UserAuthResult.class);
        return webCall.makeCall(req);
	}
}
