package com.logiforge.ballo.sync.model.db;

import com.logiforge.ballo.sync.model.api.EntityChanges;

import java.util.ArrayList;

public class JournalEntry {
	public Long id;
	public Long txnId;
	public Long operationNbr;
	public String entityId;
	public String className;
	public Integer operation;
	public Long version;
	public String parentClassName;
	public String parentId;
	public Long parentVersion;
	public String clobData;
	public byte[] blobData;
	public ArrayList<Long> absorbedEntries = new ArrayList<Long>();
	
	public JournalEntry(Long id, Long txnId, Long operationNbr, String entityId, String className, Integer operation, Long version,  
			String parentClassName, String parentId, Long parentVersion,
			String clobData, byte[] blobData) {
		this.id = id;
		this.txnId = txnId;
		this.operationNbr = operationNbr;
		this.entityId = entityId;
		this.className = className;
		this.operation = operation;
		this.version = version;
		this.parentClassName = parentClassName;
		this.parentId = parentId;
		this.parentVersion = parentVersion; 
		this.clobData = clobData;
		this.blobData = blobData;
	}
	
	public JournalEntry(Long id, Long txnId, Long operationNbr, String entityId, String className, Integer operation, Long version,  
			String parentClassName, String parentId, Long parentVersion) {
		this.id = id;
		this.txnId = txnId;
		this.operationNbr = operationNbr;
		this.entityId = entityId;
		this.className = className;
		this.operation = operation;
		this.version = version;
		this.parentClassName = parentClassName;
		this.parentId = parentId;
		this.parentVersion = parentVersion; 
	}
	
	public JournalEntry(Long txnId, Long operationNbr, SyncEntity entity, String parentClassName, String parentId, Long parentVersion, Integer operation,
			String clobData, byte[] blobData) {
		this.txnId = txnId;
		this.operationNbr = operationNbr;
		this.entityId = entity.id;
		this.className = entity.getClass().getSimpleName();
		this.operation = operation;
		this.version = entity.version;
		this.parentClassName = parentClassName;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.clobData = clobData;
		this.blobData = blobData;
	}
	
	public JournalEntry(Long txnId, Long operationNbr, SyncEntity entity, String parentClassName, String parentId, Long parentVersion, Integer operation) {
		this.txnId = txnId;
		this.operationNbr = operationNbr;
		this.entityId = entity.id;
		this.className = entity.getClass().getSimpleName();
		this.operation = operation;
		this.version = entity.version;
		this.parentClassName = parentClassName;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
	}
	
	public void absorb(JournalEntry thatEntry) {
		absorbedEntries.add(thatEntry.id);
		absorbedEntries.addAll(thatEntry.absorbedEntries);
	}

	public boolean isParentOf(JournalEntry thatEntry) {
		return thatEntry.parentId != null && this.entityId.equals(thatEntry.parentId);
	}

    public boolean isChildOf(JournalEntry thatEntry) {
        return this.parentId != null && this.parentId.equals(thatEntry.entityId);
    }

	public boolean sameAs(JournalEntry thatEntry) {
		return this.entityId.equals(thatEntry.entityId);
	}

    public boolean isSiblingOf(JournalEntry thatEntry) {
        return this.parentId != null && thatEntry.parentId != null
               && this.parentId.equals(thatEntry.parentId);
    }

    public boolean isOperationOn(JournalEntry thatEntry) {
	    return this.entityId.equals(thatEntry.entityId);
    }

	public boolean isNew() {
		return this.operation == EntityChanges.OP_NEW;
	}

	public boolean isDelete() {
		return this.operation == EntityChanges.OP_DELETE;
	}

	public boolean isUpdate() {
		return this.operation == EntityChanges.OP_UPDATE;
	}
	
	public String toConciseString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(" ").append(txnId).append(" ").append(operationNbr).append(" ");
		switch(operation){
		case 0:
			sb.append("N ");
			break;
		case 1:
			sb.append("D ");
			break;
		case 2:
			sb.append("U ");
			break;
		default:
			sb.append("? ");
			break;
		}
		sb.append(className).append("(").append(entityId.substring(0, 4)).append("/").append(version).append(")").append(" ");

		if(parentClassName != null) {
			sb.append(parentClassName).append("(").append(parentId.substring(0, 4)).append("/").append(parentVersion).append(")");
		}
		if(absorbedEntries != null && absorbedEntries.size() > 0){
			sb.append("[");
			for(int i=0; i<absorbedEntries.size(); i++) {
				if(i>0) {
					sb.append(", ");
				}
				sb.append(absorbedEntries.get(i));
			}
			sb.append("]");
		}
		return sb.toString();
	}
}
