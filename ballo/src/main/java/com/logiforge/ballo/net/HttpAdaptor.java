package com.logiforge.ballo.net;

import java.io.IOException;

public interface HttpAdaptor {

    Response execute(PostRequest request) throws IOException;
}
