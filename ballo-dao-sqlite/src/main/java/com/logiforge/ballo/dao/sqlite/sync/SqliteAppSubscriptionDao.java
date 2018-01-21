package com.logiforge.ballo.dao.sqlite.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.dao.sqlite.SqliteDao;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iorlanov on 1/13/18.
 */

public class SqliteAppSubscriptionDao extends SqliteDao implements AppSubscriptionDao {
    public static final String TABLE_NAME = "APP_SUBSCRIPTION";
    private static final String COL_ENTITY_CLASS_NAME = "ENTITY_CLASS_NAME";
    private static final String COL_ENTITY_ID = "ENTITY_ID";
    private static final String COL_VISIBLE_VERSION = "VISIBLE_VERSION";

    private static final String CREATE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS APP_SUBSCRIPTION (" +
            "ENTITY_CLASS_NAME TEXT," +
            "ENTITY_ID TEXT PRIMARY KEY," +
            "VISIBLE_VERSION INTEGER" +
            ");";

    public SqliteAppSubscriptionDao() {
        super();
    }

    protected SqliteAppSubscriptionDao(SQLiteDatabase database) {
        super(database);
    }

    public void init() {
        if (!tableExists(TABLE_NAME)) {
            db.execSQL(CREATE_STATEMENT);
        }
    }

    public void createAppSubscription(AppSubscription appSubscription) throws Exception {
        ContentValues values = new ContentValues();
        values.put(COL_ENTITY_CLASS_NAME, appSubscription.entityClassName);
        values.put(COL_ENTITY_ID, appSubscription.entityId);
        values.put(COL_VISIBLE_VERSION, appSubscription.visibleVersion);

        db.insert(TABLE_NAME, null, values);
    }

    public List<AppSubscription> getAllSubscriptions() throws Exception {
        List<AppSubscription> subscriptions = new ArrayList<>();

        Cursor c;
        c = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                AppSubscription subscription = new AppSubscription(
                        getString(COL_ENTITY_CLASS_NAME, c),
                        getString(COL_ENTITY_ID, c),
                        getLong(COL_VISIBLE_VERSION, c));

                subscriptions.add(subscription);
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return subscriptions;
    }
}
