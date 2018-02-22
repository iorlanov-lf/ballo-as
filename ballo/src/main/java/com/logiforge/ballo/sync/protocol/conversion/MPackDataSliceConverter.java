package com.logiforge.ballo.sync.protocol.conversion;

import com.logiforge.ballo.sync.model.api.DataSlice;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;


public class MPackDataSliceConverter extends MPackObjectConverter {
	public void pack(Object obj, MessageBufferPacker packer) throws IOException {
		DataSlice dataSlice = (DataSlice)obj;

		// root entity
		SyncEntity rootEntity = dataSlice.rootEntity;
		MPackObjectConverter entityConverter = ConverterFactory.getSyncEntityConverter(rootEntity.getClass().getSimpleName());
		entityConverter.pack(rootEntity, packer);
		
		// child entities
		packer.packInt(dataSlice.childEntities.size());
		for(SyncEntity childEntity : dataSlice.childEntities) {
			entityConverter = ConverterFactory.getSyncEntityConverter(childEntity.getClass().getSimpleName());
			entityConverter.pack(childEntity, packer);
		}
	}
	
	public Object unpack(MessageUnpacker unpacker) throws IOException {

		// root entity
		DataSlice dataSlice = new DataSlice();
		String rootEntityClassName = unpackNullableString(unpacker);
		MPackObjectConverter entityConverter = ConverterFactory.getSyncEntityConverter(rootEntityClassName);
		dataSlice.rootEntity = (SyncEntity)entityConverter.unpack(unpacker);
		
		// child entities
		int childrenCount = unpacker.unpackInt();
		for(int i=0; i<childrenCount; i++) {
			String entityClassName = unpackNullableString(unpacker);
			entityConverter = ConverterFactory.getSyncEntityConverter(entityClassName);
			SyncEntity entity = (SyncEntity)entityConverter.unpack(unpacker);
			dataSlice.childEntities.add(entity);
		}
		
		return dataSlice;
	}
}
