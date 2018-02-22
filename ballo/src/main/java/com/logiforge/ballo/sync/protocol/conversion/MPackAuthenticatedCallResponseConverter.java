package com.logiforge.ballo.sync.protocol.conversion;

import java.io.IOException;

import com.logiforge.ballo.sync.model.api.AuthenticatedCallResponse;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

public abstract class MPackAuthenticatedCallResponseConverter extends MPackObjectConverter {
	protected void packAuthorizedCallResponseAttributes(MessageBufferPacker packer, AuthenticatedCallResponse response) throws IOException {
		// SimpleResponse
		packer.packBoolean(response.success);
		packer.packInt(response.errorCode);
		packNullableString(packer, response.message);
				
		// AuthorizedCallResponse
		 packer.packInt(response.appAuthResult);
	}
	
	protected void unpackAuthorizedCallResponseAttributes(MessageUnpacker unpacker, AuthenticatedCallResponse response) throws IOException {
		// SimpleResponse
		response.success = unpacker.unpackBoolean();
		response.errorCode = unpacker.unpackInt();
		response.message = unpackNullableString(unpacker);
		
		// AuthorizedCallResponse
		response.appAuthResult = unpacker.unpackInt();
	}
}
