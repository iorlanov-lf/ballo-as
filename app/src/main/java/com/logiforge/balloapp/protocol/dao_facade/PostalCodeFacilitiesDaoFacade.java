package com.logiforge.balloapp.protocol.dao_facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.SyncProtocol;
import com.logiforge.ballo.sync.protocol.dao_facade.DefaultSyncEntityDaoFacade;
import com.logiforge.balloapp.dao.FacilityDao;
import com.logiforge.balloapp.model.db.Facility;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iorlanov on 12/14/17.
 */

public class PostalCodeFacilitiesDaoFacade extends DefaultSyncEntityDaoFacade {
    static private final String ENTITY_CLASS_NAME = PostalCodeFacilities.class.getSimpleName();
    static private final String PARENT_CLASS_NAME = null;

    public PostalCodeFacilitiesDaoFacade(SyncProtocol syncProtocol) {
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
        FacilityDao facilityDao = Ballo.db().getSyncDao(Facility.class);
        return facilityDao.findByPostalCode(id);
    }

    @Override
    protected void deleteChildren(String id) {
        FacilityDao facilityDao = Ballo.db().getSyncDao(Facility.class);
        facilityDao.deleteByPostalCode(id);
    }
}
