package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.UserPostalCode;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class UserPostalCodeConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
        if(changes != null) {
            packer.packInt(changes.size());
            for(Map.Entry<Integer, SyncEntity.ValuePair> change : changes.entrySet()) {
                packer.packInt(change.getKey());
                switch (change.getKey()) {
                    case UserPostalCode.FLD_USER_NAME:
                    case UserPostalCode.FLD_POSTAL_CODE:
                        packer.packString((String) change.getValue().oldValue);
                        packer.packString((String) change.getValue().newValue);
                        break;

                    default:
                        throw new IOException("Unknown field id: " + UserPostalCode.class.getSimpleName() + "." + change.getKey());
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
                case UserPostalCode.FLD_USER_NAME:
                case UserPostalCode.FLD_POSTAL_CODE: {
                    String oldValue = unpacker.unpackString();
                    String newValue = unpacker.unpackString();
                    changes.put(key, new SyncEntity.ValuePair(oldValue, newValue));
                    break;
                }

                default:
                    throw new IOException("Unknown field id: " + UserPostalCode.class.getSimpleName() + "." + key);
            }
        }

        return changes;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        UserPostalCode userPostalCode = (UserPostalCode) obj;
        packSyncEntityAttributes(packer, userPostalCode);

        packer.packString(userPostalCode.getUserName());
        packer.packString(userPostalCode.getPostalCode());
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        UserPostalCode userPostalCode = new UserPostalCode();

        unpackSyncEntityAttributes(unpacker, userPostalCode);
        userPostalCode.setUserName(unpacker.unpackString());
        userPostalCode.setPostalCode(unpacker.unpackString());

        return userPostalCode;
    }
}
