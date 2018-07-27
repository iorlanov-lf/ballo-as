package com.logiforge.ballo.sync.facade;

import android.util.Pair;

import com.logiforge.ballo.sync.model.api.EntityChanges;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.ConverterFactory;
import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultAggregationPolicy implements AggregationPolicy {


	public MergeActions merge1(JournalTransaction aggTxn, JournalTransaction rawTxn) throws Exception {
	    // find related entries
        List<Pair<JournalEntry, JournalEntry>> relatedPairs = new ArrayList<Pair<JournalEntry, JournalEntry>>();

        for(JournalEntry rawEntry : rawTxn.entries) {
            JournalEntry aggEntry = findRelated(aggTxn, rawEntry);
            if(aggEntry != null)
                relatedPairs.add(new Pair<JournalEntry, JournalEntry>(aggEntry, rawEntry));
        }

        if(relatedPairs != null) {

            MergeActions mergeActions = new MergeActions();

            for(Pair<JournalEntry, JournalEntry> pair  : relatedPairs) {
                JournalEntry aggEntry = pair.first;
                JournalEntry rawEntry = pair.second;

                switch (rawEntry.operation) {
                    case EntityChanges.OP_NEW:
                    case EntityChanges.OP_DELETE:
                        rawEntry.txnId = aggTxn.txnId;
                        aggTxn.entries.add(rawEntry);
                        break;

                    case EntityChanges.OP_UPDATE:
                        if (aggEntry.operation == EntityChanges.OP_NEW) {
                            if (aggEntry.entityId.equals(rawEntry.entityId)) {
                                // update of the newly created entity
                                merge(aggEntry, rawEntry, rawTxn, mergeActions);
                            } else {
                                // that entity must be an update of the parent
                                move(rawEntry, aggTxn, rawTxn, mergeActions);
                            }
                        } else if (aggEntry.operation == EntityChanges.OP_DELETE) {
                            rawEntry.txnId = aggTxn.txnId;
                            aggTxn.entries.add(rawEntry);
                        } else {
                            merge(aggEntry, rawEntry, rawTxn, mergeActions);
                        }
                        break;
                }
            }

            return mergeActions;
		} else {
            return null;
        }
	}

    @Override
    public MergeActions merge(JournalTransaction aggTxn, JournalTransaction rawTxn) throws Exception {
        MergeActions mergeActions = new MergeActions();

        for(JournalEntry rawEntry : rawTxn.entries) {
            switch (rawEntry.operation) {
                case EntityChanges.OP_NEW:
                    for(JournalEntry aggEntry : aggTxn.entries) {
                        if(aggEntry.isParentOf(rawEntry) || aggEntry.isSiblingOf(rawEntry)) {
                            move(rawEntry, aggTxn, rawTxn, mergeActions);
                        }
                    }
                    break;

                case EntityChanges.OP_UPDATE:
                    for(JournalEntry aggEntry : aggTxn.entries) {
                        if(aggEntry.isParentOf(rawEntry) || aggEntry.isSiblingOf(rawEntry) || aggEntry.isChildOf(rawEntry)) {
                            move(rawEntry, aggTxn, rawTxn, mergeActions);
                        } else if(aggEntry.sameAs(rawEntry)) {
                            merge(aggEntry, rawEntry, rawTxn, mergeActions);
                        }
                    }
                    break;

                case EntityChanges.OP_DELETE:
                    for(JournalEntry aggEntry : aggTxn.entries) {
                        if(aggEntry.sameAs(rawEntry) || aggEntry.isChildOf(rawEntry)) {
                            discard(aggEntry, aggTxn, mergeActions);
                        } else if(aggEntry.isParentOf(rawEntry)) {
                            move(rawEntry, aggTxn, rawTxn, mergeActions);
                        }
                    }
                    break;
            }
        }

        return mergeActions;
    }

    protected void discard(JournalEntry aggEntry, JournalTransaction aggTxn, MergeActions mergeActions) {
	    mergeActions.absorbedEntries.add(aggEntry);
	    aggTxn.entries.remove(aggEntry);
        if(aggTxn.entries.isEmpty()) {
            mergeActions.emptyTransactions.add(aggTxn);
        }
    }

	protected void merge(JournalEntry aggEntry, JournalEntry rawEntry, JournalTransaction rawTxn, MergeActions mergeActions) throws IOException {
		aggEntry.absorb(rawEntry);

        SyncEntityConverter entityConverter = ConverterFactory.getSyncEntityConverter(aggEntry.className);
        Map<Integer, SyncEntity.ValuePair> changes1 =  entityConverter.changesFromBytes(aggEntry.blobData);
        Map<Integer, SyncEntity.ValuePair> changes2 =  entityConverter.changesFromBytes(rawEntry.blobData);

        for(Map.Entry<Integer, SyncEntity.ValuePair> attributeChange : changes2.entrySet()) {
            SyncEntity.ValuePair vp1 = changes1.get(attributeChange.getKey());
            SyncEntity.ValuePair vp2 = attributeChange.getValue();
            if(vp1 != null) {
                vp2.oldValue = vp1.oldValue;
            }
            changes1.put(attributeChange.getKey(), attributeChange.getValue());
        }

        aggEntry.blobData = entityConverter.changesToBytes(changes1);
        rawTxn.entries.remove(rawEntry);

		mergeActions.absorbingEntries.add(aggEntry);
        mergeActions.absorbedEntries.add(rawEntry);
        if(rawTxn.entries.isEmpty()) {
            mergeActions.emptyTransactions.add(rawTxn);
        }
    }

    protected void move(JournalEntry rawEntry, JournalTransaction aggTxn, JournalTransaction rawTxn, MergeActions mergeActions) {
        rawEntry.txnId = aggTxn.txnId;
        aggTxn.entries.add(rawEntry);
        rawTxn.entries.remove(rawEntry);

        mergeActions.movedEntries.add(rawEntry);
        if(rawTxn.entries.isEmpty()) {
            mergeActions.emptyTransactions.add(rawTxn);
        }
    }
	
	protected JournalEntry findRelated(JournalTransaction aggTxn, JournalEntry rawEntry) {
        for(JournalEntry aggEntry : aggTxn.entries) {
            if(aggEntry.isOperationOn(rawEntry)) {
                return aggEntry;
            }

            if(aggEntry.isSiblingOf(rawEntry)) {
                if((aggEntry.isNew() || aggEntry.isDelete()) &&
                   (rawEntry.isNew() || rawEntry.isDelete())) {
                    return aggEntry;
                }
            }

            if(aggEntry.isParentOf(rawEntry)) {
                if(rawEntry.isNew() || rawEntry.isDelete()) {
                    return aggEntry;
                }
            }

            if(rawEntry.isParentOf(aggEntry)) {
                if(aggEntry.isNew() || aggEntry.isDelete()) {
                    return aggEntry;
                }

                if(rawEntry.isDelete()) {
                    return aggEntry;
                }
            }
        }
		
		return null;
	}
}
