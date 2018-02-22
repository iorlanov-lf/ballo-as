package com.logiforge.ballo.sync.dao;

import com.logiforge.ballo.dao.Dao;
import com.logiforge.ballo.sync.model.db.AppSubscription;

import java.util.List;

/**
 * Created by iorlanov on 1/13/18.
 */

public interface AppSubscriptionDao extends Dao {
    void createAppSubscription(AppSubscription appSubscription) throws Exception;
    List<AppSubscription> getAllSubscriptions() throws Exception;
    void deleteAll() throws Exception;
    void updateVisibleVersion(String entityId, Long version) throws Exception;
}
