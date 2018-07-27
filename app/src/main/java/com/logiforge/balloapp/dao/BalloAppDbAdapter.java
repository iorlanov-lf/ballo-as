package com.logiforge.balloapp.dao;

import android.database.sqlite.SQLiteDatabase;

import com.logiforge.ballo.dao.sqlite.SqliteDbAdapter;
import com.logiforge.balloapp.model.db.Facility;
import com.logiforge.balloapp.model.db.PostalCode;
import com.logiforge.balloapp.model.db.PostalCodeFacilities;
import com.logiforge.balloapp.model.db.PostalCodes;
import com.logiforge.balloapp.model.db.UserPostalCode;
import com.logiforge.balloapp.model.db.UserPostalCodes;

/**
 * Created by iorlanov on 12/14/17.
 */

public class BalloAppDbAdapter extends SqliteDbAdapter {
    public BalloAppDbAdapter(SQLiteDatabase db)  {
        super(db);
    }

    public void init() throws Exception {
        super.init();

        FacilityDao facilityDao = new FacilityDao();
        facilityDao.init();
        syncDaos.put(Facility.class.getSimpleName(), facilityDao);

        PostalCodeFacilitiesDao postalCodeFacilitiesDao = new PostalCodeFacilitiesDao();
        postalCodeFacilitiesDao.init();
        syncDaos.put(PostalCodeFacilities.class.getSimpleName(), postalCodeFacilitiesDao);

        PostalCodeDao postalCodeDao = new PostalCodeDao();
        postalCodeDao.init();
        syncDaos.put(PostalCode.class.getSimpleName(), postalCodeDao);

        PostalCodesDao postalCodesDao = new PostalCodesDao();
        postalCodesDao.init();
        syncDaos.put(PostalCodes.class.getSimpleName(), postalCodesDao);

        UserPostalCodeDao userPostalCodeDao = new UserPostalCodeDao();
        userPostalCodeDao.init();
        syncDaos.put(UserPostalCode.class.getSimpleName(), userPostalCodeDao);

        UserPostalCodesDao userPostalCodesDao = new UserPostalCodesDao();
        userPostalCodesDao.init();
        syncDaos.put(UserPostalCodes.class.getSimpleName(), userPostalCodesDao);
    }
}
