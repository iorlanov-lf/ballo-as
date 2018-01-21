package com.logiforge.ballo.sync.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.net.JsonWebCall;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import java.util.List;

/**
 * Created by iorlanov on 1/10/18.
 */

public class SyncApi extends Api {
    private static final String OP_GET_APP_SUBSCRIPTIONS = "get_app_subscriptions";
    private SyncApiParams syncApiParams;

    public SyncApi(Context context, LogContext logContext, ApiObjectFactory apiFactory, SyncApiParams syncApiParams) {
        super(context, logContext, apiFactory);
        this.syncApiParams = syncApiParams;
    }

    public AuthenticatedCallResponse<List<AppSubscription>> getAppSubscriptions() throws Exception {
        PostRequest req = new PostRequest(syncApiParams.getSyncUrl(OP_GET_APP_SUBSCRIPTIONS), 1);

        req.addStringPart("op", OP_GET_APP_SUBSCRIPTIONS);
        req.addStringPart("appId", Ballo.authFacade().getAppId());


        JsonWebCall<AuthenticatedCallResponse<List<AppSubscription>>> webCall =
                new JsonWebCall<AuthenticatedCallResponse<List<AppSubscription>>>(getHttpAdaptor(), gson,
                        new TypeToken<AuthenticatedCallResponse<List<AppSubscription>>>(){}.getType());

        return makeAuthorizedCall(webCall, req);
    }

    private <T> AuthenticatedCallResponse<T> makeAuthorizedCall(JsonWebCall<AuthenticatedCallResponse<T>> webCall, PostRequest req) throws Exception {
        Ballo.authFacade().addApiAuthenticationData(req);
        AuthenticatedCallResponse<T> response = webCall.makeCall(req);
        if(response != null && !response.success) {
            if(Ballo.authFacade().onAuthenticatedCallFailure(context, response.apiAuthOutcome)) {
                Ballo.authFacade().addApiAuthenticationData(req);
                response = webCall.makeCall(req);
            }
        }

        return response;
    }
}
