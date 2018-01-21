package com.logiforge.ballo.sync.facade;

import android.content.Context;

import com.logiforge.ballo.auth.facade.AuthEventHandler;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import java.util.List;

/**
 * Created by iorlanov on 1/10/18.
 */

public class SyncAuthEventHandler implements AuthEventHandler {
    SyncFacade syncFacade;

    public SyncAuthEventHandler(SyncFacade syncFacade) {
        this.syncFacade = syncFacade;
    }

    @Override
    public void onRegisterUser(Context context, AppIdentity oldIdentity, AppIdentity newIdentity) throws Exception {
        List<AppSubscription> appSubscriptions = syncFacade.getAppSubscriptions(context, oldIdentity, newIdentity);
    }

    @Override
    public void onRegisterApp(Context context, AppIdentity oldIdentity, AppIdentity newIdentity) throws Exception {
        List<AppSubscription> appSubscriptions = syncFacade.getAppSubscriptions(context, oldIdentity, newIdentity);
    }
}
