package com.logiforge.ballo.sync.facade;

import android.content.Context;

import com.logiforge.ballo.auth.facade.AuthEventHandler;
import com.logiforge.ballo.auth.facade.OperationContext;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.model.api.LogContext;
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
    public void onRegisterUser(OperationContext opContext, AppIdentity newIdentity) throws Exception {
        // delete old user data
        syncFacade.deleteUserData(opContext.logContext);

        List<AppSubscription> appSubscriptions =
                syncFacade.getRemoteAppSubscriptions(opContext.context, opContext.logContext, newIdentity);
    }

    @Override
    public void onRegisterApp(OperationContext opContext, AppIdentity newIdentity) throws Exception {
        // delete old user data
        syncFacade.deleteUserData(opContext.logContext);

        List<AppSubscription> appSubscriptions =
                syncFacade.getRemoteAppSubscriptions(opContext.context, opContext.logContext, newIdentity);
    }
}
