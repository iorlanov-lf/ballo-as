package com.logiforge.ballo.dao.sqlite;

import android.database.sqlite.SQLiteDatabase;

//import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.dao.BalloSequenceDao;
import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.dao.DbAdapter;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.dao.WorkflowDao;
import com.logiforge.ballo.dao.sqlite.auth.SqliteAppIdentityDao;
import com.logiforge.ballo.dao.sqlite.sync.SqliteAppSubscriptionDao;
import com.logiforge.ballo.dao.sqlite.sync.SqliteJournalEntryDao;
import com.logiforge.ballo.sync.dao.SyncEntityDao;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.ballo.sync.model.db.JournalEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 10/20/17.
 */

public class SqliteDbAdapter implements DbAdapter {
    protected SQLiteDatabase db = null;
    protected Map<String, Dao> daos = new HashMap<>();
    protected Map<String, SyncEntityDao> syncDaos = new HashMap<>();

    public SqliteDbAdapter(SQLiteDatabase db) {
        this.db = db;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void init() throws Exception {
        SqliteBalloLogDao sqliteBalloLogDao = new SqliteBalloLogDao();
        sqliteBalloLogDao.init();
        daos.put(BalloLogDao.class.getSimpleName(), sqliteBalloLogDao);

        SqliteBalloSequenceDao sqliteBalloSequenceDao = new SqliteBalloSequenceDao();
        sqliteBalloSequenceDao.init();
        daos.put(BalloSequenceDao.class.getSimpleName(), sqliteBalloSequenceDao);

        SqliteWorkflowDao sqliteWorkflowDao = new SqliteWorkflowDao();
        sqliteWorkflowDao.init();
        daos.put(WorkflowDao.class.getSimpleName(), sqliteWorkflowDao);
    }

    public void registerDao(String className, Dao dao) {
        daos.put(className, dao);
    }

    @Override
    public <T extends Dao> T getDao(Class clazz) {
        return (T)daos.get(clazz.getSimpleName());
    }

    @Override
    public <T extends Dao> T getDao(String className) {
        return (T)daos.get(className);
    }

    @Override
    public <T extends SyncEntityDao> T getSyncDao(Class clazz) {
        return (T)syncDaos.get(clazz.getSimpleName());
    }

    @Override
    public <T extends SyncEntityDao> T getSyncDao(String className) {
        return (T)syncDaos.get(className);
    }

    @Override
    public DbTransaction beginTxn(Long action, boolean isSemaforeSynchronized) throws Exception {
        return doBeginTxn(action, isSemaforeSynchronized);
    }

    @Override
    public DbTransaction beginSyncTxn() throws Exception {
        return doBeginTxn(null, true);
    }

    @Override
    public void commitTxn(DbTransaction txn) {
        if(db.inTransaction()) {
            db.setTransactionSuccessful();
            txn.isSuccessful = true;
        }
    }

    @Override
    public void endTxn(DbTransaction txn) {
        if(db.inTransaction()) {
            db.endTransaction();

            if(txn.isSemaforeSynchronized) {
                DbTransaction.semaphore.release();
            }
        }
    }

    protected DbTransaction doBeginTxn(Long action, boolean isSemaforeSynchronized) throws Exception {
        DbTransaction txn = null;

        if(isSemaforeSynchronized) {
            DbTransaction.semaphore.acquire();

            BalloSequenceDao seqDao = getDao(BalloSequenceDao.class);
            Long id = seqDao.getValue();
            txn = new DbTransaction(id, action, true);
        } else {
            txn = new DbTransaction(-1L, action, false);
        }

        db.beginTransaction();

        return txn;
    }
}
