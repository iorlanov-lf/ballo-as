package com.logiforge.balloapp.protocol.dao_facade;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.dao_facade.DefaultSyncEntityDaoFacade;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCode;
import com.logiforge.balloapp.model.db.UserPostalCodes;

import java.util.List;

/**
 * Created by iorlanov on 12/14/17.
 */

public class UserPostalCodeDaoFacade extends DefaultSyncEntityDaoFacade {
    static private final String ENTITY_CLASS_NAME = UserPostalCode.class.getSimpleName();
    static private final String PARENT_CLASS_NAME = UserPostalCodes.class.getSimpleName();

    public UserPostalCodeDaoFacade(SyncProtocol syncProtocol) {
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
        return false;
    }

    @Override
    protected boolean myVersionMatters() {
        return true;
    }

    @Override
    protected boolean parentVersionMatters() {
        return true;
    }

    @Override
    protected List<SyncEntity> getChildren(String id) {
        return null;
    }

    @Override
    protected void deleteChildren(String id) {

    }
}
