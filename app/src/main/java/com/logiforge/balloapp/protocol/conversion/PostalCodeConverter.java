package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.Facility;
import com.logiforge.balloapp.model.db.PostalCode;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class PostalCodeConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
        if(changes != null) {
            packer.packInt(changes.size());
            for(Map.Entry<Integer, SyncEntity.ValuePair> change : changes.entrySet()) {
                packer.packInt(change.getKey());
                switch (change.getKey()) {
                    case PostalCode.FLD_LATITUDE:
                    case PostalCode.FLD_LONGITUDE:
                        packNullableFloat(packer, (Float) change.getValue().oldValue);
                        packNullableFloat(packer, (Float) change.getValue().newValue);
                        break;

                    default:
                        throw new IOException("Unknown field id: " + PostalCode.class.getSimpleName() + "." + change.getKey());
                }
            }
        }
    }

    @Override
    public Map<Integer, SyncEntity.ValuePair> unpackChanges(MessageUnpacker unpacker) throws IOException {
        int size = unpacker.unpackInt();
        Map<Integer, SyncEntity.ValuePair> changes = new HashMap<>(size);
        for(int i=0; i<size; i++) {
            int key = unpacker.unpackInt();
            switch (key) {
                case PostalCode.FLD_LATITUDE:
                case PostalCode.FLD_LONGITUDE: {
                    Float oldValue = unpackNullableFloat(unpacker);
                    Float newValue = unpackNullableFloat(unpacker);
                    changes.put(key, new SyncEntity.ValuePair(oldValue, newValue));
                    break;
                }

                default:
                    throw new IOException("Unknown field id: " + PostalCode.class.getSimpleName() + "." + key);
            }
        }

        return changes;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        PostalCode postalCode = (PostalCode) obj;
        packSyncEntityAttributes(packer, postalCode);

        packNullableFloat(packer, postalCode.getLatitude());
        packNullableFloat(packer, postalCode.getLongitude());
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        PostalCode postalCode = new PostalCode();

        unpackSyncEntityAttributes(unpacker, postalCode);
        postalCode.setLatitude(unpackNullableFloat(unpacker));
        postalCode.setLongitude(unpackNullableFloat(unpacker));

        return postalCode;
    }
}
