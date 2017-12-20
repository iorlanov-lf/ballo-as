package com.logiforge.ballo.dao;

import com.logiforge.ballo.sync.dao.SyncEntityDao;

/**
 * Created by iorlanov on 10/20/17.
 */

public interface DbAdapter {
    void init() throws Exception;
    
    void registerDao(String className, Dao dao);
    <T extends Dao> T getDao(Class clazz);
    <T extends Dao> T getDao(String className);

    <T extends SyncEntityDao> T getSyncDao(Class clazz);
    <T extends SyncEntityDao> T getSyncDao(String className);

    DbTransaction beginTxn(Long action, boolean isSemaforeSynchronized) throws Exception;
    DbTransaction beginSyncTxn() throws Exception;
    void commitTxn(DbTransaction txn);
    void endTxn(DbTransaction txn);
}
