package com.logiforge.balloapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iorlanov on 2/24/2017.
 */
public class BalloAppSQLiteOpenHelper extends SQLiteOpenHelper
{
    public static final String NAME = "ballo_app_db";
    public static final int VERSION = 1;

    public BalloAppSQLiteOpenHelper(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
