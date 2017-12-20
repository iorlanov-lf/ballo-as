package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class PostalCodeFacilities extends SyncEntity {
    public PostalCodeFacilities() {
        super();
    }

    public PostalCodeFacilities(String id, Long version, Integer syncState) {
        super(id, version, syncState);
    }

    public String getSyncParentId() {
        return id;
    }
    public void applyChanges(Map<Integer, ValuePair> changes) {

    }
}
