package com.logiforge.balloapp.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.logiforge.ballo.dao.sqlite.SqliteSyncEntityDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iorlanov on 2/23/2017.
 */
public class PostalCodeDao extends SqliteSyncEntityDao {
    public static final String TABLE_NAME = "POSTAL_CODE";

    public static final String COL_LATITUDE = "LATITUDE";
    public static final String COL_LONGITUDE = "LONGITUDE";

    public static final String ENTITY_CLASS_NAME = PostalCode.class.getName();

    public static final String CREATE_STATEMENT =
            "CREATE TABLE POSTAL_CODE (" +
                    "ID TEXT PRIMARY KEY," +
                    "VERSION INTEGER," +
                    "SYNC_STATE INTEGER," +
                    "LATITUDE FLOAT," +
                    "LONGITUDE FLOAT" +
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
        return (T) new PostalCode(
                getString(COL_ID, c),
                getLong(COL_VERSION, c),
                getInt(COL_SYNC_STATE, c),
                getFloat(COL_LATITUDE, c),
                getFloat(COL_LONGITUDE, c)
        );
    }

    public List<PostalCode> findNeighbours(float lat1, float lon1, float lat2, float lon2) {
        List<PostalCode> postalCodeList = new ArrayList<>();
        Cursor c = db.rawQuery(
                "select * from " + TABLE_NAME + " where latitude >= ? and latitude <= ? and longitude >= ? and  longitude <= ?" ,
                new String[]{Float.toString(lat2), Float.toString(lat1), Float.toString(lon1), Float.toString(lon2)});
        while (c.moveToNext()) {
            PostalCode pcode = fromCursor(c);
            postalCodeList.add(pcode);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return postalCodeList;
    }
}
