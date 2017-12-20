package com.logiforge.ballo.sync.protocol.conversion;

import java.io.IOException;
import java.io.InputStream;

public interface ObjectConverter {
	byte[] toBytes(Object obj) throws IOException;
	Object fromBytes(byte[] bytes) throws IOException;
	Object fromInputStream(InputStream istream) throws IOException;

}
