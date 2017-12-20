package com.logiforge.ballo.sync.protocol.conversion;

import java.io.IOException;
import java.io.InputStream;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

public abstract class MPackObjectConverter implements ObjectConverter {
	abstract protected void pack(Object obj, MessageBufferPacker packer) throws IOException;
	abstract protected Object unpack(MessageUnpacker unpacker) throws IOException;

	@Override
	public byte[] toBytes(Object obj) throws IOException {
		MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
		pack(obj, packer);
		byte[] bytes = packer.toByteArray();
		packer.close();
		return bytes;
	}
	
	@Override
	public Object fromBytes(byte[] bytes) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
		Object obj = unpack(unpacker);
		unpacker.close();
		return obj;
	}
	
	@Override
	public Object fromInputStream(InputStream istream) throws IOException {
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(istream);
		Object obj = unpack(unpacker);
		unpacker.close();
		return obj;
	}
	
	protected void packNullableInt(MessageBufferPacker packer, Integer value) throws IOException {
        if(value == null)
            packer.packNil();
        else
            packer.packInt(value);
    }

    protected void packNullableLong(MessageBufferPacker packer, Long value) throws IOException {
        if(value == null)
            packer.packNil();
        else
            packer.packLong(value);
    }
    
    protected void packNullableFloat(MessageBufferPacker packer, Float value) throws IOException {
        if(value == null)
            packer.packNil();
        else
            packer.packFloat(value);
    }

    protected void packNullableString(MessageBufferPacker packer, String value) throws IOException {
        if(value == null)
            packer.packNil();
        else
            packer.packString(value);
    }
    
    protected void packNullableBytes(MessageBufferPacker packer, byte[] value) throws IOException {
        if(value == null || value.length == 0) {
            packer.packNil();
        } else {
        	packer.packBinaryHeader(value.length);
            packer.writePayload(value);
        }
    }

    protected void packNullableBoolean(MessageBufferPacker packer, Boolean value) throws IOException {
        if(value == null)
            packer.packNil();
        else
            packer.packBoolean(value);
    }
    
    protected Integer unpackNullableInt(MessageUnpacker unpacker) throws IOException {
    	Value v = unpacker.unpackValue();
    	
    	if(v.isNilValue()) {
    		return null;
    	} else {
    		return v.asIntegerValue().asInt();
    	}
    }
    
    protected Long unpackNullableLong(MessageUnpacker unpacker) throws IOException {
    	Value v = unpacker.unpackValue();
    	
    	if(v.isNilValue()) {
    		return null;
    	} else {
    		return v.asIntegerValue().asLong();
    	}
    }
    
    protected Float unpackNullableFloat(MessageUnpacker unpacker) throws IOException {
    	Value v = unpacker.unpackValue();
    	
    	if(v.isNilValue()) {
    		return null;
    	} else {
    		return v.asFloatValue().toFloat();
    	}
    }
    
    protected String unpackNullableString(MessageUnpacker unpacker) throws IOException {
    	Value v = unpacker.unpackValue();
    	
    	if(v.isNilValue()) {
    		return null;
    	} else {
    		return v.asStringValue().asString();
    	}
    }
    
    protected byte[] unpackNullableBytes(MessageUnpacker unpacker) throws IOException {
    	Value v = unpacker.unpackValue();
    	
    	if(v.isNilValue()) {
    		return null;
    	} else {
    		byte[] bytes = v.asBinaryValue().asByteArray();
    		if(bytes.length > 0) {
    			return bytes;
    		} else {
    			return null;
    		}
    	}
    }

    protected Boolean unpackNullableBoolean(MessageUnpacker unpacker) throws IOException {
        Value v = unpacker.unpackValue();

        if(v.isNilValue()) {
            return null;
        } else {
            return v.asBooleanValue().getBoolean();
        }
    }
}
