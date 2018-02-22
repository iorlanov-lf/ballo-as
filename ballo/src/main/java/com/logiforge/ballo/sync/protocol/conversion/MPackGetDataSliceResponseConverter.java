package com.logiforge.ballo.sync.protocol.conversion;

import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import com.logiforge.ballo.sync.model.api.DataSlice;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;



public class MPackGetDataSliceResponseConverter extends MPackAuthenticatedCallResponseConverter {

	public void pack(Object obj, MessageBufferPacker packer) throws IOException {
		AuthenticatedCallResponse<DataSlice> response = (AuthenticatedCallResponse<DataSlice>)obj;

		// AuthorizedCallResponse
		this.packAuthorizedCallResponseAttributes(packer, response);
		
		// DataSlice
		if(response.workload == null) {
			packer.packNil();
		} else {
			packer.packString(DataSlice.class.getSimpleName());
			MPackDataSliceConverter dataSliceConverter = new MPackDataSliceConverter();
			dataSliceConverter.pack(response.workload, packer);
		}
	}
	
	public Object unpack(MessageUnpacker unpacker) throws IOException {

		// AuthorizedCallResponse
		AuthenticatedCallResponse<DataSlice> response = new AuthenticatedCallResponse<DataSlice>();
		this.unpackAuthorizedCallResponseAttributes(unpacker, response);
		
		// DataSlice
		String className = unpackNullableString(unpacker);
		if(className == null) {
			response.workload = null;
		} else {
			MPackDataSliceConverter dataSliceConverter = new MPackDataSliceConverter();
			DataSlice dataSlice = (DataSlice)dataSliceConverter.unpack(unpacker);
			response.workload = dataSlice;
		}
		
		return response;
	}
}
