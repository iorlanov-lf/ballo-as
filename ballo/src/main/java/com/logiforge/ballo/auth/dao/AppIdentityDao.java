package com.logiforge.ballo.auth.dao;

import com.logiforge.ballo.auth.model.db.AppIdentity;
import com.logiforge.ballo.dao.Dao;

public interface AppIdentityDao extends Dao {
    void init();
	
    void setAppId(String appId);
	
    AppIdentity getAppIdentity();
	
    void updateAuthenticationData(String userName, String email, String displayName, String accessToken, String refreshToken);
	
    void updateAuthToken(String authToken);
}
