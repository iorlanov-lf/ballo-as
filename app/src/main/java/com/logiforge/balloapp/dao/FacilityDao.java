package com.logiforge.balloapp.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.logiforge.ballo.dao.sqlite.SqliteSyncEntityDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.balloapp.model.db.Facility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iorlanov on 2/23/2017.
 */
public class FacilityDao extends SqliteSyncEntityDao {
    public static final String TABLE_NAME = "FACILITY";

    public static final String COL_NAME = "NAME";
    public static final String COL_STREET_ADDRESS = "STREET_ADDRESS";
    public static final String COL_CITY = "CITY";
    public static final String COL_STATE = "STATE";
    public static final String COL_POSTAL_CODE = "POSTAL_CODE";
    public static final String COL_DIRECTIONS = "DIRECTIONS";
    public static final String COL_IS_REFERENCE_ENTITY = "IS_REFERENCE_ENTITY";
    public static final String COL_ORIGINATOR_USER_NAME = "ORIGINATOR_USER_NAME";

    public static final String ENTITY_CLASS_NAME = Facility.class.getName();

    public static final String CREATE_STATEMENT =
            "CREATE TABLE FACILITY (" +
                    "ID TEXT PRIMARY KEY," +
                    "VERSION INTEGER," +
                    "SYNC_STATE INTEGER," +
                    "NAME TEXT," +
                    "STREET_ADDRESS TEXT," +
                    "CITY TEXT," +
                    "STATE TEXT," +
                    "POSTAL_CODE TEXT," +
                    "DIRECTIONS TEXT," +
                    "IS_REFERENCE_ENTITY INTEGER," +
                    "ORIGINATOR_USER_NAME TEXT" +
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
        Facility facility = (Facility)entity;

        ContentValues values = new ContentValues();
        values.put(COL_NAME, facility.getName());
        values.put(COL_STREET_ADDRESS, facility.getStreetAddress());
        values.put(COL_CITY, facility.getCity());
        values.put(COL_STATE, facility.getState());
        values.put(COL_POSTAL_CODE, facility.getPostalCode());
        values.put(COL_DIRECTIONS, facility.getDirections());
        values.put(COL_IS_REFERENCE_ENTITY, facility.getIsReferenceEntity());
        values.put(COL_ORIGINATOR_USER_NAME, facility.getOriginatorUserName());

        return values;
    }

    @Override
    protected ContentValues getContentForUpdate(Map<Integer, SyncEntity.ValuePair> changes) {
        ContentValues values = new ContentValues();

        if(changes != null) {
            for(Map.Entry<Integer, SyncEntity.ValuePair> change : changes.entrySet()) {
                switch (change.getKey()) {
                    case Facility.FLD_NAME:
                        values.put(COL_NAME, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_STREET_ADDRESS:
                        values.put(COL_STREET_ADDRESS, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_CITY:
                        values.put(COL_CITY, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_STATE:
                        values.put(COL_STATE, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_POSTAL_CODE:
                        values.put(COL_POSTAL_CODE, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_DIRECTION:
                        values.put(COL_DIRECTIONS, (String) change.getValue().newValue);
                        break;
                    case Facility.FLD_IS_REFERENCE_ENTITY:
                        values.put(COL_IS_REFERENCE_ENTITY, (Boolean) change.getValue().newValue);
                        break;
                    case Facility.FLD_ORIGINATOR_USER_NAME:
                        values.put(COL_ORIGINATOR_USER_NAME, (String) change.getValue().newValue);
                        break;
                    default:
                        break;
                }
            }
        }

        return values;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getEntityClassName() {
        return ENTITY_CLASS_NAME;
    }

    public List<Facility> findByLikeName(String substring) {
        List<Facility> facilities = new ArrayList<>();

        Cursor c = db.rawQuery(
                "select * from " + TABLE_NAME + " where " + COL_NAME + " like '%" + substring + "%'", null);
        while (c.moveToNext()) {
            Facility f = fromCursor(c);
            facilities.add(f);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return facilities;
    }

    public <T extends SyncEntity> List<T> findByPostalCode(String postalCode) {
        List<T> entities = new ArrayList<>();

        Cursor c;
        c = db.query(getTableName(), null, COL_POSTAL_CODE+"=?", new String[]{postalCode}, null, null, null);
        while (c.moveToNext()) {
            T e = (T)fromCursor(c);
            entities.add(e);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return entities;
    }

    public void deleteByPostalCode(String postalCode) {
        db.delete(TABLE_NAME, COL_POSTAL_CODE+"=?", new String[]{postalCode});
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends SyncEntity> T fromCursor(Cursor c) {
        return (T) new Facility(
                getString(COL_ID, c),
                getLong(COL_VERSION, c),
                getInt(COL_SYNC_STATE, c),
                getString(COL_NAME, c),
                getString(COL_STREET_ADDRESS, c),
                getString(COL_CITY, c),
                getString(COL_STATE, c),
                getString(COL_POSTAL_CODE, c),
                getString(COL_DIRECTIONS, c),
                getBoolean(COL_IS_REFERENCE_ENTITY, c),
                getString(COL_ORIGINATOR_USER_NAME, c)
        );
    }
}
