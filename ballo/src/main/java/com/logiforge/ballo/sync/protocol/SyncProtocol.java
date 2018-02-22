package com.logiforge.ballo.sync.protocol;

import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;
import com.logiforge.ballo.sync.protocol.dao_facade.SyncEntityDaoFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/8/17.
 */

public interface SyncProtocol {
    void init();
    //SyncEntityConverter getSyncEntityConverter(Class clazz);
    //SyncEntityConverter getSyncEntityConverter(String className);
    SyncEntityDaoFacade getSyncEntityDaoFacade(Class clazz);
    SyncEntityDaoFacade getSyncEntityDaoFacade(String className);
}
