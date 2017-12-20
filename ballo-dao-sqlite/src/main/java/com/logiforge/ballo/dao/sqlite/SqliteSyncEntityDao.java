package com.logiforge.ballo.dao.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.sync.dao.SyncEntityDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;


public abstract class SqliteSyncEntityDao extends SqliteDao implements SyncEntityDao {
	protected static final String COL_ID = "ID";
	protected static final String COL_VERSION = "VERSION";
	protected static final String COL_SYNC_STATE = "SYNC_STATE";

	public SqliteSyncEntityDao() {
		super();
	}
	
	public SqliteSyncEntityDao(SQLiteDatabase database) {
		super(database);
	}
	
	// ABSTRACT
	abstract protected ContentValues getContentForInsert(SyncEntity entity);
	abstract protected ContentValues getContentForUpdate(SyncEntity entity);
    abstract protected ContentValues getContentForUpdate(Map<Integer, SyncEntity.ValuePair> changes);
	abstract protected String getTableName();
    abstract protected String getEntityClassName();
    abstract protected <T extends SyncEntity> T fromCursor(Cursor c);

	// These are meant to be overridden if necessary
	protected String[] getNoSyncFields() {
		return null;
	}

    @Override
	public void add(SyncEntity entity) {
		ContentValues values = getContentForInsert(entity);
		values.put(COL_ID, entity.id);
		values.put(COL_VERSION, entity.version);
		values.put(COL_SYNC_STATE, SYNC_STATE_NORMAL);
		db.insertWithOnConflict(getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}

    @Override
    public void update(String entityId, Long version, int syncState, Map<Integer, SyncEntity.ValuePair> changes) {
        ContentValues values = getContentForUpdate(changes);
        values.put(COL_VERSION, version);
        values.put(COL_SYNC_STATE, syncState);

        String[] noSyncFields = getNoSyncFields();
        if(noSyncFields != null && noSyncFields.length > 0) {
            for(String fieldName : noSyncFields) {
                values.remove(fieldName);
            }
        }

        db.update(getTableName(), values, COL_ID+"=?", new String[] {entityId});
    }

	@Override
	public void delete(String id) {
		db.delete(getTableName(), COL_ID+"=?", new String[] {id});
	}

    @Override
    public <T extends SyncEntity> T find(String id) {
        T e = null;

        Cursor c;
        c = db.query(getTableName(), null,
                "ID=?",
                new String[]{id},
                null, null, null);

        if (c.moveToFirst()) {
            e = fromCursor(c);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return (T)e;
    }

    @Override
	public void deleteAll() {
		db.delete(getTableName(), null, null);
	}

    @Override
	public void updateVersion(String id, long version) {
		ContentValues values = new ContentValues();
		values.put(COL_VERSION, version);
		db.update(getTableName(), values, COL_ID+"=?", new String[] {id});
	}

    @Override
	public void setSyncState(String id, int state) {
		ContentValues values = new ContentValues();
		values.put(COL_SYNC_STATE, state);
		db.update(getTableName(), values, COL_ID+"=?", new String[] {id});
	}

    @Override
	public boolean exists(String id) {
		boolean result = false;
		
		Cursor c = db.query(getTableName(), new String[]{COL_ID}, COL_ID+"=?", new String[]{id}, null, null, null);
		if (c.moveToFirst()) {
			result = true;
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        
        return result;
	}

    @Override
	public Long getVersion(String id) {
		Long version = null;
		Cursor c = db.query(getTableName(), new String[]{COL_VERSION}, COL_ID+"=?", new String[]{id}, null, null, null);
		
		if (c.moveToFirst()) {
			version = getLong(COL_VERSION, c);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        
        return version;
	}

    @Override
	public InventoryItem getEntityAsInventory(String id) {
		InventoryItem entityAsInventory = null;
		if(id != null) {		
			Cursor c = db.query(getTableName(), new String[]{COL_VERSION, COL_SYNC_STATE}, COL_ID+"=?", new String[]{id}, null, null, null);
			
			if (c.moveToFirst()) {
				entityAsInventory = new InventoryItem(
						id, 
						getEntityClassName(),
						getLong(COL_VERSION, c), 
						getInt(COL_SYNC_STATE, c),
						null, 
						null, 
						null
						
				);
	        }
	        if (c != null && !c.isClosed()) {
	            c.close();
	        }
		}
		
		return entityAsInventory;
	}

    @Override
	public HashMap<String, InventoryItem> getInventory() {
		HashMap<String, InventoryItem> entities = new HashMap<String, InventoryItem>();
		
		Cursor c;	
		c = db.query(getTableName(), null, 
				null, 
				null, 
				null, null, COL_ID);
		
		if (c.moveToFirst()) {
            do {
            	InventoryItem e = new InventoryItem(
            		getString(COL_ID, c),
                    getEntityClassName(),
            		getLong(COL_VERSION, c),
            		getInt(COL_SYNC_STATE, c),
            		null,
            		null,
            		null
            	);
            	
            	entities.put(e.entityId, e);
            	
            	
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
		
		return entities;
	}

    @Override
	public int count() {
		Cursor c = db.rawQuery("SELECT count(id) c FROM " + getTableName(), null);
		Integer count = 0;
		if (c.moveToFirst()) {
			count = getInt("c", c);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}

		return count;
	}

    public <T extends SyncEntity> List<T> findAll() {
        List<T> entities = new ArrayList<>();

        Cursor c;
        c = db.query(getTableName(), null, null, null, null, null, null);
        while (c.moveToNext()) {
            T e = (T)fromCursor(c);
            entities.add(e);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        return entities;
    }
}
