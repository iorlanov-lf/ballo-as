package com.logiforge.ballo.sync.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;

public interface SyncEntityDao extends Dao {
	int SYNC_STATE_TRANSIENT = -3;
	int SYNC_STATE_IGNORED = -2;
	int SYNC_STATE_ADDED = -1;
	int SYNC_STATE_NORMAL = 0;
	int SYNC_STATE_MODIFIED = 1;
	int SYNC_STATE_OUTDATED = 2;
	int SYNC_STATE_CONFLICT = 3;

	// single entity CRUD
	void add(SyncEntity entity);
	void update(String entityId, Long version, int syncState, Map<Integer, SyncEntity.ValuePair> changes);
	void delete(String id);

	// single entity queries
    <T extends SyncEntity> T find(String id);
	Long getVersion(String id);
	void updateVersion(String id, long version);
	void setSyncState(String id, int state);

	// all entities
	void deleteAll();
	<T extends SyncEntity> List<T> findAll();

	// inventory queries
	HashMap<String, InventoryItem> getInventory(String parentId, Long parentVersion);
	HashMap<String, InventoryItem> getInventory();
	InventoryItem getEntityAsInventory(String id);
	
	// other
	boolean exists(String id);
	int count();
}
