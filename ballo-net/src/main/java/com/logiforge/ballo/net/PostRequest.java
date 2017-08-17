package com.logiforge.ballo.net;

import java.util.Map;

/**
 * Created by iorlanov on 8/15/17.
 */
public class PostRequest {
    public String url;
    public Map<String, String> stringParts;
    public Map<String, byte[]> binaryParts;
    public int attempts = 1;
}
