package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by iorlanov on 11/21/17.
 */

public interface SyncEntityDaoFacade {
    // CRUD
    void syncAdd(SyncEntity entity);
    void syncUpdate(String entityId, Long version, Map<Integer, SyncEntity.ValuePair> changes);
    void syncDelete(String id);
    void add(DbTransaction txn, SyncEntity entity) throws Exception;
    void update(DbTransaction txn, SyncEntity entity) throws Exception;
    void delete(DbTransaction txn, String id) throws Exception;

    // queries
    <T> T find(String id) throws Exception;
}
