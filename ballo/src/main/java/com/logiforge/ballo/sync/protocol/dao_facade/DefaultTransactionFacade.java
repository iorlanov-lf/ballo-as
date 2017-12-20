package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.dao.DbTransaction;

/**
 * Created by iorlanov on 12/13/17.
 */

public class DefaultTransactionFacade implements TransactionFacade {
    @Override
    public DbTransaction beginTxn(Long action) throws Exception {
        return Ballo.db().beginTxn(action, true);
    }

    @Override
    public DbTransaction beginTxnNoLog() throws Exception {
        return Ballo.db().beginTxn(null, true);
    }

    @Override
    public void commitTxn(DbTransaction txn) {
        Ballo.db().commitTxn(txn);
    }

    @Override
    public void endTxn(DbTransaction txn) {
        Ballo.db().endTxn(txn);
    }

    public void endTxnAndSync(DbTransaction txn) {
        Ballo.db().endTxn(txn);

        if(txn.isSuccessful) {
            // start sync job here
        }
    }
}
