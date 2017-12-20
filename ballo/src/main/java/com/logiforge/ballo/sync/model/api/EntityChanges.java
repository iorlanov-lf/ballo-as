package com.logiforge.ballo.sync.model.api;

import com.logiforge.ballo.sync.model.db.SyncEntity;

import java.util.Map;

public class EntityChanges {
	public static final int OP_NONE = -1;
	public static final int OP_NEW = 0;
	public static final int OP_DELETE = 1;
	public static final int OP_UPDATE = 2;
	public static final int OP_REPLACE = 3;
	public static final int OP_REPLACE_PARENT = 4;
	public static final int OP_DELETE_PARENT = 5;
	public static final int OP_NEW_SLICE = 6;
	
	public int operation = OP_NONE;	
	public Long operationNbr = null;
	
	public String className = null;
	public String entityId = null;	
	public Long version = null;
	
	public String parentClassName = null;
	public String parentId = null;
	public Long parentVersion = null;
	
	public SyncEntity entity = null;
	public byte[] packedEntity = null; 

	public EntityChanges() {
	}
	
	public EntityChanges(int operation, Long operationNbr, InventoryItem item) {
		this.operation = operation;
		this.operationNbr = operationNbr;
		this.className = item.className;
		this.entityId = item.entityId;
		this.version = item.version;
		this.parentClassName = item.parentClassName;
		this.parentId = item.parentId;
		this.parentVersion = item.parentVersion;
	}
	
	public String getType() {
		if(className != null) {
			return className;
		} else {
			return entity.getClass().getSimpleName();
		}
	}
	
	public String toConciseString() {
		StringBuilder sb = new StringBuilder();
		sb.append(operationNbr).append(" ");
		switch(operation){
		case 0:
			sb.append("N  ");
			break;
		case 1:
			sb.append("D  ");
			break;
		case 2:
			sb.append("U  ");
			break;
		case 3:
			sb.append("R  ");
			break;
		case 4:
			sb.append("RP ");
			break;
		case 5:
			sb.append("DP ");
			break;
		default:
			sb.append("?  ");
			break;
		}
		sb.append(className).append("(").append(entityId.substring(0, 4)).append("/").append(version).append(")").append(" ");
		if(parentClassName != null) {
			sb.append(parentClassName).append("(").append(parentId.substring(0, 4)).append("/").append(parentVersion).append(")");
		}
		return sb.toString();
	}
}
