package com.logiforge.ballo.dao.sqlite;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.Ballo;

import java.util.Calendar;

/**
 * Created by iorlanov on 10/19/17.
 */

public abstract class SqliteDao {
    protected SQLiteDatabase db = null;

    public SqliteDao() {
        db = ((SqliteDbAdapter) Ballo.db()).getDb();
    }

    protected SqliteDao(SQLiteDatabase db) {
        this.db = db;
    }

    protected abstract String getTableName();

    public String getString(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getString(colIdx);
        }
    }

    public Long getLong(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getLong(colIdx);
        }
    }

    public Integer getInt(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getInt(colIdx);
        }
    }

    public Float getFloat(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getFloat(colIdx);
        }
    }

    public Boolean getBoolean(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getInt(colIdx) == 1;
        }
    }

    public Calendar getCalendar(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(c.getLong(colIdx));
            return cal;
        }
    }

    public byte[] getBlob(String colName, Cursor c) {
        int colIdx = c.getColumnIndexOrThrow(colName);
        if (c.isNull(colIdx)) {
            return null;
        } else {
            return c.getBlob(colIdx);
        }
    }

    public void deleteAll() {
        db.delete(getTableName(), null, null);
    }

    public boolean tableExists(String name) {
        Cursor c = db.rawQuery("SELECT count(name) c FROM sqlite_master WHERE type='table' AND name=?", new String[]{name});
        Integer count = null;
        if (c.moveToFirst()) {
            count = getInt("c", c);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        if (count != null && count == 1) {
            return true;
        } else {
            return false;
        }
    }
}
