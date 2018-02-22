package com.logiforge.ballo.sync.model.api;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iorlanov on 2/11/18.
 */

public class DataSlice {
    public SyncEntity rootEntity = null;
    public List<SyncEntity> childEntities = null;

    public DataSlice() {
        childEntities = new ArrayList<SyncEntity>();
    }

    public DataSlice(SyncEntity rootEntity) {
        this.rootEntity = rootEntity;
        this.childEntities = new ArrayList<SyncEntity>();
    }
}
