package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class PostalCode extends SyncEntity {
    public static final String KNOWN_PARENT_CODE = "all_postal_codes";

    static public final int FLD_LONGITUDE = 10;
    static public final int FLD_LATITUDE = 20;

    Float longitude;
    Float latitude;

    public PostalCode() {
        super();
    }

    public PostalCode(String id, Long version, Integer syncState, Float longitude, Float latitude) {
        super(id, version, syncState);

        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getSyncParentId() {
        return KNOWN_PARENT_CODE;
    }

    public void applyChanges(Map<Integer, ValuePair> changes) {
        if(changes != null) {
            for(Map.Entry<Integer, ValuePair> change : changes.entrySet()) {
                switch (change.getKey()) {
                    case FLD_LONGITUDE:
                        longitude = (Float) change.getValue().newValue;
                        break;
                    case FLD_LATITUDE:
                        latitude = (Float) change.getValue().newValue;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }
}
