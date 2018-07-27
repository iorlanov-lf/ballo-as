package com.logiforge.balloapp.facade;

import android.util.Log;

import com.logiforge.ballo.Ballo;
import com.logiforge.ballo.dao.DbTransaction;
import com.logiforge.balloapp.dao.PostalCodeDao;
import com.logiforge.balloapp.dao.PostalCodeFacilitiesDao;
import com.logiforge.balloapp.dao.UserPostalCodeDao;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;
import com.logiforge.balloapp.model.db.UserPostalCode;
import com.logiforge.balloapp.protocol.DataAction;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by iorlanov on 4/9/18.
 */

public class RegistrationFacade {
    private static final String TAG = RegistrationFacade.class.getSimpleName();
    private static final float MILES_PER_DEGREE_OF_LAT = 69;
    private static final float EARTH_RADIUS_IN_MILES = 3963.1675f;

    public boolean persistUserPostalCodes(String userName, List<String> postalCodes) {
        UserPostalCodeDao userPostalCodeDao = Ballo.db().getSyncDao(UserPostalCode.class);

        DbTransaction dbTransaction = null;
        try {
            dbTransaction = Ballo.db().beginTxn(DataAction.ACTN_ADD_USER_POSTAL_CODES, true);
            for (String postalCode : postalCodes) {
                UserPostalCode upc = new UserPostalCode(userName, postalCode);
                userPostalCodeDao.add(upc);
            }

            Ballo.db().commitTxn(dbTransaction);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage()==null?e.getClass().getSimpleName():e.getMessage());
            return false;
        } finally {
            Ballo.db().endTxn(dbTransaction);
        }
    }

    public boolean subscribeForFacilities(List<String> postalCodes, float distanceToNeighbour) {

        try {
            PostalCodeDao pcodeDao = Ballo.db().getSyncDao(PostalCode.class);
            PostalCodeFacilitiesDao pcodeFacilitiesDao = Ballo.db().getSyncDao(PostalCodeFacilities.class);

            // find all neighbours
            Set<PostalCode> uniqueNighbours = new TreeSet<>();
            for(String postalCode : postalCodes) {
                PostalCode pcode = pcodeDao.find(postalCode);

                float lat1 = pcode.getLatitude() + distanceToNeighbour/MILES_PER_DEGREE_OF_LAT;
                float lat2 = pcode.getLatitude() - distanceToNeighbour/MILES_PER_DEGREE_OF_LAT;

                float milesPerDegreeOfLon =
                        (float)(Math.cos(Math.toRadians(pcode.getLongitude())) * Math.PI * EARTH_RADIUS_IN_MILES / 180);
                float lon1 = pcode.getLongitude() - distanceToNeighbour / milesPerDegreeOfLon;
                float lon2 = pcode.getLongitude() - distanceToNeighbour / milesPerDegreeOfLon;

                List<PostalCode> pcodeList = pcodeDao.findNeighbours(lat1, lon1, lat2, lon2);

                uniqueNighbours.addAll(pcodeList);
            }

            Iterator<PostalCode> uniqueNeighbourIt = uniqueNighbours.iterator();
            while(uniqueNeighbourIt.hasNext()) {
                PostalCode pcode = uniqueNeighbourIt.next();

                if(pcodeFacilitiesDao.find(pcode.id) == null) {

                }
            }

        } catch(Exception e) {

        }

        return true;
    }
}
