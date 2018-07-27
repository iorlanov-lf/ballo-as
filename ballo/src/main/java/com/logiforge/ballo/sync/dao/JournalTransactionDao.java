package com.logiforge.ballo.sync.dao;

import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.List;

public interface JournalTransactionDao extends Dao {

	void add(JournalTransaction journalTransaction);
	void updateIsAggregated(JournalTransaction journalTransaction, Boolean isAggregated);
	void delete(JournalTransaction journalTransaction);
	List<JournalTransaction> getAll();
}
