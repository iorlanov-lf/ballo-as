package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.PostalCodes;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class PostalCodesConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
    }

    @Override
    public Map<Integer, SyncEntity.ValuePair> unpackChanges(MessageUnpacker unpacker) throws IOException {
        return null;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        PostalCodes postalCodes = (PostalCodes) obj;
        packSyncEntityAttributes(packer, postalCodes);
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        PostalCodes postalCodes = new PostalCodes();
        unpackSyncEntityAttributes(unpacker, postalCodes);
        return postalCodes;
    }
}
