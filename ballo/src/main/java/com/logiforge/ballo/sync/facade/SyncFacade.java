package com.logiforge.ballo.sync.facade;

import android.content.Context;

import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.BalloLog;
import com.logiforge.ballo.sync.api.SyncApi;
import com.logiforge.ballo.sync.api.SyncApiParams;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.dao.SyncDaoInitializer;
import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.ballo.sync.protocol.SyncProtocol;

import java.util.List;

import static com.logiforge.ballo.Ballo.db;

/**
 * Created by iorlanov on 1/12/18.
 */

public class SyncFacade {

    protected SyncDaoInitializer syncDaoInitializer;
    protected SyncProtocol syncProtocol;
    protected SyncApiParams syncApiParams;
    protected ApiObjectFactory apiObjectFactory;

    public SyncFacade(SyncDaoInitializer syncDaoInitializer, SyncProtocol syncProtocol, SyncApiParams syncApiParams, ApiObjectFactory apiObjectFactory) {
        this.syncDaoInitializer = syncDaoInitializer;
        this.syncProtocol = syncProtocol;
        this.syncApiParams = syncApiParams;
        this.apiObjectFactory = apiObjectFactory;
    }

    public void init() throws Exception {
        syncDaoInitializer.init();
        syncProtocol.init();
    }

    public SyncProtocol syncProtocol() {
        return syncProtocol;
    }

    public List<AppSubscription> getAppSubscriptions(Context context, AppIdentity oldIdentity, AppIdentity newIdentity) throws Exception {
        LogContext logContext = new LogContext("Sync", "getAppSubs");

        SyncApi syncApi = new SyncApi(context, logContext, apiObjectFactory, syncApiParams);
        AuthenticatedCallResponse<List<AppSubscription>> response =
                syncApi.getAppSubscriptions();

        if(response != null && response.success) {
            List<AppSubscription> appSubscriptions = response.workload;

            if(appSubscriptions.size() > 0) {
                DbTransaction txn = db().beginSyncTxn();
                try {
                    AppSubscriptionDao appSubscriptionDao = db().getDao(AppSubscription.class);
                    for(AppSubscription appSubscription : appSubscriptions) {
                        appSubscriptionDao.createAppSubscription(appSubscription);
                    }
                    db().commitTxn(txn);
                } catch (Exception e) {
                    logContext.addLog(BalloLog.LVL_ERROR, e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                } finally {
                    db().endTxn(txn);
                }
            }

            return appSubscriptions;
        } else {
            return null;
        }
    }
}
