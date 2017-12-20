package com.logiforge.balloapp.protocol.dao_facade;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.dao_facade.DefaultSyncEntityDaoFacade;
import com.logiforge.balloapp.model.db.Facility;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;

import java.util.List;

/**
 * Created by iorlanov on 12/14/17.
 */

public class FacilityDaoFacade extends DefaultSyncEntityDaoFacade {
    static private final String ENTITY_CLASS_NAME = Facility.class.getSimpleName();
    static private final String PARENT_CLASS_NAME = PostalCodeFacilities.class.getSimpleName();

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
        return false;
    }

    @Override
    protected List<SyncEntity> getChildren(String id) {
        return null;
    }

    @Override
    protected void deleteChildren(String id) {

    }
}
