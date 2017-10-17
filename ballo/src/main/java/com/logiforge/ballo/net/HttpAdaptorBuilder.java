package com.logiforge.ballo.net;

/**
 * Created by iorlanov on 8/29/17.
 */

public interface HttpAdaptorBuilder {
    HttpAdaptorBuilder parameter(String name, String value);
    HttpAdaptorBuilder parameter(String name, int value);
    HttpAdaptorBuilder parameter(String name, long value);
    HttpAdaptorBuilder parameter(String name, boolean value);
    HttpAdaptorBuilder useCookies();
    HttpAdaptor build();
}
