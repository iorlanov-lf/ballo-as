package com.logiforge.ballo.auth.model.api;

/**
 * Created by iorlanov on 2/2/18.
 */

public class RegisterAppParams {
    public String userName;
    public String password;
    public String appId;
    public String gcmId;

    public RegisterAppParams(String userName, String password, String appId, String gcmId) {
        this.userName = userName;
        this.password = password;
        this.appId = appId;
        this.gcmId = gcmId;
    }

    public RegisterAppParams(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.appId = null;
        this.gcmId = null;
    }
}
