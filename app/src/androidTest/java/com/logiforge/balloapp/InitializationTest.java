package com.logiforge.balloapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.model.db.AppIdentity;

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


    @BeforeClass
    public static void classStartUp() {

    }

    @Test
    public void initTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertNotNull(appContext);
        assertEquals("com.logiforge.balloapp", appContext.getPackageName());

        try {
            if(Ballo.db() == null) {
                BalloInitializer.init(appContext);
            }

            AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
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
