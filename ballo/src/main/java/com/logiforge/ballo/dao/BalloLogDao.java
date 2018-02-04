package com.logiforge.ballo.dao;

import com.logiforge.ballo.model.db.BalloLog;

import java.util.List;

public interface BalloLogDao extends Dao {

	void init();
	
	void log(String level, String job, String tag, String message);
	
	void log(BalloLog log);

    void log(List<BalloLog> logEntries);
	
	List<BalloLog> getLog();
	
	void deleteLog(Long id);

	void deleteAll();
}
