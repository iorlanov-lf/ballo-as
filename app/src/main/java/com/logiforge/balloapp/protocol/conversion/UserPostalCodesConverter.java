package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCodes;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class UserPostalCodesConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
    }

    @Override
    public Map<Integer, SyncEntity.ValuePair> unpackChanges(MessageUnpacker unpacker) throws IOException {
        return null;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        UserPostalCodes postalCodes = (UserPostalCodes) obj;
        packSyncEntityAttributes(packer, postalCodes);
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        UserPostalCodes postalCodes = new UserPostalCodes();
        unpackSyncEntityAttributes(unpacker, postalCodes);
        return postalCodes;
    }
}
