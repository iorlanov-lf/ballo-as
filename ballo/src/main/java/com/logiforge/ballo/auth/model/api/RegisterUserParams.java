package com.logiforge.ballo.auth.model.api;

/**
 * Created by iorlanov on 2/2/18.
 */

public class RegisterUserParams {
    public String userName;
    public String email;
    public String displayName;
    public String password;
    public String appId;
    public String gcmId;

    public RegisterUserParams(String userName, String email, String displayName, String password, String appId, String gcmId) {
        this.userName = userName;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.appId = appId;
        this.gcmId = gcmId;
    }

    public RegisterUserParams(String userName, String email, String displayName, String password) {
        this.userName = userName;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.appId = null;
        this.gcmId = null;
    }
}
