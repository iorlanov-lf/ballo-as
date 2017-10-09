package com.logiforge.ballo.net;

import java.io.InputStream;

/**
 * Created by iorlanov on 8/15/17.
 */
public class Response {
    private String stringResponse;
    private InputStream binaryResponse;

    public Response() {
        this.stringResponse = null;
        this.binaryResponse = null;
    }

    public Response(String stringResponse) {
        this.stringResponse = stringResponse;
    }

    public Response(InputStream binaryResponse) {
        this.binaryResponse = binaryResponse;
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public void setStringResponse(String stringResponse) {
        this.stringResponse = stringResponse;
    }

    public InputStream getBinaryResponse() {
        return binaryResponse;
    }

    public void setBinaryResponse(InputStream binaryResponse) {
        this.binaryResponse = binaryResponse;
    }
}
