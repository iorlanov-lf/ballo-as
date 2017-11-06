package com.logiforge.ballo.model.api;

import java.util.ArrayList;
import java.util.Date;

import com.logiforge.ballo.model.db.BalloLog;

public class LogContext {
	private static final String TAG = LogContext.class.getSimpleName();
	
	public String job;
	public String tag;
	public ArrayList<BalloLog> logs = new ArrayList<BalloLog>();
	
	public LogContext(String job, String tag) {
		this.job = job;
		this.tag = tag;
	}
	
	public void addLog(String level, String message) {
		Date date = new Date();
		logs.add(new BalloLog(date.getTime(), level, this.job, this.tag, message));
	}
	
	public void addLog(String level, String tag, String message) {
		Date date = new Date();
		logs.add(new BalloLog(date.getTime(), level, this.job, tag, message));
	}
}
