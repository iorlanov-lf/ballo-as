package com.logiforge.ballo.sync.protocol;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;
import com.logiforge.ballo.sync.protocol.dao_facade.SyncEntityDaoFacade;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/8/17.
 */

public abstract class DefaultSyncProtocol implements SyncProtocol {
    protected Map<String, SyncEntityConverter> syncEntityConverters = new HashMap<>();
    protected Map<String, SyncEntityDaoFacade> syncEntityDaoFacades = new HashMap<>();

    @Override
    public SyncEntityConverter getSyncEntityConverter(Class clazz) {
        return syncEntityConverters.get(clazz.getSimpleName());
    }

    @Override
    public SyncEntityConverter getSyncEntityConverter(String className) {
        return syncEntityConverters.get(className);
    }

    @Override
    public SyncEntityDaoFacade getSyncEntityDaoFacade(Class clazz) {
        return syncEntityDaoFacades.get(clazz.getSimpleName());
    }

    @Override
    public SyncEntityDaoFacade getSyncEntityDaoFacade(String className){
        return syncEntityDaoFacades.get(className);
    }
}
