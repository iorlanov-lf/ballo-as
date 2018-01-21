package com.logiforge.ballo.sync.model.db;

/**
 * Created by iorlanov on 11/13/17.
 */

public class AppSubscription {
    public String entityClassName;
    public String entityId;
    public Long visibleVersion;

    public AppSubscription(){}

    public AppSubscription(String entityClassName, String entityId, Long visibleVersion) {
        this.entityClassName = entityClassName;
        this.entityId = entityId;
        this.visibleVersion = visibleVersion;
    }
}
