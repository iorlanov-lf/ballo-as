package com.logiforge.ballo;

import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.DefaultAuthFacade;
import com.logiforge.ballo.dao.DaoContext;

/**
 * Created by iorlanov on 10/20/17.
 */

public class Ballo {
    static Ballo instance;

    public static void init(DaoContext daoContext, AuthFacade authFacade) throws Exception {
        instance = new Ballo(daoContext, authFacade);

    }

    public static DaoContext daoContext() {
        if(instance != null) {
            return instance.daoContext;
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

    private DaoContext daoContext;
    private AuthFacade authFacade;
    private Ballo(DaoContext daoContext, AuthFacade authFacade) throws Exception {
        instance = this;

        this.daoContext = daoContext;
        this.daoContext.init();

        this.authFacade = authFacade;
        authFacade.init();
    }
}
