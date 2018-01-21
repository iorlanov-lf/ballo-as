package com.logiforge.ballo.auth.facade;

import android.content.Context;

import com.logiforge.ballo.auth.model.db.AppIdentity;

/**
 * Created by iorlanov on 11/7/17.
 */
public interface AuthEventHandler {
    void onRegisterUser(Context context, AppIdentity oldIdentity, AppIdentity newIdentity) throws Exception;
    void onRegisterApp(Context context, AppIdentity oldIdentity, AppIdentity newIdentity) throws Exception;
}
