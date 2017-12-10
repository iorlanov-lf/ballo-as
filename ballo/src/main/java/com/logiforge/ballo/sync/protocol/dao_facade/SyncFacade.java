package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/21/17.
 */

interface SyncFacade {
    void syncAdd(SyncEntity entity);
    void syncUpdate(String entityId, Long version, Map<Integer, SyncEntity.ValuePair> changes);
    void syncDelete(String id);
    void uiAdd(DbTransaction txn, SyncEntity entity) throws Exception;
    void uiUpdate(DbTransaction txn, SyncEntity entity) throws Exception;
    void uiDelete(DbTransaction txn, String id) throws Exception;

    String getEntityClassName();
    String getSyncParentEntityClassName();
    boolean isSyncRootEntity();
    boolean myVersionMatters();
    boolean parentVersionMatters();
}
