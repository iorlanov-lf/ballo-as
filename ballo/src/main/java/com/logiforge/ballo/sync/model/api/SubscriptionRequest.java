package com.logiforge.ballo.sync.model.api;

/**
 * Created by iorlanov on 2/11/18.
 */

public class SubscriptionRequest {
    public String className = null;
    public String entityId = null;

    public SubscriptionRequest() {

    }

    public SubscriptionRequest(String className, String entityId) {
        this.entityId = entityId;
        this.className = className;
    }
}
