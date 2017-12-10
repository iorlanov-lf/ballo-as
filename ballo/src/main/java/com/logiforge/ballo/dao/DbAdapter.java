package com.logiforge.ballo.dao;

import com.logiforge.ballo.sync.dao.SyncDao;

/**
 * Created by iorlanov on 10/20/17.
 */

public interface DbAdapter {
    void init() throws Exception;
    public <T extends Dao> T getDao(Class clazz);
    public <T extends Dao> T getDao(String className);

    public <T extends SyncDao> T getSyncDao(Class clazz);
    public <T extends SyncDao> T getSyncDao(String className);

    public DbTransaction beginUiTxn(Long action, boolean isSemaforeSynchronized) throws Exception;
    public DbTransaction beginSyncTxn() throws Exception;
    public void commitTxn(DbTransaction txn);
    public void endTxn(DbTransaction txn);
}
