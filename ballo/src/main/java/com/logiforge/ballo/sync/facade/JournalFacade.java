package com.logiforge.ballo.sync.facade;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.dao.JournalTransactionDao;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;

import com.logiforge.ballo.sync.facade.AggregationPolicy.MergeActions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by iorlanov on 4/29/18.
 */

public class JournalFacade {
    AggregationPolicy aggregationPolicy = null;

    public JournalFacade() {
        aggregationPolicy = getAggregationPolicy();
    }

    public List<JournalTransaction> getJournalTransactions() throws Exception {

        List<JournalTransaction> rawTransactions = getRawJornalTransactios();
        InMemoryJournal inMemoryJournal = new InMemoryJournal(rawTransactions);
        agregateTransactions(inMemoryJournal);

        return inMemoryJournal.aggTransactions;
    }

    protected List<JournalTransaction> getRawJornalTransactios() {
        JournalTransactionDao journalTransactionDao = Ballo.db().getDao(JournalTransaction.class);
        JournalEntryDao journalEntryDao = Ballo.db().getDao(JournalEntry.class);

        List<JournalTransaction> journalTransactions = journalTransactionDao.getAll();
        HashMap<Long, JournalTransaction> jTxnMap = new HashMap<>();
        for(JournalTransaction jTxn : journalTransactions) {
            jTxnMap.put(jTxn.txnId, jTxn);
        }

        List<JournalEntry> journalEntries = journalEntryDao.getEntries();
        for(JournalEntry jEntry : journalEntries) {
            JournalTransaction jTxn = jTxnMap.get(jEntry.txnId);
            jTxn.entries.add(jEntry);
        }

        return journalTransactions;
    }

    protected void agregateTransactions(InMemoryJournal inMemoryJournal) throws Exception {
        MergeActions mergeActions = new MergeActions();

        for (JournalTransaction rawTxn : inMemoryJournal.rawTransactions) {
            if (rawTxn.isAggregatable) {
                for (JournalTransaction aggTxn : inMemoryJournal.aggTransactions) {
                    if (aggTxn.isAggregatable) {
                        mergeActions.add(aggregationPolicy.merge(aggTxn, rawTxn));
                    }
                }
            }

            if(rawTxn.entries.size() > 0) {
                inMemoryJournal.aggTransactions.add(rawTxn);
            }
        }

        // persist the changes
        JournalTransactionDao journalTransactionDao = Ballo.db().getDao(JournalTransaction.class);
        JournalEntryDao journalEntryDao = Ballo.db().getDao(JournalEntry.class);

        for(JournalTransaction txn : mergeActions.emptyTransactions) {
            journalTransactionDao.delete(txn);
        }

        for(JournalEntry entry : mergeActions.absorbedEntries) {
            journalEntryDao.deleteById(entry.id);
        }

        for(JournalEntry entry : mergeActions.movedEntries) {
            journalEntryDao.updateTxnId(entry);
        }

        for(JournalEntry entry : mergeActions.absorbingEntries) {
            journalEntryDao.updateLOBs(entry);
        }

        for(JournalTransaction txn : inMemoryJournal.aggTransactions) {
            if(!txn.entries.isEmpty() && !txn.isAggregated) {
                journalTransactionDao.updateIsAggregated(txn, true);
            }
        }
    }

    protected AggregationPolicy getAggregationPolicy() {
        return new DefaultAggregationPolicy();
    }
}
