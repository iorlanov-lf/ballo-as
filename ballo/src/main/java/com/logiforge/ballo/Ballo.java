package com.logiforge.ballo;

import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.dao.DbAdapter;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 10/20/17.
 */

public class Ballo {
    static Ballo instance;

    public static void init(DbAdapter dbAdapter, AuthFacade authFacade, SyncProtocol syncProtocol) throws Exception {
        instance = new Ballo(dbAdapter, authFacade, syncProtocol);

    }

    public static DbAdapter db() {
        if(instance != null) {
            return instance.dbAdapter;
        } else {
            return null;
        }
    }

    public static AuthFacade authFacade() {
        if(instance != null) {
            return instance.authFacade;
        } else {
            return null;
        }
    }

    public static SyncProtocol syncProtocol() {
        if(instance != null) {
            return instance.syncProtocol;
        } else {
            return null;
        }
    }

    private DbAdapter dbAdapter;
    private AuthFacade authFacade;
    private SyncProtocol syncProtocol;
    private Ballo(DbAdapter dbAdapter, AuthFacade authFacade, SyncProtocol syncProtocol) throws Exception {
        instance = this;

        this.dbAdapter = dbAdapter;
        this.dbAdapter.init();

        this.authFacade = authFacade;
        authFacade.init();

        this.syncProtocol = syncProtocol;
        syncProtocol.init();
    }
}
