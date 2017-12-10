package com.logiforge.ballo.sync.protocol;

import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/8/17.
 */

public abstract class DefaultSyncProtocol implements SyncProtocol {
    protected Map<String, SyncEntityConverter> syncEntityConverters = new HashMap<>();

    @Override
    public SyncEntityConverter getSyncEntityConverter(Class clazz) {
        return syncEntityConverters.get(clazz.getName());
    }

    @Override
    public SyncEntityConverter getSyncEntityConverter(String className) {
        return syncEntityConverters.get(className);
    }
}
