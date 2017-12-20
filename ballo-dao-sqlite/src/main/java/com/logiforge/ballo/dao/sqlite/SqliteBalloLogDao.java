package com.logiforge.ballo.dao.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.model.db.BalloLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteBalloLogDao extends SqliteDao implements BalloLogDao {
	
	public static final String TABLE_NAME = "BALLO_LOG";
	private static final String COL_ID = "ID";
	private static final String COL_DATE = "DATE";
	private static final String COL_LEVEL = "LEVEL";
	private static final String COL_JOB = "JOB";
	private static final String COL_TAG = "TAG";
	private static final String COL_MESSAGE = "MESSAGE";
	
	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS BALLO_LOG (" +
		"ID INTEGER PRIMARY KEY AUTOINCREMENT," +
		"DATE INTEGER," +
		"LEVEL TEXT," +
		"JOB TEXT," +
		"TAG TEXT," +
		"MESSAGE TEXT" +
		");";
	

	public SqliteBalloLogDao() {
		super();
	}
	
	protected SqliteBalloLogDao(SQLiteDatabase database) {
		super(database);
	}
	
	public void init() {
		if (!tableExists(TABLE_NAME)) {
			db.execSQL(CREATE_STATEMENT);
		}
	}
	
	public void log(String level, String job, String tag, String message) {
		Date date = new Date();
		ContentValues values = new ContentValues();
		values.put(COL_DATE, date.getTime());
		values.put(COL_LEVEL, level);
		values.put(COL_JOB, job);
		values.put(COL_TAG, tag);
		values.put(COL_MESSAGE, message);
		
		db.insert(TABLE_NAME, null, values);
	}
	
	public void log(BalloLog log) {
		ContentValues values = new ContentValues();
		values.put(COL_DATE, log.date);
		values.put(COL_LEVEL, log.level);
		values.put(COL_JOB, log.job);
		values.put(COL_TAG, log.tag);
		values.put(COL_MESSAGE, log.message);
		
		db.insert(TABLE_NAME, null, values);
	}

    public void log(List<BalloLog> logEntries) {
        if(logEntries != null) {
            for (BalloLog log : logEntries) {
                log(log);
            }
        }
    }
	
	public List<BalloLog> getLog() {
		ArrayList<BalloLog> logs = new ArrayList<BalloLog>();
	
		Cursor c;	
		c = db.query(TABLE_NAME, null, null, null, null, null, COL_ID);
		
		if (c.moveToFirst()) {
            do {
				BalloLog log = new BalloLog(
            			getLong(COL_ID, c),
            			getLong(COL_DATE, c),
            			getString(COL_LEVEL, c),
            			getString(COL_JOB, c),
            			getString(COL_TAG, c),
            			getString(COL_MESSAGE, c)
            	);
            	logs.add(log);
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
		
		return logs;
	}
	
	public void deleteLog(Long id) {
		db.delete(TABLE_NAME, "ID=?", new String[] {id.toString()});
	}

	public void deleteAll() {
		db.delete(TABLE_NAME, null, null);
	}
}
