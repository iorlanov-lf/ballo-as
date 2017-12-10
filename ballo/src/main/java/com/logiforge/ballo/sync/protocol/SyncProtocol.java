package com.logiforge.ballo.sync.protocol;

import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/8/17.
 */

public interface SyncProtocol {
    void init();
    SyncEntityConverter getSyncEntityConverter(Class clazz);
    SyncEntityConverter getSyncEntityConverter(String className);
}
