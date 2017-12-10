package com.logiforge.ballo.net;

import java.io.IOException;

public interface HttpAdapter {

    Response execute(PostRequest request) throws IOException;
}
