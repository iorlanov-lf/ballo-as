package com.logiforge.ballo.model.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iorlanov on 1/27/18.
 */

public class Workflow {
    public String name;
    public Integer state = Integer.MIN_VALUE;
    public String clob;
    public String parentName;
    List<Workflow> childWorkflows;

    public Workflow(String name) {
        this.name = name;
    }

    public Workflow(String name, Integer state, String clob, String parentName) {
        this.name = name;
        this.state = state;
        this.clob = clob;
        this.parentName = parentName;
    }

    public List<Workflow> getChildWorkflows() {
        return childWorkflows;
    }

    public void addChildWorkflow(Workflow workflow) {
        if(childWorkflows == null) {
            childWorkflows = new ArrayList<>();
        }

        workflow.parentName = this.name;
        childWorkflows.add(workflow);
    }

    public Workflow findChildWorkflow(String name) {
        if(childWorkflows == null) {
            return null;
        } else {
            for(Workflow workflow : childWorkflows) {
                if(workflow.name.equals(name)) {
                    return workflow;
                } else {
                    Workflow decendentWorkflow = workflow.findChildWorkflow(name);
                    if(decendentWorkflow != null) {
                        return decendentWorkflow;
                    }
                }
            }
        }

        return null;
    }

    public boolean skipStep(boolean resume, int state) {
        return resume && this.state > state;
    }
}
