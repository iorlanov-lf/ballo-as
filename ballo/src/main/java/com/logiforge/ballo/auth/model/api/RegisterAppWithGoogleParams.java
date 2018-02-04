package com.logiforge.ballo.auth.model.api;

/**
 * Created by iorlanov on 2/2/18.
 */

public class RegisterAppWithGoogleParams {
    public String googleId;
    public String googleEmail;
    public String googleDisplayName;
    public String googleAT;
    public String appId;
    public String gcmId;

    public RegisterAppWithGoogleParams(String googleId, String googleEmail, String googleDisplayName, String googleAT) {
        this.googleId = googleId;
        this.googleEmail = googleEmail;
        this.googleDisplayName = googleDisplayName;
        this.googleAT = googleAT;
        this.appId = appId;
        this.gcmId = gcmId;
    }
}
