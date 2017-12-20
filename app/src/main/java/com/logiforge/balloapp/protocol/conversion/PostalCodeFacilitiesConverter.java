package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class PostalCodeFacilitiesConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
    }

    @Override
    public Map<Integer, SyncEntity.ValuePair> unpackChanges(MessageUnpacker unpacker) throws IOException {
        return null;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        PostalCodeFacilities postalCodeFacilities = (PostalCodeFacilities) obj;
        packSyncEntityAttributes(packer, postalCodeFacilities);
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        PostalCodeFacilities postalCodeFacilities = new PostalCodeFacilities();
        unpackSyncEntityAttributes(unpacker, postalCodeFacilities);
        return postalCodeFacilities;
    }
}
