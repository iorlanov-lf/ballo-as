package com.logiforge.ballo.dao.sqlite.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.dao.sqlite.SqliteDao;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.dao.JournalTransactionDao;
import com.logiforge.ballo.sync.model.api.EntityChanges;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.ArrayList;
import java.util.List;

public class SqliteJournalTransactionDao extends SqliteDao implements JournalTransactionDao {

	public static final String TABLE_NAME = "JOURNAL_TRANSACTION";
	private static final String COL_TXN_ID = "TXN_ID";
	private static final String COL_ACTION = "ACTION";
	private static final String COL_IS_AGGREGATABLE = "IS_AGGREGATABLE";
	private static final String COL_IS_AGGREGATED = "IS_AGGREGATED";

	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS JOURNAL_TRANSACTION (" +
		"TXN_ID INTEGER," +
		"ACTION INTEGER," +
		"IS_AGGREGATABLE INTEGER" +
		"IS_AGGREGATED INTEGER" +
		");";

	public SqliteJournalTransactionDao() {
		super();
	}

	protected SqliteJournalTransactionDao(SQLiteDatabase database) {
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
	public void add(JournalTransaction jtxn) {
		ContentValues values = new ContentValues();
		values.put(COL_TXN_ID, jtxn.txnId);
		values.put(COL_ACTION, jtxn.action);
		values.put(COL_IS_AGGREGATABLE, fromBoolean(jtxn.isAggregatable));
		values.put(COL_IS_AGGREGATED, fromBoolean(jtxn.isAggregated));

		db.insert(TABLE_NAME, null, values);
	}

	@Override
	public void updateIsAggregated(JournalTransaction jtxn, Boolean isAggregated) {
		ContentValues values = new ContentValues();
		values.put(COL_TXN_ID, jtxn.txnId);
		values.put(COL_IS_AGGREGATED, fromBoolean(jtxn.isAggregated));

		db.update(TABLE_NAME, values, COL_TXN_ID + "= ?", new String[]{jtxn.txnId.toString()});
	}

	@Override
	public void delete(JournalTransaction jtxn) {
		db.delete(TABLE_NAME, COL_TXN_ID + "=?", new String[]{jtxn.txnId.toString()});
	}

	@Override
	public List<JournalTransaction> getAll() {
		List<JournalTransaction> journalTransactions = new ArrayList<>();
		
		try {		
			Cursor c;	
			c = db.query(TABLE_NAME, null, null, null, null, null, COL_TXN_ID);
			
			if (c.moveToFirst()) {
	            do {
	            	JournalTransaction jtxn = new JournalTransaction(
	            			getLong(COL_TXN_ID, c),
	            			getLong(COL_ACTION, c),
	            			getBoolean(COL_IS_AGGREGATABLE, c),
							getBoolean(COL_IS_AGGREGATED, c)
	            	);
					journalTransactions.add(jtxn);
	            } while (c.moveToNext());
	        }
	        if (c != null && !c.isClosed()) {
	            c.close();
	        }
		
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage());
		} finally {

		}
		
		return journalTransactions;
	}
}
