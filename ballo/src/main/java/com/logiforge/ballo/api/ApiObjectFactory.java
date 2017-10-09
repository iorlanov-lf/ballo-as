package com.logiforge.ballo.api;

import com.google.gson.Gson;
import com.logiforge.ballo.net.HttpAdaptor;

/**
 * Created by iorlanov on 9/30/17.
 */

public interface ApiObjectFactory {
    HttpAdaptor getHttpAdapter();
    Gson getGson();
}
