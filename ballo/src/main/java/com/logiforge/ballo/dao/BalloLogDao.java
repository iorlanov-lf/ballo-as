package com.logiforge.ballo.dao;

import com.logiforge.ballo.model.db.BalloLog;

import java.util.List;

public interface BalloLogDao extends Dao {

	void init();
	
	public void log(String level, String job, String tag, String message);
	
	public void log(BalloLog log);

    public void log(List<BalloLog> logEntries);
	
	public List<BalloLog> getLog();
	
	public void deleteLog(Long id);

	public void deleteAll();
}
