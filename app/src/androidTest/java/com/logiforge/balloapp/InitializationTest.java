package com.logiforge.balloapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.AuthParams;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.DefaultAuthFacade;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.DaoContext;
import com.logiforge.ballo.dao.sqlite.SqliteDaoContext;
import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by iorlanov on 10/20/17.
 */

public class InitializationTest {
    private static final String AUTH_URL = "http://10.0.0.21:8080/auth";
    //private static final String AUTH_URL = "https://ballo-test.appspot.com/auth";

    private static final String GCM_SENDER_ID = "662722394825";

    @BeforeClass
    public static void classStartUp() {

    }

    @Test
    public void initTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertNotNull(appContext);
        assertEquals("com.logiforge.balloapp", appContext.getPackageName());

        try {
            BalloAppSQLiteOpenHelper balloAppSQLiteOpenHelper = new BalloAppSQLiteOpenHelper(appContext);
            SQLiteDatabase sqliteDb = balloAppSQLiteOpenHelper.getWritableDatabase();
            DaoContext daoContext = new SqliteDaoContext(sqliteDb);

            ApiObjectFactory apiObjFactory = new ApiObjectFactory() {
                @Override
                public HttpAdaptor getHttpAdapter() {
                    HttpAdaptorBuilder httpAdaptorBuilder = new OkHttpAdaptorBuilder();
                    return httpAdaptorBuilder.build();
                }

                @Override
                public Gson getGson() {
                    return new GsonBuilder().create();
                }
            };

            AuthParams authParams = new AuthParams() {
                @Override
                public String getAuthUrl(String op) {
                    return AUTH_URL;
                }

                @Override
                public String getGcmSenderId() {
                    return GCM_SENDER_ID;
                }
            };

            AuthFacade authFacade = new DefaultAuthFacade(authParams, apiObjFactory);
            Ballo.init(daoContext, authFacade);

            AppIdentityDao appIdentityDao = daoContext.getDao(AppIdentityDao.class);
            AppIdentity appIdentity = appIdentityDao.getAppIdentity();
            assertNotNull(appIdentity);
            assertNotNull(appIdentity.getAppId());

            String appId = Ballo.authFacade().getAppId();
            assertNotNull(appId);
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
}
