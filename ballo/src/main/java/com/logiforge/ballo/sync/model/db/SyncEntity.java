package com.logiforge.ballo.sync.model.db;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public abstract class SyncEntity implements Comparable<SyncEntity>{
	
	public String id = null;
	public Long version = 0L; 
	public Integer syncState = null;

	public Map<Integer, ValuePair> changes;

    public SyncEntity() {
    }

	public SyncEntity(String id, Long version, Integer syncState) {
		this.id = id;
		this.version = version;
		this.syncState = syncState;
	}

	@Override
	public int compareTo(SyncEntity otherEntity) {
        if(id == null && otherEntity.id == null) {
            return 0;
        } else if(id == null && otherEntity.id != null) {
            return 1;
        } else if(id != null && otherEntity.id == null) {
            return -1;
        } else {
            return id.compareTo(otherEntity.id);
        }
    }

    public abstract String getSyncParentId();
    public abstract void applyChanges(Map<Integer, ValuePair> changes);

    protected void logPropertyChange(int fieldId, Object oldValue, Object newValue) {
        if(oldValue != newValue) {
            if(oldValue != null && newValue != null) {
                if(oldValue.equals(newValue)) {
                    return;
                }
            }

            if(changes == null) {
                changes = new HashMap<>();
            }

            ValuePair valuePair = changes.get(fieldId);
            if(valuePair == null) {
                valuePair = new ValuePair(oldValue, newValue);
                changes.put(fieldId, valuePair);
            } else {
                valuePair.newValue = newValue;
            }
        } else {
            return;
        }
    }

    public static class ValuePair {

        public ValuePair(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Object oldValue;
        public Object newValue;
    }
}
