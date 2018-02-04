package com.logiforge.ballo.model.db;

import java.util.List;

/**
 * Created by iorlanov on 1/27/18.
 */

public class Workflow {
    public String name;
    public Integer state;
    public String clob;
    public List<Workflow> childWorkflows;

    public Workflow(String name, Integer state, String clob) {
        this.name = name;
        this.state = state;
        this.clob = clob;
    }
}
