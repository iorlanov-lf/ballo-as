package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class PostalCodes extends SyncEntity {

    public PostalCodes() {
        super();
    }

    public PostalCodes(String id, Long version, Integer syncState) {
        super(id, version, syncState);
    }

    public String getSyncParentId() {
        return null;
    }
    public void applyChanges(Map<Integer, ValuePair> changes) {

    }
}
