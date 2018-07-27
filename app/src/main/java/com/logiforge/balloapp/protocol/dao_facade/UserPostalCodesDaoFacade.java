package com.logiforge.balloapp.protocol.dao_facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.dao_facade.DefaultSyncEntityDaoFacade;
import com.logiforge.balloapp.dao.PostalCodeDao;
import com.logiforge.balloapp.dao.UserPostalCodeDao;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCode;
import com.logiforge.balloapp.model.db.UserPostalCodes;

import java.util.List;

/**
 * Created by iorlanov on 12/14/17.
 */

public class UserPostalCodesDaoFacade extends DefaultSyncEntityDaoFacade {
    static private final String ENTITY_CLASS_NAME = UserPostalCodes.class.getSimpleName();
    static private final String PARENT_CLASS_NAME = null;

    public UserPostalCodesDaoFacade(SyncProtocol syncProtocol) {
        super(syncProtocol);
    }

    @Override
    protected String getEntityClassName() {
        return ENTITY_CLASS_NAME;
    }

    @Override
    protected String getSyncParentEntityClassName() {
        return PARENT_CLASS_NAME;
    }

    @Override
    protected boolean isSyncRootEntity() {
        return true;
    }

    @Override
    protected boolean myVersionMatters() {
        return true;
    }

    @Override
    protected boolean parentVersionMatters() {
        return false;
    }

    @Override
    protected List<SyncEntity> getChildren(String id) {
        UserPostalCodeDao userPostalCodeDao = Ballo.db().getSyncDao(UserPostalCode.class);
        return userPostalCodeDao.findAll();
    }

    @Override
    protected void deleteChildren(String id) {
        UserPostalCodeDao userPostalCodeDao = Ballo.db().getSyncDao(UserPostalCode.class);
        userPostalCodeDao.deleteAll();
    }
}
