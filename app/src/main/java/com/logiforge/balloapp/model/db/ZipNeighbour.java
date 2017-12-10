package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class ZipNeighbour extends SyncEntity {
    static public final int FLD_ZIP = 10;
    static public final int FLD_NEARBY_ZIP = 20;

    String zip;
    String nearbyZip;

    public ZipNeighbour() {
        super();
    }

    public ZipNeighbour(String id, Long version, Integer syncState, String zip, String nearbyZip) {
        super(id, version, syncState);

        this.zip = zip;
        this.nearbyZip = nearbyZip;
    }

    @Override
    public String getSyncParentId() {
        return id;
    }

    @Override
    public void applyChanges(Map<Integer, ValuePair> changes) {
        if(changes != null) {
            for(Map.Entry<Integer, ValuePair> change : changes.entrySet()) {
                switch (change.getKey()) {
                    case FLD_ZIP:
                        zip = (String) change.getValue().newValue;
                        break;
                    case FLD_NEARBY_ZIP:
                        nearbyZip = (String) change.getValue().newValue;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        logPropertyChange(FLD_ZIP, this.zip, zip);
        this.zip = zip;
    }

    public String getNearbyZip() {
        return nearbyZip;
    }

    public void setNearbyZip(String nearbyZip) {
        logPropertyChange(FLD_NEARBY_ZIP, this.nearbyZip, nearbyZip);
        this.nearbyZip = nearbyZip;
    }
}
