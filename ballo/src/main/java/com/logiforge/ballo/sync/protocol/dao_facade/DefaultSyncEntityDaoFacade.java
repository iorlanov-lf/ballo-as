package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.logiforge.ballo.sync.dao.SyncEntityDao;

/**
 * Created by iorlanov on 11/22/17.
 */

public abstract class DefaultSyncEntityDaoFacade implements SyncEntityDaoFacade {
    abstract protected String getEntityClassName();
    abstract protected String getSyncParentEntityClassName();
    abstract protected boolean isSyncRootEntity();
    abstract protected boolean myVersionMatters();
    abstract protected boolean parentVersionMatters();

    abstract protected List<SyncEntity> getChildren(String id);
    abstract protected void deleteChildren(String id);


    protected void onUiAdd(SyncEntity entity) {
    }
    protected void onUiUpdate(SyncEntity entity) {
    }
    protected void onUiDelete(SyncEntity entity) {
    }

    @Override
    public void syncAdd(SyncEntity entity) {
        entity.syncState = SyncEntityDao.SYNC_STATE_NORMAL;
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.add(entity);
    }

    @Override
    public void syncUpdate(String entityId, Long version, Map<Integer, SyncEntity.ValuePair> changes) {
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.update(entityId, version, SyncEntityDao.SYNC_STATE_NORMAL, changes);
    }

    @Override
    public void syncDelete(String id) {
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.delete(id);
    }

    @Override
    public void add(DbTransaction txn, SyncEntity entity) throws Exception {
        if(entity.id == null) {
            entity.id = UUID.randomUUID().toString();
        }
        entity.syncState = SyncEntityDao.SYNC_STATE_ADDED;
        entity.version = 0L;
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.add(entity);

        if(Ballo.authFacade().isCloudRegistered()) {
            boolean populateJournal = true;
            Long parentVersion = null;

            if(parentVersionMatters()) {
                SyncEntityDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    populateJournal = false;
                }
            }

            if(populateJournal) {
                journalOnCreated(txn, entity, entity.getSyncParentId(), parentVersion);
            }
        }

        onUiAdd(entity);
    }

    @Override
    public void update(DbTransaction txn, SyncEntity entity) throws Exception {
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());

        boolean populateJournal = true;
        Long parentVersion = null;

        if(myVersionMatters()) {
            InventoryItem parentAsInventory = syncDao.getEntityAsInventory(entity.id);
            // version logic here

            if(entity.syncState == SyncEntityDao.SYNC_STATE_NORMAL) {
                entity.syncState = SyncEntityDao.SYNC_STATE_MODIFIED;
            } else if(entity.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                entity.syncState = SyncEntityDao.SYNC_STATE_CONFLICT;
            }

            if(entity.syncState == SyncEntityDao.SYNC_STATE_CONFLICT ||
               entity.syncState == SyncEntityDao.SYNC_STATE_IGNORED ||
               entity.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                populateJournal = false;
            }
        }

        syncDao.update(entity.id, entity.version, entity.syncState, entity.changes);

        if(Ballo.authFacade().isCloudRegistered()) {
            if(parentVersionMatters()) {
                SyncEntityDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    populateJournal = false;
                }
            }

            if(populateJournal) {
                journalOnUpdated(txn, entity, entity.getSyncParentId(), parentVersion);
            }
        }


        onUiUpdate(entity);
    }

    @Override
    public void delete(DbTransaction txn, String id) throws Exception {
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        SyncEntity entity = syncDao.find(id);

        if(Ballo.authFacade().isCloudRegistered()) {
            boolean populateJournal = true;
            Long parentVersion = null;

            if(parentVersionMatters()) {
                SyncEntityDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncEntityDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncEntityDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncEntityDao.SYNC_STATE_OUTDATED) {
                    populateJournal = false;
                }
            }

            if(populateJournal) {
                journalOnDelete(txn, entity, entity.getSyncParentId(), parentVersion);
            }
        }

        syncDao.delete(id);
        deleteChildren(id);

        onUiDelete(entity);
    }

    @Override
    public <T> T find(String id) throws Exception {
        SyncEntityDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        return (T) syncDao.find(id);
    }

    private void journalOnCreated(DbTransaction txn, SyncEntity entity, String syncParentId, Long syncParentVersion) throws Exception {
        byte[] entityAsBytes = Ballo.syncProtocol().getSyncEntityConverter(this.getEntityClassName()).toBytes(entity);
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
        journalDao.onEntityCreated(txn, entity, entityAsBytes, this.getSyncParentEntityClassName(), syncParentId, syncParentVersion);
    }

    private void journalOnUpdated(DbTransaction txn, SyncEntity entity, String syncParentId, Long syncParentVersion) throws Exception {
        byte[] entityAsBytes = Ballo.syncProtocol().getSyncEntityConverter(this.getEntityClassName()).changesToBytes(entity.changes);
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
        journalDao.onEntityUpdated(txn, entity, entityAsBytes, this.getSyncParentEntityClassName(), syncParentId, syncParentVersion);
    }

    private void journalOnDelete(DbTransaction txn, SyncEntity entity, String parentId, Long parentVersion) {
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
        journalDao.onEntityDeleted(txn, entity, this.getSyncParentEntityClassName(), parentId, parentVersion);
    }
}
