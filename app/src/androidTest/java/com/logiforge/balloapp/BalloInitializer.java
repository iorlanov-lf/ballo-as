package com.logiforge.balloapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.api.AuthParams;
import com.logiforge.ballo.auth.dao.AppIdentityDao;
import com.logiforge.ballo.auth.dao.AuthDaoInitializer;
import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.DefaultAuthFacade;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.sqlite.auth.SqliteDefaultAuthDaoInitializer;
import com.logiforge.ballo.dao.sqlite.sync.SqliteSyncDaoInitializer;
import com.logiforge.ballo.net.HttpAdapter;
import com.logiforge.ballo.net.HttpAdaptorBuilder;
import com.logiforge.ballo.net.okhttp.OkHttpAdaptorBuilder;
import com.logiforge.ballo.sync.api.SyncApiParams;
import com.logiforge.ballo.sync.dao.SyncDaoInitializer;
import com.logiforge.ballo.sync.facade.SyncAuthEventHandler;
import com.logiforge.ballo.sync.facade.SyncFacade;
import com.logiforge.ballo.sync.protocol.DefaultSyncProtocol;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.balloapp.dao.BalloAppDbAdapter;
import com.logiforge.balloapp.model.db.Facility;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.protocol.conversion.FacilityConverter;
import com.logiforge.balloapp.protocol.conversion.PostalCodeConverter;
import com.logiforge.balloapp.protocol.conversion.PostalCodeFacilitiesConverter;
import com.logiforge.balloapp.protocol.conversion.PostalCodesConverter;
import com.logiforge.balloapp.protocol.dao_facade.FacilityDaoFacade;
import com.logiforge.balloapp.protocol.dao_facade.PostalCodeDaoFacade;
import com.logiforge.balloapp.protocol.dao_facade.PostalCodeFacilitiesDaoFacade;
import com.logiforge.balloapp.protocol.dao_facade.PostalCodesDaoFacade;

/**
 * Created by iorlanov on 12/13/17.
 */

public class BalloInitializer {
    public static final String AUTH_URL = "http://10.0.0.21:8080/auth";
    public static final String SYNC_URL = "http://10.0.0.21:8080/sync";
    //private static final String AUTH_URL = "https://ballo-test.appspot.com/auth";
    //private static final String SYNC_URL = "https://ballo-test.appspot.com/sync";

    private static final String GCM_SENDER_ID = "662722394825";

    public static void init(Context appContext) throws Exception {
        // d/b adapter
        BalloAppSQLiteOpenHelper balloAppSQLiteOpenHelper = new BalloAppSQLiteOpenHelper(appContext);
        SQLiteDatabase sqliteDb = balloAppSQLiteOpenHelper.getWritableDatabase();
        BalloAppDbAdapter dbAdapter = new BalloAppDbAdapter(sqliteDb);

        // authentication facade
        AuthDaoInitializer authDaoInitializer = new SqliteDefaultAuthDaoInitializer();

        ApiObjectFactory apiObjFactory = new ApiObjectFactory() {
            @Override
            public HttpAdapter getHttpAdapter() {
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

        AuthFacade authFacade = new TestAuthFacade(authDaoInitializer, authParams, apiObjFactory);

        // sync protocol
        SyncDaoInitializer syncDaoInitializer = new SqliteSyncDaoInitializer();

        SyncProtocol syncProtocol = new DefaultSyncProtocol() {

            @Override
            public void init() {
                this.syncEntityConverters.put(Facility.class.getSimpleName(), new FacilityConverter());
                this.syncEntityDaoFacades.put(Facility.class.getSimpleName(), new FacilityDaoFacade(this));

                this.syncEntityConverters.put(PostalCodeFacilities.class.getSimpleName(), new PostalCodeFacilitiesConverter());
                this.syncEntityDaoFacades.put(PostalCodeFacilities.class.getSimpleName(), new PostalCodeFacilitiesDaoFacade(this));

                this.syncEntityConverters.put(PostalCode.class.getSimpleName(), new PostalCodeConverter());
                this.syncEntityDaoFacades.put(PostalCode.class.getSimpleName(), new PostalCodeDaoFacade(this));

                this.syncEntityConverters.put(PostalCodes.class.getSimpleName(), new PostalCodesConverter());
                this.syncEntityDaoFacades.put(PostalCodes.class.getSimpleName(), new PostalCodesDaoFacade(this));
            }
        };

        SyncApiParams syncApiParams = new SyncApiParams() {
            @Override
            public String getSyncUrl(String op) {
                return SYNC_URL;
            }
        };

        SyncFacade syncFacade = new SyncFacade(syncDaoInitializer, syncProtocol, syncApiParams, apiObjFactory);

        authFacade.registerEventHandler(new SyncAuthEventHandler(syncFacade));

        // Ballo initialization
        Ballo.init(dbAdapter, authFacade, syncFacade);
    }

    public static void emulateUserRegistration() throws Exception {
        ((TestAuthFacade)Ballo.authFacade()).emulateUserRegistration(
                "iorlanov", "iorlanov@comcast.net", "Igor Orlanov",
                "fakeAccessToken", "fakeRefreshToken");
    }

    public static class TestAuthFacade extends DefaultAuthFacade {
        public TestAuthFacade(AuthDaoInitializer daoInitializer, AuthParams authParam, ApiObjectFactory apiObjectFactory) {
            super(daoInitializer, authParam, apiObjectFactory);
        }

        public void emulateUserRegistration(String userName, String email, String displayName,
                                            String accessToken, String refreshToken) {
            AppIdentityDao appIdentityDao = Ballo.db().getDao(AppIdentityDao.class);
            appIdentityDao.updateAuthenticationData(
                    userName, email, displayName,
                    accessToken, refreshToken);

            appIdentity = new AppIdentity(
                    appIdentity.getAppId(),
                    userName, email, displayName,
                    accessToken, refreshToken);
        }
    }
}
