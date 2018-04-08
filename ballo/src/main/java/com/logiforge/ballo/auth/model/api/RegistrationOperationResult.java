package com.logiforge.ballo.auth.model.api;

import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.model.db.Workflow;

/**
 * Created by iorlanov on 10/31/17.
 */
public class RegistrationOperationResult {
    public String gcmId = null;
    public int playServicesAvailabilityError = -1;
    public String playServicesAvailabilityErrorString = null;
    public UserAuthResult authResult = null;
    public Workflow workflow;
    public boolean success = false;

    public boolean isUserAuthenticated() {
        return authResult != null &&
               authResult.success &&
               authResult.outcome == UserAuthOutcome.Success &&
               authResult.lfUser != null;
    }

}
