package com.logiforge.ballo.net;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by iorlanov on 8/15/17.
 */
public class PostRequest {
    private String url;
    private Map<String, String> stringParts;
    private Map<String, byte[]> binaryParts;
    private int attempts = 1;

    public PostRequest(String url, int attempts) {
        this.url = url;
        this.attempts = attempts;
    }

    public void addStringPart(String name, String value) {
        if(stringParts == null) {
            stringParts = new Hashtable<>();
        }

        stringParts.put(name, value);
    }

    public void addBinaryPart(String name, byte[] value) {
        if(binaryParts == null) {
            binaryParts = new Hashtable<>();
        }

        binaryParts.put(name, value);
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getStringParts() {
        return stringParts;
    }

    public Map<String, byte[]> getBinaryParts() {
        return binaryParts;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getStringPart(String par) {
        if(stringParts != null) {
            return stringParts.get(par);
        } else {
            return null;
        }
    }
}
