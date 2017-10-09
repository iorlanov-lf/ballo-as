package com.logiforge.ballo.model.api;

import java.util.ArrayList;
import java.util.Date;

import com.logiforge.ballo.model.db.SyncLog;

public class LogContext {
	private static final String TAG = LogContext.class.getSimpleName();
	
	public String job;
	public String tag;
	public ArrayList<SyncLog> logs = new ArrayList<SyncLog>();
	
	public LogContext(String job, String tag) {
		this.job = job;
		this.tag = tag;
	}
	
	public void addLog(String level, String message) {
		Date date = new Date();
		logs.add(new SyncLog(date.getTime(), level, this.job, this.tag, message));
	}
	
	public void addLog(String level, String tag, String message) {
		Date date = new Date();
		logs.add(new SyncLog(date.getTime(), level, this.job, this.tag, message));
	}
}
