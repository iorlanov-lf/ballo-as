package com.logiforge.ballo.auth.model.api;

/**
 * Created by iorlanov on 2/2/18.
 */

public class RegisterAppWithFacebookParams {
    public String facebookId;
    public String facebookEmail;
    public String facebookDisplayName;
    public String facebookAT;
    public String appId;
    public String gcmId;

    public RegisterAppWithFacebookParams(String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT) {
        this.facebookId = facebookId;
        this.facebookEmail = facebookEmail;
        this.facebookDisplayName = facebookDisplayName;
        this.facebookAT = facebookAT;
        this.appId = appId;
        this.gcmId = gcmId;
    }
}
