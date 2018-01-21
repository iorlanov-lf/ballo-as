package com.logiforge.ballo.dao.sqlite.sync;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.sync.dao.SyncDaoInitializer;
import com.logiforge.ballo.sync.model.db.AppSubscription;
import com.logiforge.ballo.sync.model.db.JournalEntry;

/**
 * Created by iorlanov on 1/16/18.
 */

public class SqliteSyncDaoInitializer implements SyncDaoInitializer {
    @Override
    public void init() throws Exception {
        // sync
        SqliteJournalEntryDao sqliteJournalEntryDao = new SqliteJournalEntryDao();
        sqliteJournalEntryDao.init();
        Ballo.db().registerDao(JournalEntry.class.getSimpleName(), sqliteJournalEntryDao);

        SqliteAppSubscriptionDao sqliteAppSubscriptionDao = new SqliteAppSubscriptionDao();
        sqliteAppSubscriptionDao.init();
        Ballo.db().registerDao(AppSubscription.class.getSimpleName(), sqliteAppSubscriptionDao);
    }
}
