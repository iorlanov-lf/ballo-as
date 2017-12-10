package com.logiforge.ballo.sync.protocol.dao_facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.model.api.InventoryItem;
import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;
import java.util.UUID;

import com.logiforge.ballo.sync.dao.SyncDao;

/**
 * Created by iorlanov on 11/22/17.
 */

public abstract class DefaultSyncFacade implements SyncFacade {
    @Override
    public void syncAdd(SyncEntity entity) {
        entity.syncState = SyncDao.SYNC_STATE_NORMAL;
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.add(entity);
    }

    @Override
    public void syncUpdate(String entityId, Long version, Map<Integer, SyncEntity.ValuePair> changes) {
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.update(entityId, version, SyncDao.SYNC_STATE_NORMAL, changes);
    }

    @Override
    public void syncDelete(String id) {
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.delete(id);
    }

    @Override
    public void uiAdd(DbTransaction txn, SyncEntity entity) throws Exception {
        if(entity.id == null) {
            entity.id = UUID.randomUUID().toString();
        }
        entity.syncState = SyncDao.SYNC_STATE_ADDED;
        entity.version = 0L;
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        syncDao.add(entity);

        if(Ballo.authFacade().isCloudRegistered()) {
            boolean populateJournal = true;
            Long parentVersion = null;

            if(parentVersionMatters()) {
                SyncDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
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
    public void uiUpdate(DbTransaction txn, SyncEntity entity) throws Exception {
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());

        boolean populateJournal = true;
        Long parentVersion = null;

        if(myVersionMatters()) {
            InventoryItem parentAsInventory = syncDao.getEntityAsInventory(entity.id);
            // version logic here

            if(entity.syncState == SyncDao.SYNC_STATE_NORMAL) {
                entity.syncState = SyncDao.SYNC_STATE_MODIFIED;
            } else if(entity.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                entity.syncState = SyncDao.SYNC_STATE_CONFLICT;
            }

            if(entity.syncState == SyncDao.SYNC_STATE_CONFLICT ||
               entity.syncState == SyncDao.SYNC_STATE_IGNORED ||
               entity.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                populateJournal = false;
            }
        }

        syncDao.update(entity.id, entity.version, entity.syncState, entity.changes);

        if(Ballo.authFacade().isCloudRegistered()) {
            if(parentVersionMatters()) {
                SyncDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
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
    public void uiDelete(DbTransaction txn, String id) throws Exception {
        SyncDao syncDao = Ballo.db().getSyncDao(getEntityClassName());
        SyncEntity entity = syncDao.find(id);

        if(Ballo.authFacade().isCloudRegistered()) {
            boolean populateJournal = true;
            Long parentVersion = null;

            if(parentVersionMatters()) {
                SyncDao parentSyncDao = Ballo.db().getSyncDao(getSyncParentEntityClassName());
                InventoryItem parentAsInventory = parentSyncDao.getEntityAsInventory(entity.getSyncParentId());
                parentVersion = parentAsInventory.version;

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_NORMAL) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_MODIFIED);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_MODIFIED;
                } else if(parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                    parentSyncDao.setSyncState(entity.getSyncParentId(), SyncDao.SYNC_STATE_CONFLICT);
                    parentAsInventory.syncState = SyncDao.SYNC_STATE_CONFLICT;
                }

                if(parentAsInventory.syncState == SyncDao.SYNC_STATE_CONFLICT ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_IGNORED ||
                   parentAsInventory.syncState == SyncDao.SYNC_STATE_OUTDATED) {
                    populateJournal = false;
                }
            }

            if(populateJournal) {
                journalOnDelete(txn, entity, entity.getSyncParentId(), parentVersion);
            }
        }

        syncDao.delete(id);
        syncDao.deleteChildren(id);

        onUiDelete(entity);
    }

    protected void onUiAdd(SyncEntity entity) {

    }

    protected void onUiUpdate(SyncEntity entity) {

    }

    protected void onUiDelete(SyncEntity entity) {

    }

    private void journalOnCreated(DbTransaction txn, SyncEntity entity, String syncParentId, Long syncParentVersion) throws Exception {
        byte[] entityAsBytes = Ballo.syncProtocol().getSyncEntityConverter(this.getEntityClassName()).toBytes(entity);
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntryDao.class);
        journalDao.onEntityCreated(txn, entity, entityAsBytes, this.getSyncParentEntityClassName(), syncParentId, syncParentVersion);
    }

    private void journalOnUpdated(DbTransaction txn, SyncEntity entity, String syncParentId, Long syncParentVersion) throws Exception {
        byte[] entityAsBytes = Ballo.syncProtocol().getSyncEntityConverter(this.getEntityClassName()).toBytes(entity.changes);
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntryDao.class);
        journalDao.onEntityUpdated(txn, entity, entityAsBytes, this.getSyncParentEntityClassName(), syncParentId, syncParentVersion);
    }

    private void journalOnDelete(DbTransaction txn, SyncEntity entity, String parentId, Long parentVersion) {
        JournalEntryDao journalDao = Ballo.db().getDao(JournalEntryDao.class);
        journalDao.onEntityDeleted(txn, entity, this.getSyncParentEntityClassName(), parentId, parentVersion);
    }
}
