package com.logiforge.ballo.sync.model.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iorlanov on 4/29/18.
 */

public class JournalTransaction {
    public Long txnId;
    public Long action;
    public Boolean isAggregatable;
    public Boolean isAggregated;
    public List<JournalEntry> entries = new ArrayList<>();

    public JournalTransaction(Long txnId, Long action, Boolean isAggregatable) {
        this.txnId = txnId;
        this.action = action;
        this.isAggregatable = isAggregatable;
        this.isAggregated = !isAggregatable;
    }

    public JournalTransaction(Long txnId, Long action, Boolean isAggregatable, Boolean isAggregated) {
        this.txnId = txnId;
        this.action = action;
        this.isAggregatable = isAggregatable;
        this.isAggregated = isAggregated;
    }
}
