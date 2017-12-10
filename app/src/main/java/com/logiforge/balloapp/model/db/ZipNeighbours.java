package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class ZipNeighbours extends SyncEntity {
    public ZipNeighbours() {
        super();
    }

    public ZipNeighbours(String id, Long version, Integer syncState) {
        super(id, version, syncState);
    }

    public String getSyncParentId() {
        return id;
    }
    public void applyChanges(Map<Integer, ValuePair> changes) {

    }
}
