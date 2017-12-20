package com.logiforge.balloapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.ballo.sync.dao.JournalEntryDao;
import com.logiforge.ballo.sync.dao.SyncEntityDao;
import com.logiforge.ballo.sync.model.db.JournalEntry;
import com.logiforge.ballo.sync.model.db.SyncEntity;
import com.logiforge.ballo.sync.protocol.conversion.SyncEntityConverter;
import com.logiforge.ballo.sync.protocol.dao_facade.DefaultTransactionFacade;
import com.logiforge.ballo.sync.protocol.dao_facade.SyncEntityDaoFacade;
import com.logiforge.ballo.sync.protocol.dao_facade.TransactionFacade;
import com.logiforge.balloapp.model.db.Facility;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by iorlanov on 12/13/17.
 */

public class FacilityTest {
    @BeforeClass
    public static void classStartUp() {
        try {
            Context appContext = InstrumentationRegistry.getTargetContext();
            if(Ballo.db() == null) {
                BalloInitializer.init(appContext);
                cleanup();
                BalloInitializer.emulateUserRegistration();
            }
        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void createFacilityTest() {
        try {
            cleanup();

            Facility createdFacility = createFacility();
            assertNotNull(createdFacility);

            SyncEntityDaoFacade facilityDaoFacade = Ballo.syncProtocol().getSyncEntityDaoFacade(Facility.class);
            Facility foundFacility = facilityDaoFacade.find(createdFacility.id);
            assertNotNull(foundFacility);

            JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
            List<JournalEntry> journalEntries = journalDao.getEntries();
            assertEquals(1, journalEntries.size());

            JournalEntry journalEntry = journalEntries.get(0);
            byte[] blobData = journalDao.getBlobData(journalEntry.id);
            SyncEntityConverter facilityConverter = Ballo.syncProtocol().getSyncEntityConverter(Facility.class);
            Facility unpackedFacility = (Facility) facilityConverter.fromBytes(blobData);
            assertNotNull(unpackedFacility);

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void updateFacilityTest() {
        String NEW_STREET_ADDRESS = "4403 Whitecap Lane";
        try {
            cleanup();

            Facility createdFacility = createFacility();
            assertNotNull(createdFacility);

            createdFacility.setStreetAddress(NEW_STREET_ADDRESS);
            SyncEntityDaoFacade facilityDaoFacade = Ballo.syncProtocol().getSyncEntityDaoFacade(Facility.class);

            TransactionFacade txnFacade = new DefaultTransactionFacade();
            DbTransaction txn = txnFacade.beginTxn(20L);
            try {
                facilityDaoFacade.update(txn, createdFacility);
                txnFacade.commitTxn(txn);
            } catch(Exception ex) {
                fail(ex.getMessage());
            } finally {
                txnFacade.endTxn(txn);
            }

            Facility foundFacility = facilityDaoFacade.find(createdFacility.id);
            assertNotNull(foundFacility);
            assertEquals(foundFacility.getStreetAddress(), NEW_STREET_ADDRESS);

            JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
            List<JournalEntry> journalEntries = journalDao.getEntries();
            assertEquals(2, journalEntries.size());

            JournalEntry journalEntry = journalEntries.get(1);
            byte[] blobData = journalDao.getBlobData(journalEntry.id);
            SyncEntityConverter facilityConverter = Ballo.syncProtocol().getSyncEntityConverter(Facility.class);
            Map<Integer, SyncEntity.ValuePair> unpackedChanges = facilityConverter.changesFromBytes(blobData);
            assertNotNull(unpackedChanges);

            Facility emptyFacility = new Facility();
            emptyFacility.applyChanges(unpackedChanges);
            assertEquals(emptyFacility.getStreetAddress(), NEW_STREET_ADDRESS);

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteFacilityTest() {
        try {
            cleanup();

            Facility createdFacility = createFacility();
            assertNotNull(createdFacility);

            SyncEntityDaoFacade facilityDaoFacade = Ballo.syncProtocol().getSyncEntityDaoFacade(Facility.class);
            Facility foundFacility = facilityDaoFacade.find(createdFacility.id);
            assertNotNull(foundFacility);

            TransactionFacade txnFacade = new DefaultTransactionFacade();
            DbTransaction txn = txnFacade.beginTxn(30L);
            try {
                facilityDaoFacade.delete(txn, createdFacility.id);
                txnFacade.commitTxn(txn);
            } catch(Exception ex) {
                fail(ex.getMessage());
            } finally {
                txnFacade.endTxn(txn);
            }

            foundFacility = facilityDaoFacade.find(createdFacility.id);
            assertNull(foundFacility);

            JournalEntryDao journalDao = Ballo.db().getDao(JournalEntry.class);
            List<JournalEntry> journalEntries = journalDao.getEntries();
            assertEquals(2, journalEntries.size());

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @NonNull
    private Facility createFacility() throws Exception {
        TransactionFacade txnFacade = new DefaultTransactionFacade();
        DbTransaction txn = txnFacade.beginTxn(10L);

        Facility facilityToAdd = null;
        try {
            facilityToAdd = new Facility(
                    "Jones Bridge Swim & Racket Club", "4403 Whitecap Ln", "Norcross", "GA", "30092",
                    "From GA 141, turn onto East Jones Bridge Road. Turn right to Whitewater Dr. Turn left to Whitewater Ln.", "iorlanov");

            SyncEntityDaoFacade facilityDaoFacade = Ballo.syncProtocol().getSyncEntityDaoFacade(Facility.class);
            facilityDaoFacade.add(txn, facilityToAdd);


            txnFacade.commitTxn(txn);
        } catch(Exception ex) {
            fail(ex.getMessage());
        } finally {
            txnFacade.endTxn(txn);
        }
        return facilityToAdd;
    }

    static private void cleanup() {
        SyncEntityDao facilityDao = Ballo.db().getSyncDao(Facility.class);
        facilityDao.deleteAll();

        JournalEntryDao journalEntryDao = Ballo.db().getDao(JournalEntry.class);
        journalEntryDao.deleteAll();
    }
}
