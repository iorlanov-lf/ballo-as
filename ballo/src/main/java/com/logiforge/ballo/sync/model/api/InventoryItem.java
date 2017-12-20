package com.logiforge.ballo.sync.model.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryItem implements Comparable<InventoryItem> {
	public String entityId = null;
	public String className = null;
	public Long version = null; 	
	public Integer syncState = null;

	public String parentId = null;
	public String parentClassName = null;
	public Long parentVersion = null;
	
	public HashMap<String, InventoryItem> children = null; 

	public InventoryItem() {
		
	}
	
	public InventoryItem(
			String entityId, String className, Long version, Integer syncState,
			String parentId, String parentClassName, Long parentVersion) {
		this.entityId = entityId;
		this.className = className;
		this.version = version;
		this.syncState = syncState;

		this.parentId = parentId;
		this.parentClassName = parentClassName;
		this.parentVersion = parentVersion;
	}
	
	public void addChildren(List<InventoryItem> childrenToAdd) {
		if(childrenToAdd != null && childrenToAdd.size() > 0) {
			if(children == null) {
				children = new HashMap<String, InventoryItem>();
			}
			
			for(InventoryItem entity : childrenToAdd) {
				children.put(entity.entityId, entity);
			}
		}
	}
	
	public void addChildren(HashMap<String, InventoryItem> childrenToAdd) {
		if(childrenToAdd != null && childrenToAdd.size() > 0) {
			if(children == null) {
				children = new HashMap<String, InventoryItem>();
			}
			
			children.putAll(childrenToAdd);
		}
	}
	
	@Override
	public boolean equals(Object another) {
		return this.entityId.equals(((InventoryItem)another).entityId);
	}

	@Override
	public int compareTo(InventoryItem another) {
		return this.entityId.compareTo(another.entityId);
	}
	
	public String toString(int offset) {
		StringBuilder sb = new StringBuilder();
		if(offset > 0) {
			char[] offsetArray = new char[offset];
			Arrays.fill(offsetArray, ' ');
			sb.append(offsetArray);
		}
		sb.append(className).append("/").append(entityId.subSequence(0, 4)).append("(").append(version).append(")\n");
		
		if(children != null) {
			for(InventoryItem child : children.values()) {
				sb.append(child.toString(offset+2));
			}
		}
		return sb.toString();
	}
}