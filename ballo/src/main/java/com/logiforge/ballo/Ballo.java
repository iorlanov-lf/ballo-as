package com.logiforge.ballo;

import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.dao.DbAdapter;
import com.logiforge.ballo.sync.facade.SyncFacade;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 10/20/17.
 */

public class Ballo {
    static Ballo instance;

    public static void init(DbAdapter dbAdapter, AuthFacade authFacade, SyncFacade syncFacade) throws Exception {
        instance = new Ballo(dbAdapter, authFacade, syncFacade);

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

    public static SyncFacade syncFacade() {
        if(instance != null) {
            return instance.syncFacade;
        } else {
            return null;
        }
    }

    private DbAdapter dbAdapter;
    private AuthFacade authFacade;
    private SyncFacade syncFacade;
    private Ballo(DbAdapter dbAdapter, AuthFacade authFacade, SyncFacade syncFacade) throws Exception {
        instance = this;

        this.dbAdapter = dbAdapter;
        this.dbAdapter.init();

        this.authFacade = authFacade;
        authFacade.init();

        this.syncFacade = syncFacade;
        syncFacade.init();
    }
}
