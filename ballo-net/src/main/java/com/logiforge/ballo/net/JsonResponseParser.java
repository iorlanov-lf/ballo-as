package com.logiforge.ballo.net;

import java.io.IOException;

public interface JsonResponseParser<T> {
	T parseJsonContent(String content) throws IOException;
}