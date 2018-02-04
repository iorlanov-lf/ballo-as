package com.logiforge.ballo.dao.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.dao.BalloLogDao;
import com.logiforge.ballo.dao.WorkflowDao;
import com.logiforge.ballo.model.db.BalloLog;
import com.logiforge.ballo.model.db.Workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SqliteWorkflowDao extends SqliteDao implements WorkflowDao {

	public static final String TABLE_NAME = "BALLO_WORKFLOW";
	private static final String COL_NAME = "NAME";
	private static final String COL_STATE = "STATE";
    private static final String COL_CLOB = "CLOB";
	private static final String COL_PARENT_NAME = "PARENT_NAME";

	private static final String CREATE_STATEMENT =
		"CREATE TABLE IF NOT EXISTS BALLO_WORKFLOW (" +
		"NAME TEXT NOT NULL," +
		"STATE INTEGER," +
        "CLOB TEXT," +
		"PARENT_NAME TEXT" +
		");";


	public SqliteWorkflowDao() {
		super();
	}

	protected SqliteWorkflowDao(SQLiteDatabase database) {
		super(database);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void init() {
		if (!tableExists(TABLE_NAME)) {
			db.execSQL(CREATE_STATEMENT);
		}
	}

    @Override
	public void save(Workflow workflow) {
	    db.delete(TABLE_NAME, COL_NAME + "=? or " + COL_PARENT_NAME + "=?", new String[]{workflow.name, workflow.name});

        ContentValues values = new ContentValues();
        values.put(COL_NAME, workflow.name);
        values.put(COL_STATE, workflow.state);
        values.put(COL_CLOB, workflow.clob);
        db.insert(TABLE_NAME, null, values);

        if(workflow.childWorkflows != null) {
            for (Workflow childWorkflow : workflow.childWorkflows) {
                ContentValues childValues = new ContentValues();
                childValues.put(COL_NAME, childWorkflow.name);
                childValues.put(COL_STATE, childWorkflow.state);
                childValues.put(COL_CLOB, childWorkflow.clob);
                childValues.put(COL_PARENT_NAME, workflow.name);
                db.insert(TABLE_NAME, null, childValues);
            }
        }
    }


	
	public Workflow find(String name) {
        Workflow workflow = null;
        Cursor c;
        c = db.query(TABLE_NAME, null, COL_NAME+"=?", new String[]{name}, null, null, null);

        if (c.moveToFirst()) {
            do {
                workflow = new Workflow(
                        getString(COL_NAME, c),
                        getInt(COL_STATE, c),
                        getString(COL_CLOB, c)
                );
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }

        if(workflow != null) {
            c = db.query(TABLE_NAME, null, COL_PARENT_NAME+"=?", new String[]{name}, null, null, null);

            if (c.moveToFirst()) {
                workflow.childWorkflows = new ArrayList<>();
                do {
                    Workflow childWorkflow = new Workflow(
                            getString(COL_NAME, c),
                            getInt(COL_STATE, c),
                            getString(COL_CLOB, c)
                    );
                    workflow.childWorkflows.add(childWorkflow);
                } while (c.moveToNext());
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return workflow;
	}
}
