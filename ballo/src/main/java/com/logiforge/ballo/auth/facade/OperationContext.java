package com.logiforge.ballo.auth.facade;

import android.content.Context;

import com.logiforge.ballo.api.ApiCallBack;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.Workflow;

/**
 * Created by iorlanov on 1/29/18.
 */

public class OperationContext {
    public Context context;
    public LogContext logContext;
    public ApiCallBack callBack;
    public boolean resume;
    public Workflow workflow;

    public OperationContext(Context context, ApiCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        this.resume = false;
    }

    public OperationContext(Context context, LogContext logContext, ApiCallBack callBack, boolean resume,  Workflow workflow) {
        this.context = context;
        this.logContext = logContext;
        this.callBack = callBack;
        this.resume = resume;
        this.workflow = workflow;
    }

    public boolean skipStep(int workflowState) {
        return resume && workflow.state >= workflowState;
    }
}
