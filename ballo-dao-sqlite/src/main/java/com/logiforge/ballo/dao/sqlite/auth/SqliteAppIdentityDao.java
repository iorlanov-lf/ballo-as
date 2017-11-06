package com.logiforge.ballo.dao.sqlite.auth;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.sqlite.SqliteDao;

public class SqliteAppIdentityDao extends SqliteDao implements AppIdentityDao {
	public static final String TABLE_NAME = "BALLO_APP_IDENTITY";
	private static final String COL_APP_ID = "APP_ID";
	private static final String COL_USER_NAME = "USER_NAME";
	private static final String COL_EMAIL = "EMAIL";
	private static final String COL_DISPLAY_NAME = "DISPLAY_NAME";
	private static final String COL_ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String COL_REFRESH_TOKEN = "REFRESH_TOKEN";
	
	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
		"APP_ID TEXT," +
		"USER_NAME TEXT," +
		"EMAIL TEXT," +
		"DISPLAY_NAME TEXT," +
		"ACCESS_TOKEN TEXT," +
		"REFRESH_TOKEN TEXT" +
		");";
	
	public SqliteAppIdentityDao() {
		super();
	}
	
	protected SqliteAppIdentityDao(SQLiteDatabase database) {
		super(database);
	}

	@Override
	public void init() {
		if(!tableExists(TABLE_NAME)) {
		    db.execSQL(CREATE_STATEMENT);

			ContentValues values = new ContentValues();
			db.insert(TABLE_NAME, COL_APP_ID, values);
		}
	}

	@Override
	public void setAppId(String appId) {
	    try {
		    ContentValues values = new ContentValues();
			values.put(COL_APP_ID, appId);
			db.update(TABLE_NAME, values, null, null);
	    } catch (Exception e) {
	    	Log.d("onCreate", e.getMessage() != null?e.getMessage():e.getClass().getSimpleName());
	    }
	}

	@Override
	public AppIdentity getAppIdentity() {

		AppIdentity appIdentity = null;
		Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
		if (c.moveToFirst()) {
	        do {
	        	appIdentity = new AppIdentity(
	        			getString(COL_APP_ID, c),
	        			getString(COL_USER_NAME, c),
	        			getString(COL_EMAIL, c),
	        			getString(COL_DISPLAY_NAME, c),
	        			getString(COL_ACCESS_TOKEN, c),
	        			getString(COL_REFRESH_TOKEN, c)
	        	);
	        } while (c.moveToNext());
	    }
	    if (c != null && !c.isClosed()) {
	        c.close();
	    }
		
		return appIdentity;
	}

	@Override
	public void updateAuthenticationData(String userName, String email, String displayName, String accessToken, String refreshToken)  {
		ContentValues values = new ContentValues();
		values.put(COL_USER_NAME, userName);
		values.put(COL_EMAIL, email);
		values.put(COL_DISPLAY_NAME, displayName);
		values.put(COL_ACCESS_TOKEN, accessToken);
		values.put(COL_REFRESH_TOKEN, refreshToken);
		db.update(TABLE_NAME, values, null, null);
	}

	@Override
	public void updateAuthToken(String authToken) {
		ContentValues values = new ContentValues();
		values.put(COL_ACCESS_TOKEN, authToken);
		db.update(TABLE_NAME, values, null, null);
	}
}
