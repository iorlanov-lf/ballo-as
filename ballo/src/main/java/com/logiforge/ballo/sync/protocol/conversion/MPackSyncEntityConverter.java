package com.logiforge.ballo.sync.protocol.conversion;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

public abstract class MPackSyncEntityConverter extends MPackObjectConverter implements SyncEntityConverter {
	public byte[] changesToBytes(Map<Integer, SyncEntity.ValuePair> changes) throws IOException {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packChanges(changes, packer);
        byte[] bytes = packer.toByteArray();
        packer.close();
        return bytes;
	}

    public Map<Integer, SyncEntity.ValuePair> changesFromBytes(byte[] bytes) throws IOException {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        Map<Integer, SyncEntity.ValuePair> changes = unpackChanges(unpacker);
        unpacker.close();
        return changes;
    }

    public Map<Integer, SyncEntity.ValuePair> changesFromInputStream(InputStream istream) throws IOException {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(istream);
        Map<Integer, SyncEntity.ValuePair> changes = unpackChanges(unpacker);
        unpacker.close();
        return changes;
    }

	protected void packSyncEntityAttributes(MessageBufferPacker packer, SyncEntity entity) throws IOException {
		//packer.packString(entity.getClass().getSimpleName());
        packer.packString(entity.id);
        packNullableLong(packer, entity.version);
	}
	
	protected void unpackSyncEntityAttributes(MessageUnpacker unpacker, SyncEntity entity) throws IOException {
		entity.id = unpacker.unpackString();
		entity.version = unpackNullableLong(unpacker);
	}

    abstract protected void packChanges(
	        Map<Integer, SyncEntity.ValuePair> changes, MessageBufferPacker packer) throws IOException;
    abstract protected Map<Integer, SyncEntity.ValuePair> unpackChanges(MessageUnpacker unpacker) throws IOException;
}
