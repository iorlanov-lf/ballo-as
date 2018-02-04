package com.logiforge.ballo.auth.facade;

import android.content.Context;

import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.model.api.LogContext;

/**
 * Created by iorlanov on 11/7/17.
 */
public interface AuthEventHandler {
    void onRegisterUser(OperationContext opContext, AppIdentity newIdentity) throws Exception;
    void onRegisterApp(OperationContext opContext, AppIdentity newIdentity) throws Exception;
}
