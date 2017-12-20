package com.logiforge.balloapp.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.MPackSyncEntityConverter;
import com.logiforge.balloapp.model.db.Facility;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by iorlanov on 12/11/17.
 */

public class FacilityConverter extends MPackSyncEntityConverter {
    @Override
    protected void packChanges(Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException {
        if(changes != null) {
            packer.packInt(changes.size());
            for(Map.Entry<Integer, SyncEntity.ValuePair> change : changes.entrySet()) {
                packer.packInt(change.getKey());
                switch (change.getKey()) {
                    case Facility.FLD_NAME:
                    case Facility.FLD_STREET_ADDRESS:
                    case Facility.FLD_CITY:
                    case Facility.FLD_STATE:
                    case Facility.FLD_POSTAL_CODE:
                    case Facility.FLD_DIRECTION:
                    case Facility.FLD_ORIGINATOR_USER_NAME:
                        packNullableString(packer, (String) change.getValue().oldValue);
                        packNullableString(packer, (String) change.getValue().newValue);
                        break;

                    case Facility.FLD_IS_REFERENCE_ENTITY:
                        packNullableBoolean(packer, (Boolean) change.getValue().oldValue);
                        packNullableBoolean(packer, (Boolean) change.getValue().newValue);
                        break;

                    default:
                        throw new IOException("Unknown field id: " + Facility.class.getSimpleName() + "." + change.getKey());
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
                case Facility.FLD_NAME:
                case Facility.FLD_STREET_ADDRESS:
                case Facility.FLD_CITY:
                case Facility.FLD_STATE:
                case Facility.FLD_POSTAL_CODE:
                case Facility.FLD_DIRECTION:
                case Facility.FLD_ORIGINATOR_USER_NAME: {
                    String oldValue = unpackNullableString(unpacker);
                    String newValue = unpackNullableString(unpacker);
                    changes.put(key, new SyncEntity.ValuePair(oldValue, newValue));
                    break;
                }

                case Facility.FLD_IS_REFERENCE_ENTITY: {
                    Boolean oldValue = unpackNullableBoolean(unpacker);
                    Boolean newValue = unpackNullableBoolean(unpacker);
                    changes.put(key, new SyncEntity.ValuePair(oldValue, newValue));
                    break;
                }

                default:
                    throw new IOException("Unknown field id: " + Facility.class.getSimpleName() + "." + key);
            }
        }

        return changes;
    }

    @Override
    public void pack(Object obj, MessageBufferPacker packer) throws IOException {
        Facility facility = (Facility) obj;
        packSyncEntityAttributes(packer, facility);

        packNullableString(packer, facility.getName());
        packNullableString(packer, facility.getStreetAddress());
        packNullableString(packer, facility.getCity());
        packNullableString(packer, facility.getState());
        packNullableString(packer, facility.getPostalCode());
        packNullableString(packer, facility.getDirections());
        packNullableBoolean(packer, facility.getIsReferenceEntity());
        packNullableString(packer, facility.getOriginatorUserName());
    }

    @Override
    public Object unpack(MessageUnpacker unpacker) throws IOException {
        Facility facility = new Facility();

        unpackSyncEntityAttributes(unpacker, facility);

        facility.setNameNoLog(unpackNullableString(unpacker));
        facility.setStreetAddressNoLog(unpackNullableString(unpacker));
        facility.setCityNoLog(unpackNullableString(unpacker));
        facility.setStateNoLog(unpackNullableString(unpacker));
        facility.setZipNoLog(unpackNullableString(unpacker));
        facility.setDirectionsNoLog(unpackNullableString(unpacker));
        facility.setIsReferenceEntityNoLog(unpackNullableBoolean(unpacker));
        facility.setOriginatorUserNameNoLog(unpackNullableString(unpacker));

        return facility;
    }
}
