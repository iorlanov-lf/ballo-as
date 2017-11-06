package com.logiforge.ballo.dao;

/**
 * Created by iorlanov on 10/20/17.
 */

public interface DaoContext {
    void init() throws Exception;
    public <T extends Dao> T getDao(Class clazz);
}
