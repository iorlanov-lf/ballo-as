package com.logiforge.ballo.sync.facade;

import android.content.Context;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.auth.facade.AuthEventHandler;
import com.logiforge.ballo.auth.facade.AuthFacade;
import com.logiforge.ballo.auth.facade.OperationContext;
import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.WorkflowDao;
import com.logiforge.ballo.model.db.Workflow;
import com.logiforge.ballo.sync.dao.AppSubscriptionDao;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import java.util.List;

/**
 * Created by iorlanov on 1/10/18.
 */

public class SyncAuthEventHandler implements AuthEventHandler {
    public static final String WF_ON_REGISTER_USER = "SyncAuthEventHandler.onRegisterUser";
    public static final String WF_ON_REGISTER_APP = "SyncAuthEventHandler.onRegisterApp";

    public static class OnRegisterUserState {
        public static final int DELETED_USER_DATA = 10;
        public static final int GOT_REMOTE_SUBSCRIPTIONS = 20;
        public static final int GOT_SUBSCRIPTION_DATA = 30;
    }

    public static class OnRegisterAppState {
        public static final int DELETED_USER_DATA = 10;
        public static final int GOT_REMOTE_SUBSCRIPTIONS = 20;
        public static final int GOT_SUBSCRIPTION_DATA = 30;
    }

    SyncFacade syncFacade;

    public SyncAuthEventHandler(SyncFacade syncFacade) {
        this.syncFacade = syncFacade;
    }

    @Override
    public void onRegisterUser(OperationContext opContext, AppIdentity newIdentity) throws Exception {
        Workflow workflow;
        if(opContext.resume) {
            workflow = opContext.workflow.findChildWorkflow(WF_ON_REGISTER_USER);
            if(workflow == null) {
                WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
                workflow = workflowDao.find(WF_ON_REGISTER_USER);
            }
        } else {
            workflow = new Workflow(WF_ON_REGISTER_USER, null, null);
            opContext.workflow.addChildWorkflow(workflow);
        }

        if(!workflow.skipStep(opContext.resume, OnRegisterUserState.DELETED_USER_DATA)) {
            syncFacade.deleteUserData(opContext.logContext);
            workflow.state = OnRegisterUserState.DELETED_USER_DATA;
        }

        if(!workflow.skipStep(opContext.resume, OnRegisterUserState.GOT_REMOTE_SUBSCRIPTIONS)) {
            syncFacade.getRemoteAppSubscriptions(opContext.context, opContext.logContext, newIdentity);
            workflow.state = OnRegisterUserState.GOT_REMOTE_SUBSCRIPTIONS;
        }

        if(!workflow.skipStep(opContext.resume, OnRegisterUserState.GOT_SUBSCRIPTION_DATA)) {
            syncFacade.downloadSubscriptionData(opContext.context, opContext.logContext);
            workflow.state = OnRegisterUserState.GOT_SUBSCRIPTION_DATA;
        }
    }

    @Override
    public void onRegisterApp(OperationContext opContext, AppIdentity newIdentity) throws Exception {
        Workflow workflow;
        if(opContext.resume) {
            workflow = opContext.workflow.findChildWorkflow(WF_ON_REGISTER_APP);
            if(workflow == null) {
                WorkflowDao workflowDao = Ballo.db().getDao(WorkflowDao.class);
                workflow = workflowDao.find(WF_ON_REGISTER_APP);
            }
        } else {
            workflow = new Workflow(WF_ON_REGISTER_APP, null, null);
            opContext.workflow.addChildWorkflow(workflow);
        }

        if(!workflow.skipStep(opContext.resume, OnRegisterAppState.DELETED_USER_DATA)) {
            syncFacade.deleteUserData(opContext.logContext);
            workflow.state = OnRegisterAppState.DELETED_USER_DATA;
        }

        if(!workflow.skipStep(opContext.resume, OnRegisterAppState.GOT_REMOTE_SUBSCRIPTIONS)) {
            List<AppSubscription> appSubscriptions =
                    syncFacade.getRemoteAppSubscriptions(opContext.context, opContext.logContext, newIdentity);
            workflow.state = OnRegisterAppState.GOT_REMOTE_SUBSCRIPTIONS;
        }


    }
}
