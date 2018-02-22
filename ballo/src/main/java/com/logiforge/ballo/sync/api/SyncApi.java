package com.logiforge.ballo.sync.api;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.net.BinaryWebCall;
import com.logiforge.ballo.net.JsonWebCall;
import com.logiforge.ballo.net.PostRequest;
import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import com.logiforge.ballo.sync.model.api.DataSlice;
import com.logiforge.ballo.sync.model.api.SubscriptionRequest;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.ballo.sync.protocol.conversion.MPackGetDataSliceResponseConverter;

import java.util.List;

/**
 * Created by iorlanov on 1/10/18.
 */

public class SyncApi extends Api {
    private static final String OP_GET_APP_SUBSCRIPTIONS = "get_app_subscriptions";
    public static final String OP_GET_SUBSCRIPTION_DATA= "get_subscription_data";

    public static final String PAR_SUBSCRIPTION_REQUEST = "subscription_request";

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

        return makeAuthenticatedCall(webCall, req);
    }

    public AuthenticatedCallResponse<DataSlice> getSubscriptionData(SubscriptionRequest subscriptionRequest) throws Exception {
        PostRequest req = new PostRequest(syncApiParams.getSyncUrl(OP_GET_SUBSCRIPTION_DATA), 1);

        req.addStringPart("op", OP_GET_SUBSCRIPTION_DATA);
        req.addStringPart("appId", Ballo.authFacade().getAppId());
        req.addStringPart(PAR_SUBSCRIPTION_REQUEST, gson.toJson(subscriptionRequest));


        BinaryWebCall<AuthenticatedCallResponse<DataSlice>> webCall =
                new BinaryWebCall<AuthenticatedCallResponse<DataSlice>>(getHttpAdaptor(), new MPackGetDataSliceResponseConverter());

        return makeAuthenticatedCall(webCall, req);
    }

    private <T> AuthenticatedCallResponse<T> makeAuthenticatedCall(JsonWebCall<AuthenticatedCallResponse<T>> webCall, PostRequest req) throws Exception {
        Ballo.authFacade().addApiAuthenticationData(req);
        AuthenticatedCallResponse<T> response = webCall.makeCall(req);
        if(response != null && !response.success) {
            if(Ballo.authFacade().onAuthenticatedCallFailure(context, response.appAuthResult)) {
                Ballo.authFacade().addApiAuthenticationData(req);
                response = webCall.makeCall(req);
            }
        }

        return response;
    }

    private <T> AuthenticatedCallResponse<T> makeAuthenticatedCall(BinaryWebCall<AuthenticatedCallResponse<T>> webCall, PostRequest req) throws Exception {
        Ballo.authFacade().addApiAuthenticationData(req);
        AuthenticatedCallResponse<T> response = webCall.makeCall(req);
        if(response != null && !response.success) {
            if(Ballo.authFacade().onAuthenticatedCallFailure(context, response.appAuthResult)) {
                Ballo.authFacade().addApiAuthenticationData(req);
                response = webCall.makeCall(req);
            }
        }

        return response;
    }
}
