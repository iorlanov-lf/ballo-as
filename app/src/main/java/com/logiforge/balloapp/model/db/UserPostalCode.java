package com.logiforge.balloapp.model.db;

import com.logiforge.ballo.sync.dao.SyncEntityDao;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

/**
 * Created by iorlanov on 11/13/17.
 */

public class UserPostalCode extends SyncEntity {
    static public final int FLD_USER_NAME = 10;
    static public final int FLD_POSTAL_CODE = 20;

    String userName;
    String postalCode;

    public UserPostalCode() {
        super();
    }

    public UserPostalCode(String id, Long version, Integer syncState, String userName, String postalCode) {
        super(id, version, syncState);

        this.userName = userName;
        this.postalCode = postalCode;
    }

    public UserPostalCode(String userName, String postalCode) {
        super(userName + postalCode, 0L, SyncEntityDao.SYNC_STATE_ADDED);

        this.userName = userName;
        this.postalCode = postalCode;
    }

    public String getSyncParentId() {
        return userName;
    }

    public void applyChanges(Map<Integer, ValuePair> changes) {
        if(changes != null) {
            for(Map.Entry<Integer, ValuePair> change : changes.entrySet()) {
                switch (change.getKey()) {
                    case FLD_USER_NAME:
                        userName = (String) change.getValue().newValue;
                        break;
                    case FLD_POSTAL_CODE:
                        postalCode = (String) change.getValue().newValue;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
