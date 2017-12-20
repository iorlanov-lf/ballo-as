package com.logiforge.ballo.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.logiforge.ballo.dao.Dao;

public interface BalloSequenceDao extends Dao {
	void init();
	Long getValue();
}
