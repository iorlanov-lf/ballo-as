package com.logiforge.ballo.sync.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by iorlanov on 12/8/17.
 */

public interface SyncEntityConverter extends ObjectConverter {
    byte[] changesToBytes(Map<Integer, SyncEntity.ValuePair> changes) throws IOException;
    Map<Integer, SyncEntity.ValuePair> changesFromBytes(byte[] bytes) throws IOException;
    Map<Integer, SyncEntity.ValuePair> changesFromInputStream(InputStream istream) throws IOException;
}
