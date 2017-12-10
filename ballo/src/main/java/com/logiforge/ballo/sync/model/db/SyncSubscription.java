package com.logiforge.ballo.sync.model.db;

/**
 * Created by iorlanov on 11/13/17.
 */

public class SyncSubscription {
    String rootEntityClassName;
    String rootEntityId;
    boolean isReplicated;
    Long replicatedVersion;
}
