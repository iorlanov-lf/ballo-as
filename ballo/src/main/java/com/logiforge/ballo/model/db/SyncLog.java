package com.logiforge.ballo.model.db;

public class SyncLog {
	public static final String LVL_FATAL = "F";
	public static final String LVL_ERROR = "E";
	public static final String LVL_WARNING = "W";
	public static final String LVL_INFO = "I";
	public static final String LVL_DEBUG = "D";
	public static final String LVL_TRACE = "T";
	
	public Long id;
	public Long date;
	public String level;
	public String job;
	public String tag;
	public String message;

	public SyncLog(Long date, String level, String job, String tag, String message) {
		this.date = date;
		this.level = level;
		this.job = job;
		this.tag = tag;
		this.message = message;
	}
	
	public SyncLog(Long id, Long date, String level, String job, String tag, String message) {
		this.id = id;
		this.date = date;
		this.level = level;
		this.job = job;
		this.tag = tag;
		this.message = message;
	}
}
