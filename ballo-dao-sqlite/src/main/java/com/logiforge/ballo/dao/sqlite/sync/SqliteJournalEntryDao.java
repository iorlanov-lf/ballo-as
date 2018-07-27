package com.logiforge.ballo.dao.sqlite.sync;

import java.util.ArrayList;
import java.util.List;

import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.dao.sqlite.SqliteDao;
import com.logiforge.ballo.sync.model.api.EntityChanges;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteJournalEntryDao extends SqliteDao implements JournalEntryDao {
	
	public static final String TABLE_NAME = "JOURNAL_ENTRY";
	private static final String COL_ID = "ID";
	private static final String COL_TXN_ID = "TXN_ID";
	private static final String COL_OPERATION_NBR = "OPERATION_NBR";
	private static final String COL_ENTITY_ID = "ENTITY_ID";
	private static final String COL_CLASS_NAME = "CLASS_NAME";
	private static final String COL_OPERATION = "OPERATION";
	private static final String COL_VERSION = "VERSION";
	private static final String COL_PARENT_CLASS_NAME = "PARENT_CLASS_NAME";
	private static final String COL_PARENT_ID = "PARENT_ID";
	private static final String COL_PARENT_VERSION = "PARENT_VERSION";
	private static final String COL_CLOB_DATA = "CLOB_DATA";
	private static final String COL_BLOB_DATA = "BLOB_DATA";
	
	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS JOURNAL_ENTRY (" +
		"ID INTEGER PRIMARY KEY AUTOINCREMENT," +
		"TXN_ID INTEGER," +
		"OPERATION_NBR INTEGER," +
		"ENTITY_ID TEXT," +
		"CLASS_NAME TEXT," +
		"OPERATION INTEGER," +
		"VERSION INTEGER," +
		"PARENT_CLASS_NAME TEXT," +
		"PARENT_ID TEXT," +
		"PARENT_VERSION INTEGER," +
		"CLOB_DATA TEXT," +
		"BLOB_DATA BLOB" +
		");";
	
	public SqliteJournalEntryDao() {
		super();
	}
	
	protected SqliteJournalEntryDao(SQLiteDatabase database) {
		super(database);
	}

	protected String getTableName() {
		return TABLE_NAME;
	}

	public void init() {
		if (!tableExists(TABLE_NAME)) {
			db.execSQL(CREATE_STATEMENT);
		}
	}


    @Override
	public void onEntityCreated(DbTransaction txn, SyncEntity entity, byte[] entityAsBytes, String parentClassName, String parentId, Long parentVersion) {
		try {		
			if(entity != null) {

				add(new JournalEntry(
						txn.id, txn.nextOperationNbr(), entity, parentClassName, parentId, parentVersion, EntityChanges.OP_NEW,
						null, entityAsBytes));
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {

		}
	}

    @Override
	public void onEntityDeleted(DbTransaction txn, SyncEntity entity, String parentClassName, String parentId, Long parentVersion) {
		try {
			if(entity != null) {
				add(new JournalEntry(txn.id, txn.nextOperationNbr(), entity, parentClassName, parentId, parentVersion, EntityChanges.OP_DELETE));
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {

		}
	}


	@Override
	public void onEntityUpdated(DbTransaction txn, SyncEntity entity, byte[] entityAsBytes, String parentClassName, String parentId, Long parentVersion) {
		try {		
			if(entity != null) {
				add(new JournalEntry(
						txn.id, txn.nextOperationNbr(), entity, parentClassName, parentId, parentVersion, EntityChanges.OP_UPDATE,
						null, entityAsBytes));
			}
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {

		}
	}

	@Override
	public List<JournalEntry> getEntries() {
		ArrayList<JournalEntry> entries = new ArrayList<JournalEntry>();
		
		try {		
			Cursor c;	
			c = db.query(TABLE_NAME, null, null, null, null, null, COL_ID+","+COL_TXN_ID+","+COL_OPERATION_NBR);
			
			if (c.moveToFirst()) {
	            do {
	            	JournalEntry e = new JournalEntry(
	            			getLong(COL_ID, c),
	            			getLong(COL_TXN_ID, c),
	            			getLong(COL_OPERATION_NBR, c),
	            			getString(COL_ENTITY_ID, c),
	            			getString(COL_CLASS_NAME, c),
	            			getInt(COL_OPERATION, c),
	            			getLong(COL_VERSION, c),
	            			getString(COL_PARENT_CLASS_NAME, c),
	            			getString(COL_PARENT_ID, c),
	            			getLong(COL_PARENT_VERSION, c)
	            	);
	            	entries.add(e);
	            } while (c.moveToNext());
	        }
	        if (c != null && !c.isClosed()) {
	            c.close();
	        }
		
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {

		}
		
		return entries;
	}

	@Override
	public void delete(String entityId) {
		db.delete(TABLE_NAME, "ENTITY_ID=?", new String[] {entityId});
	}

	@Override
	public void delete(String id, Integer updateCounter) {
		db.delete(TABLE_NAME, "ENTITY_ID=? AND UPDATE_COUNTER=?", new String[] {id, updateCounter.toString()});
	}

	@Override
	public void deleteByEntityOrParentId(String entityId) {
		db.delete(TABLE_NAME, "ENTITY_ID=?", new String[] {entityId});
		db.delete(TABLE_NAME, "PARENT_ID=?", new String[] {entityId});
	}

	@Override
	public void deleteOrUpdateVersion(String id, Integer updateCounter, Long version) {
		if(db.delete(TABLE_NAME, "ENTITY_ID=? AND UPDATE_COUNTER=?", new String[] {id, updateCounter.toString()}) == 0) {
			ContentValues values = new ContentValues();
			values.put(COL_VERSION, version);
			db.update(TABLE_NAME, values, "ENTITY_ID=?", new String[] {id});
		}
	}

	@Override
	public void deleteOrUpdateVersionAndOp(String id, Integer updateCounter, Long version) {
		if(db.delete(TABLE_NAME, "ENTITY_ID=? AND UPDATE_COUNTER=?", new String[] {id, updateCounter.toString()}) == 0) {
			ContentValues values = new ContentValues();
			values.put(COL_OPERATION, EntityChanges.OP_UPDATE);
			values.put(COL_VERSION, version);
			db.update(TABLE_NAME, values, "ENTITY_ID=?", new String[] {id});
		}
	}

	@Override
	public void deleteTxn(Long txnId) {
		db.delete(TABLE_NAME, COL_TXN_ID+"=?", new String[] {txnId.toString()});
	}

	@Override
	public void deleteByIds(List<Long> ids) {
		for(Long id : ids) {
            deleteById(id);
		}
	}

	@Override
	public void deleteById(Long id) {
		db.delete(TABLE_NAME, COL_ID+"=?", new String[] {id.toString()});
	}

	@Override
	public String getClobData(Long id) {
		String clobData = null;
		
		Cursor c = db.query(TABLE_NAME, new String[]{COL_CLOB_DATA}, "ID=?", new String[]{id.toString()}, null, null, null);

		if (c.moveToFirst()) {
			clobData = getString(COL_CLOB_DATA, c);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
		
		return clobData;
	}

	@Override
	public byte[] getBlobData(Long id) {
		byte[] blobData = null;
		
		Cursor c = db.query(TABLE_NAME, new String[]{COL_BLOB_DATA}, "ID=?", new String[]{id.toString()}, null, null, null);

		if (c.moveToFirst()) {
			blobData = this.getBlob(COL_BLOB_DATA, c);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
		
		return blobData;
	}

	@Override
	public boolean hasMoreUpdates(Long moreThanThisId, String entityId) {
		Cursor c = db.rawQuery(
			    "SELECT COUNT(*) c FROM " + TABLE_NAME + " WHERE "+COL_ID+">? AND "+COL_ENTITY_ID+"=? AND "+COL_OPERATION_NBR+"=2",
			    new String[] {moreThanThisId.toString(), entityId}
		);
		
		c.moveToFirst();
		
		int count = getInt("c", c);
		
		return count > 0;
	}

    @Override
	public void updateVersion(String entityId, Long version) {
		ContentValues values = new ContentValues();
		values.put(COL_VERSION, version);
		db.update(TABLE_NAME, values, COL_ENTITY_ID + "= ?", new String[]{entityId});
	}

	@Override
	public void updateParentVersion(String entityId, Long version) {
		ContentValues values = new ContentValues();
		values.put(COL_PARENT_VERSION, version);
		db.update(TABLE_NAME, values, COL_PARENT_ID + "= ?", new String[]{entityId});
	}

	@Override
	public void updateTxnId(JournalEntry entry) {
		ContentValues values = new ContentValues();
		values.put(COL_TXN_ID, entry.txnId);
		db.update(TABLE_NAME, values, COL_ID + "= ?", new String[]{entry.id.toString()});
	}

    @Override
    public void updateLOBs(JournalEntry entry) {
        ContentValues values = new ContentValues();
        values.put(COL_CLOB_DATA, entry.clobData);
        values.put(COL_BLOB_DATA, entry.blobData);
        db.update(TABLE_NAME, values, COL_ID + "= ?", new String[]{entry.id.toString()});
    }

	private void add(JournalEntry e) {
		ContentValues values = new ContentValues();
		values.put(COL_TXN_ID, e.txnId);
		values.put(COL_OPERATION_NBR, e.operationNbr);
		values.put(COL_ENTITY_ID, e.entityId);
		values.put(COL_CLASS_NAME, e.className);
		values.put(COL_OPERATION, e.operation);
		values.put(COL_VERSION, e.version);
		values.put(COL_PARENT_CLASS_NAME, e.parentClassName);
		values.put(COL_PARENT_ID, e.parentId);
		values.put(COL_PARENT_VERSION, e.parentVersion);
		values.put(COL_CLOB_DATA, e.clobData);
		values.put(COL_BLOB_DATA, e.blobData);
		
		db.insert(TABLE_NAME, null, values);
	}

}
