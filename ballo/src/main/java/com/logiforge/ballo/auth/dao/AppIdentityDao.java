package com.logiforge.ballo.auth.dao;

import java.util.UUID;
/*

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AppIdentityDao extends DbTable {
	public static final String TABLE_NAME = "INSTALLATION";
	private static final String COL_APP_ID = "APP_ID";
	private static final String COL_DEVICE_ID = "DEVICE_ID";
	private static final String COL_USER_NAME = "USER_NAME";
	private static final String COL_EMAIL = "EMAIL";
	private static final String COL_DISPLAY_NAME = "DISPLAY_NAME";
	private static final String COL_ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String COL_REFRESH_TOKEN = "REFRESH_TOKEN";
	
	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
		"APP_ID TEXT," +
		"DEVICE_ID TEXT," +
		"USER_NAME TEXT," +
		"EMAIL TEXT," +
		"DISPLAY_NAME TEXT," +
		"ACCESS_TOKEN TEXT," +
		"REFRESH_TOKEN TEXT" +
		");";
	
	public AppIdentityDao() {
		super();
	}
	
	protected AppIdentityDao(SQLiteDatabase database) {
		super(database);
	}
	
	public void create() {
		if(!tableExists(TABLE_NAME)) {
		    db.execSQL(CREATE_STATEMENT);

			ContentValues values = new ContentValues();
			db.insert(TABLE_NAME, COL_APP_ID, values);
		}
	}
	
	public void setAppAndDeviceIds(String appId, String deviceId) {
	    try {
		    ContentValues values = new ContentValues();
			values.put(COL_APP_ID, appId);
			values.put(COL_DEVICE_ID, deviceId==null ? "APP_ID" + appId : deviceId);
			db.update(TABLE_NAME, values, null, null);
	    } catch (Exception e) {
	    	Log.d("onCreate", e.getMessage() != null?e.getMessage():e.getClass().getSimpleName());
	    }
	}
	
	
	
	public Installation getInstallation() {

		Installation installation = null;
		Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
		if (c.moveToFirst()) {
	        do {
	        	installation = new Installation(
	        			getString(COL_APP_ID, c),
	        			getString(COL_DEVICE_ID, c),
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
		
		return installation;
	}
	
	public void updateAuthorization(String userName, String email, String displayName, String accessToken, String refreshToken)  { 
		ContentValues values = new ContentValues();
		values.put(COL_USER_NAME, userName);
		values.put(COL_EMAIL, email);
		values.put(COL_DISPLAY_NAME, displayName);
		values.put(COL_ACCESS_TOKEN, accessToken);
		values.put(COL_REFRESH_TOKEN, refreshToken);
		db.update(TABLE_NAME, values, null, null);
		
		if(userName != null) {
			((DefaultAuthPlugin)Lavolta.instance().getAuthPlugin()).onUpdateAuthorization(true, accessToken, refreshToken);
		}
	}
	
	public void updateAuthToken(String authToken) throws Exception {
		ContentValues values = new ContentValues();
		values.put(COL_ACCESS_TOKEN, authToken);
		db.update(TABLE_NAME, values, null, null);
	}
	
	
} */
