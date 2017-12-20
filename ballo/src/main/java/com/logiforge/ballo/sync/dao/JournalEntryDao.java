package com.logiforge.ballo.sync.dao;

import java.util.List;

import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.SyncEntity;

public interface JournalEntryDao extends Dao {

	void onEntityCreated(DbTransaction txn, SyncEntity entity, byte[] entityAsBytes, String parentClassName, String parentId, Long parentVersion);
	
	void onEntityDeleted(DbTransaction txn, SyncEntity entity, String parentClassName, String parentId, Long parentVersion);
	
	void onEntityUpdated(DbTransaction txn, SyncEntity entity, byte[] entityAsBytes, String parentClassName, String parentId, Long parentVersion);

	List<JournalEntry> getEntries();

	void delete(String entityId);
	
	void delete(String id, Integer updateCounter);
	
	void deleteByEntityOrParentId(String entityId);
	
	void deleteOrUpdateVersion(String id, Integer updateCounter, Long version);
	
	void deleteOrUpdateVersionAndOp(String id, Integer updateCounter, Long version);
	
	void deleteTxn(Long txnId);
	
	void deleteAll();
	
	void deleteByIds(List<Long> ids);
	
	void deleteById(Long id);
	
	String getClobData(Long id);
	
	byte[] getBlobData(Long id);
	
	boolean hasMoreUpdates(Long moreThanThisId, String entityId);
	
	void updateVersion(String entityId, Long version);
	
	void updateParentVersion(String entityId, Long version);
}
