package com.logiforge.balloapp.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.logiforge.ballo.dao.sqlite.SqliteSyncEntityDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 2/23/2017.
 */
public class UserPostalCodesDao extends SqliteSyncEntityDao {
    public static final String TABLE_NAME = "USER_POSTAL_CODES";

    public static final String ENTITY_CLASS_NAME = UserPostalCodes.class.getName();

    public static final String CREATE_STATEMENT =
            "CREATE TABLE USER_POSTAL_CODES (" +
                    "ID TEXT PRIMARY KEY," +
                    "VERSION INTEGER," +
                    "SYNC_STATE INTEGER" +
                    ")";

    public void init() {
        if (!tableExists(TABLE_NAME)) {
            db.execSQL(CREATE_STATEMENT);
        }
    }

    @Override
    public HashMap<String, InventoryItem> getInventory(String s, Long aLong) {
        return null;
    }

    @Override
    protected ContentValues getContentForInsert(SyncEntity entity) {
        return getContentForUpdate(entity);
    }

    @Override
    protected ContentValues getContentForUpdate(SyncEntity entity) {
        return new ContentValues();
    }

    @Override
    protected ContentValues getContentForUpdate(Map<Integer, SyncEntity.ValuePair> changes) {
        return new ContentValues();
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getEntityClassName() {
        return ENTITY_CLASS_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends SyncEntity> T fromCursor(Cursor c) {
        return (T) new UserPostalCodes(
                getString(COL_ID, c),
                getLong(COL_VERSION, c),
                getInt(COL_SYNC_STATE, c)
        );
    }
}
