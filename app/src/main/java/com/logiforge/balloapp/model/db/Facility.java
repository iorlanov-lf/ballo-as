package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class Facility extends SyncEntity {
    static public final int FLD_NAME = 10;
    static public final int FLD_STREET_ADDRESS = 20;
    static public final int FLD_CITY = 30;
    static public final int FLD_STATE = 40;
    static public final int FLD_ZIP = 50;
    static public final int FLD_DIRECTION = 60;
    static public final int FLD_IS_REFERENCE_ENTITY = 70;
    static public final int FLD_ORIGINATOR_USER_NAME = 80;

    String name;
    String streetAddress;
    String city;
    String state;
    String zip;
    String directions;

    Boolean isReferenceEntity;
    String originatorUserName;

    public Facility() {
        super();
    }

    public Facility(String id, Long version, Integer syncState,
                    String name, String streetAddress, String city, String state, String zip, String directions,
                    Boolean isReferenceEntity, String originatorUserName) {
        super(id, version, syncState);

        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.directions = directions;

        this.isReferenceEntity = isReferenceEntity;
        this.originatorUserName = originatorUserName;
    }

    @Override
    public String getSyncParentId() {
        return zip;
    }

    @Override
    public void applyChanges(Map<Integer, ValuePair> changes) {
        if(changes != null) {
            for(Map.Entry<Integer, ValuePair> change : changes.entrySet()) {
                switch (change.getKey()) {
                    case FLD_NAME:
                        name = (String) change.getValue().newValue;
                        break;
                    case FLD_STREET_ADDRESS:
                        streetAddress = (String) change.getValue().newValue;
                        break;
                    case FLD_CITY:
                        city = (String) change.getValue().newValue;
                        break;
                    case FLD_STATE:
                        state = (String) change.getValue().newValue;
                        break;
                    case FLD_ZIP:
                        zip = (String) change.getValue().newValue;
                        break;
                    case FLD_DIRECTION:
                        directions = (String) change.getValue().newValue;
                        break;
                    case FLD_IS_REFERENCE_ENTITY:
                        isReferenceEntity = (Boolean) change.getValue().newValue;
                        break;
                    case FLD_ORIGINATOR_USER_NAME:
                        originatorUserName = (String) change.getValue().newValue;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logPropertyChange(FLD_NAME, this.name, name);
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        logPropertyChange(FLD_STREET_ADDRESS, this.streetAddress, streetAddress);
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        logPropertyChange(FLD_CITY, this.city, city);
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        logPropertyChange(FLD_STATE, this.state, state);
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        logPropertyChange(FLD_ZIP, this.zip, zip);
        this.zip = zip;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        logPropertyChange(FLD_DIRECTION, this.directions, directions);
        this.directions = directions;
    }

    public Boolean getIsReferenceEntity() {
        return isReferenceEntity;
    }

    public void setReferenceEntity(Boolean isReferenceEntity) {
        logPropertyChange(FLD_IS_REFERENCE_ENTITY, this.isReferenceEntity, isReferenceEntity);
        isReferenceEntity = isReferenceEntity;
    }

    public String getOriginatorUserName() {
        return originatorUserName;
    }

    public void setOriginatorUserName(String originatorUserName) {
        logPropertyChange(FLD_ORIGINATOR_USER_NAME, this.originatorUserName, originatorUserName);
        this.originatorUserName = originatorUserName;
    }
}
