package com.logiforge.ballo.dao.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.dao.DaoContext;
import com.logiforge.ballo.dao.sqlite.auth.SqliteAppIdentityDao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 10/20/17.
 */

public class SqliteDaoContext implements DaoContext {
    protected SQLiteDatabase db = null;
    protected Map<String, Dao> daos = new HashMap<>();

    public SqliteDaoContext(SQLiteDatabase db) {
        this.db = db;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void init() throws Exception {
        SqliteAppIdentityDao sqliteAppIdentityDao = new SqliteAppIdentityDao();
        sqliteAppIdentityDao.init();
        daos.put(AppIdentityDao.class.getName(), sqliteAppIdentityDao);

        SqliteBalloLogDao sqliteBalloLogDao = new SqliteBalloLogDao();
        sqliteBalloLogDao.init();
        daos.put(BalloLogDao.class.getName(), sqliteBalloLogDao);
    }

    @Override
    public <T extends Dao> T getDao(Class clazz) {
        return (T)daos.get(clazz.getName());
    }
}
