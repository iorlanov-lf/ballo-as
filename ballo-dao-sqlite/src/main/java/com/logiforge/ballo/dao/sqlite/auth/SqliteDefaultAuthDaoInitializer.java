package com.logiforge.ballo.dao.sqlite.auth;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.dao.AuthDaoInitializer;

/**
 * Created by iorlanov on 1/16/18.
 */

public class SqliteDefaultAuthDaoInitializer implements AuthDaoInitializer {
    @Override
    public void init() throws Exception {
        SqliteAppIdentityDao sqliteAppIdentityDao = new SqliteAppIdentityDao();
        sqliteAppIdentityDao.init();
        Ballo.db().registerDao(AppIdentityDao.class.getSimpleName(), sqliteAppIdentityDao);
    }
}
