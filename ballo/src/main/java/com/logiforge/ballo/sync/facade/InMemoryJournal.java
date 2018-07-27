package com.logiforge.ballo.sync.facade;

import android.util.Log;

import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.JournalTransaction;

import java.util.ArrayList;
import java.util.List;

public class InMemoryJournal {
	public List<JournalTransaction> rawTransactions = null;
	public List<JournalTransaction> aggTransactions = null;

	public InMemoryJournal(List<JournalTransaction> rawTransactions) {
		this.rawTransactions = rawTransactions;
		this.aggTransactions = new ArrayList<JournalTransaction>();
	}
	
	public void logRawJournal() {
		StringBuilder sb = new StringBuilder();
		int entryCounter = 0;
		for(JournalTransaction txn : rawTransactions) {
			for(JournalEntry rawEntry : txn.entries) {
				sb.append(rawEntry.toConciseString()).append("\n");
				entryCounter++;
			}
		}
		
		if(sb.toString().length() == 0) {
			Log.d("RAW_JOURNAL", "No entries");
		} else {
			Log.i("RAW_JOURNAL", rawTransactions.size() + " transactions / " + entryCounter + " operations" );
			Log.d("RAW_JOURNAL", sb.toString());
		}
	}
	
	public void logAggJournal() {
		StringBuilder sb = new StringBuilder();
		int entryCounter = 0;
		for(JournalTransaction txn : aggTransactions) {
			for(JournalEntry rawEntry : txn.entries) {
				sb.append(rawEntry.toConciseString()).append("\n");
				entryCounter++;
			}
		}
		
		if(sb.toString().length() == 0) {
			Log.d("AGG_JOURNAL", "No entries");
		} else {
			Log.i("RAW_JOURNAL", aggTransactions.size() + " transactions / " + entryCounter + " operations" );
			Log.d("AGG_JOURNAL", sb.toString());
		}
	}
}