package com.logiforge.ballo.sync.facade;

import android.content.Context;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.BalloLog;
import com.logiforge.ballo.sync.api.SyncApi;
import com.logiforge.ballo.sync.api.SyncApiParams;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.dao.SyncDaoInitializer;
import com.logiforge.ballo.sync.dao.SyncEntityDao;
import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import com.logiforge.ballo.sync.model.api.DataSlice;
import com.logiforge.ballo.sync.model.api.SubscriptionRequest;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.dao_facade.SyncEntityDaoFacade;

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

    public void deleteUserData(LogContext logContext) throws Exception {
        DbTransaction txn = db().beginSyncTxn();
        try {
        AppSubscriptionDao appSubscriptionDao = db().getDao(AppSubscription.class);
        appSubscriptionDao.deleteAll();

        JournalEntryDao journalEntryDao = db().getDao(JournalEntry.class);
        journalEntryDao.deleteAll();
            db().commitTxn(txn);
        } catch (Exception e) {
            logContext.addLog(BalloLog.LVL_ERROR, e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        } finally {
            db().endTxn(txn);
        }
    }

    public List<AppSubscription> getRemoteAppSubscriptions(Context context, LogContext logContext, AppIdentity newIdentity) throws Exception {
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

    public void downloadSubscriptionData(Context context, LogContext logContext) throws Exception {
        SyncApi syncApi = new SyncApi(context, logContext, apiObjectFactory, syncApiParams);

        AppSubscriptionDao appSubscriptionDao = Ballo.db().getDao(AppSubscription.class);
        List<AppSubscription> appSubscriptions = appSubscriptionDao.getAllSubscriptions();

        DbTransaction txn = db().beginSyncTxn();
        try {
            for(AppSubscription appSubscription : appSubscriptions) {
                AuthenticatedCallResponse<DataSlice> resp = syncApi.getSubscriptionData(
                        new SubscriptionRequest(appSubscription.entityClassName, appSubscription.entityId));

                DataSlice dataSlice = resp.workload;
                if(dataSlice != null) {
                    SyncEntityDaoFacade daoFacade = syncProtocol.getSyncEntityDaoFacade(dataSlice.rootEntity.getClass());
                    daoFacade.add(txn, dataSlice.rootEntity);

                    if(dataSlice.childEntities != null) {
                        for(SyncEntity childEntity : dataSlice.childEntities) {
                            daoFacade = syncProtocol.getSyncEntityDaoFacade(childEntity.getClass());
                            daoFacade.add(txn, childEntity);
                        }
                    }
                }

                appSubscriptionDao.updateVisibleVersion(appSubscription.entityId, dataSlice.rootEntity.version);
            }
            db().commitTxn(txn);
        } catch (Exception e) {
            logContext.addLog(BalloLog.LVL_ERROR, e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        } finally {
            db().endTxn(txn);
        }
    }
}
