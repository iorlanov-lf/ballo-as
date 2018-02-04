package com.logiforge.ballo.dao.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.dao.BalloSequenceDao;

public class SqliteBalloSequenceDao extends SqliteDao implements BalloSequenceDao {
	public static final String TABLE_NAME = "SEQUENCE";
	private static final String COL_VALUE = "VALUE";
	
	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS SEQUENCE (" +
		"VALUE INTEGER" +
		");";
	
	
	public SqliteBalloSequenceDao() {
		super();
	}
	
	protected SqliteBalloSequenceDao(SQLiteDatabase database) {
		super(database);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void init() {
		if(!tableExists(TABLE_NAME)) {
		    db.execSQL(CREATE_STATEMENT);
		    ContentValues values = new ContentValues();
			values.put(COL_VALUE, 0);
			db.insert(TABLE_NAME, null, values);
		}
	}

	@Override
	public Long getValue() {

		Long currentValue = null;
		Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
		if (c.moveToFirst()) {
	        currentValue = getLong(COL_VALUE, c);
	    }
	    if (c != null && !c.isClosed()) {
	        c.close();
	    }
	    
	    ContentValues values = new ContentValues();
		values.put(COL_VALUE, currentValue + 1);
		db.update(TABLE_NAME, values, null, null);
		
		return currentValue;
	}
}
