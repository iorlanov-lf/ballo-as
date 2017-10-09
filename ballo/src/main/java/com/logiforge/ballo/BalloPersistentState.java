package com.logiforge.ballo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;


public class BalloPersistentState {
	private static String TAG = com.logiforge.ballo.BalloPersistentState.class.getSimpleName();
	
	public static final String PREF_GCM_ID = "pref_gcm_id";
	public static final String PREF_APP_VERSION = "pref_app_version";
	public static final String PREF_SERVICE_URL = "pref_cloud_service_url";
	
	static public String serviceUrl(Context context) {		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return  sharedPref.getString(PREF_SERVICE_URL, null);
	}
	
	static public String getGcmId(Context context) {		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		int registeredVersion = getStoredAppVersion(context);
		int currentVersion = getCurrentAppVersion(context);
		
		if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    } else {
	    	return  sharedPref.getString(PREF_GCM_ID, "");
	    }
	}
	
	static public int getStoredAppVersion(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return  sharedPref.getInt(PREF_APP_VERSION, Integer.MIN_VALUE);
	}
	
	static public void setGcmId(Context context, String gcmId) {
		int appVersion = getCurrentAppVersion(context);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString(PREF_GCM_ID, gcmId);
    	editor.putInt(PREF_APP_VERSION, appVersion);
    	editor.commit();
	}

	static public int getCurrentAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
}
