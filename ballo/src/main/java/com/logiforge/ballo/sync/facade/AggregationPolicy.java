package com.logiforge.ballo.sync.facade;

import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;

import java.util.ArrayList;
import java.util.List;

public interface AggregationPolicy {
    MergeActions merge(JournalTransaction aggTxn, JournalTransaction rawTxn) throws Exception;

    class MergeActions {
        public List<JournalTransaction> emptyTransactions = new ArrayList<>();
        public List<JournalEntry> movedEntries = new ArrayList<>();
        public List<JournalEntry> absorbedEntries = new ArrayList<>();
        public List<JournalEntry> absorbingEntries = new ArrayList<>();

        public void add(MergeActions mergeActions) {
            this.emptyTransactions.addAll(mergeActions.emptyTransactions);
            this.movedEntries.addAll(mergeActions.movedEntries);
            this.absorbedEntries.addAll(mergeActions.absorbedEntries);
            this.absorbingEntries.addAll(mergeActions.absorbingEntries);
        }
    }
}
