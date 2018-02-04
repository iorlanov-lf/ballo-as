package com.logiforge.ballo.dao;

import com.logiforge.ballo.model.db.BalloLog;
import com.logiforge.ballo.model.db.Workflow;

import java.util.List;

public interface WorkflowDao extends Dao {

	void init();

	void save(Workflow workflow);
	Workflow find(String name);

	void deleteAll();
}
