package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.dao.DbTransaction;

/**
 * Created by iorlanov on 12/13/17.
 */

public interface TransactionFacade {
    public DbTransaction beginTxn(Long action) throws Exception;
    public DbTransaction beginTxnNoLog() throws Exception;
    public void commitTxn(DbTransaction txn);
    public void endTxn(DbTransaction txn);
    public void endTxnAndSync(DbTransaction txn);
}
