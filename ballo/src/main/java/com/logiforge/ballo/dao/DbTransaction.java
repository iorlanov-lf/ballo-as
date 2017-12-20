package com.logiforge.ballo.dao;

import java.util.concurrent.Semaphore;

public class DbTransaction {
	public static Semaphore semaphore = new Semaphore(1);

	public boolean isSemaforeSynchronized = false;
	public boolean isSuccessful = false;
	public Long id = null;
    public Long action = null;
	public Long operationNbr = null;

	public DbTransaction(Long id, Long action, boolean isSemaforeSynchronized) {
		this.isSemaforeSynchronized = isSemaforeSynchronized;
		this.id = id;
        this.action = action;
		this.operationNbr = id * 100;
	}
	
	public Long nextOperationNbr() {
		Long ret = Long.valueOf(operationNbr);
		operationNbr++;
		return ret;
	}
}
