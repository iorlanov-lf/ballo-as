package com.logiforge.ballo.auth;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.logiforge.ballo.BalloPersistentState;
import com.logiforge.ballo.api.Api;
import com.logiforge.ballo.api.ApiCallBack;
import com.logiforge.ballo.api.ApiObjectFactory;
import com.logiforge.ballo.auth.model.api.UserAuthResult;
import com.logiforge.ballo.model.api.LogContext;
import com.logiforge.ballo.model.db.SyncLog;
import com.logiforge.ballo.net.HttpAdaptor;
import com.logiforge.ballo.net.JsonWebCall;
import com.logiforge.ballo.net.PostRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AuthApi extends Api {
    private static final String OP_REGISTER_APP = "register_app";

	private static final String OP_REGISTER = "register";

	private final static String TAG = AuthApi.class.getSimpleName();
	
	public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PAR_OP = "op";

    private AuthParams authParams;
	private ApiCallBack callBack;
    boolean callAsync = false;
	
	public AuthApi(Context context, LogContext logContext, ApiObjectFactory apiFactory, AuthParams authParams, boolean callAsync, ApiCallBack callBack) {
		super(context, logContext, apiFactory);
		this.callBack = callBack;
		this.authParams = authParams;
        this.callAsync = callAsync;
	}

	@SuppressWarnings("unchecked")
	public AuthApiTaskResult registerUser(String userName, String email, String displayName, String password, String deviceId, String appId) {
		PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER), 1);

		req.addStringPart("mode", "lf_id");
		req.addStringPart("op", OP_REGISTER);
		req.addStringPart("userName", userName);
		req.addStringPart("displayName", displayName);
		req.addStringPart("email", email);
		req.addStringPart("password", password);
		req.addStringPart("deviceId", deviceId);
		req.addStringPart("appId", appId);
	    
	    AuthApiTask call = new AuthApiTask(getHttpAdaptor(), gson, context, callBack, logContext, authParams);

        if(callAsync) {
            call.execute(req);
            return null;
        } else {
            AuthApiTaskResult res = call.doInBackground(req);
            if(callBack != null) {
                callBack.onPostExecute(res);
            }
            return res;
        }
	}
	
	@SuppressWarnings("unchecked")
	public AuthApiTaskResult registerApp(String userName, String password, String deviceId, String appId) {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "lf_id");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("userName", userName);
        req.addStringPart("password", password);
        req.addStringPart("deviceId", deviceId);
        req.addStringPart("appId", appId);

	    AuthApiTask call = new AuthApiTask(getHttpAdaptor(), gson, context, callBack, logContext, authParams);

        if(callAsync) {
            call.execute(req);
            return null;
        } else {
            AuthApiTaskResult res = call.doInBackground(req);
            if(callBack != null) {
                callBack.onPostExecute(res);
            }
            return res;
        }
	}
	
	@SuppressWarnings("unchecked")
	public void registerAppWithFacebook(String facebookId, String facebookEmail, String facebookDisplayName, String facebookAT, String deviceId, String appId) {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "facebook");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("facebookId", facebookId);
        req.addStringPart("facebookEmail", facebookEmail);
        req.addStringPart("facebookDisplayName", facebookDisplayName);
        req.addStringPart("facebookAT", facebookAT);
        req.addStringPart("deviceId", deviceId);
        req.addStringPart("appId", appId);
	    
	    AuthApiTask call = new AuthApiTask(getHttpAdaptor(), gson, context, callBack, logContext, authParams);

        if(callAsync) {
            call.execute(req);
        } else {
            AuthApiTaskResult res = call.doInBackground(req);
            if(callBack != null) {
                callBack.onPostExecute(res);
            }
        }
	}
	
	@SuppressWarnings("unchecked")
	public void registerAppWithGoogle(String googleId, String googleEmail, String googleDisplayName, String googleAT, String deviceId, String appId) {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "google");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("googleId", googleId);
        req.addStringPart("googleEmail", googleEmail);
        req.addStringPart("googleDisplayName", googleDisplayName);
        req.addStringPart("googleAT", googleAT);
        req.addStringPart("deviceId", deviceId);
        req.addStringPart("appId", appId);
	    
	    AuthApiTask call = new AuthApiTask(getHttpAdaptor(), gson, context, callBack, logContext, authParams);

        if(callAsync) {
            call.execute(req);
        } else {
            AuthApiTaskResult res = call.doInBackground(req);
            if(callBack != null) {
                callBack.onPostExecute(res);
            }
        }
	}
	
	@SuppressWarnings("unchecked")
	public void registerAppWithTwitter(String twitterId, String twitterDisplayName, String twitterAT, String twitterATsecret, String deviceId, String appId) {
        PostRequest req = new PostRequest(authParams.getAuthUrl(OP_REGISTER_APP), 1);

        req.addStringPart("mode", "twitter");
        req.addStringPart("op", OP_REGISTER_APP);
        req.addStringPart("twitterId", twitterId);
        req.addStringPart("twitterDisplayName", twitterDisplayName);
        req.addStringPart("twitterAT", twitterAT);
        req.addStringPart("twitterATsecret", twitterATsecret);
        req.addStringPart("deviceId", deviceId);
        req.addStringPart("appId", appId);
	    
	    AuthApiTask call = new AuthApiTask(getHttpAdaptor(), gson, context, callBack, logContext, authParams);

        if(callAsync) {
            call.execute(req);
        } else {
            AuthApiTaskResult res = call.doInBackground(req);
            if(callBack != null) {
                callBack.onPostExecute(res);
            }
        }
	}
	
	public static class AuthApiTaskResult {
		public String gcmId = null;
		public int playServicesAvailabilityError = -1;
		public String playServicesAvailabilityErrorString = null;
		public UserAuthResult authResult = null;
	}

	private static class AuthApiTask extends AsyncTask<PostRequest, Void, AuthApiTaskResult> {
		private HttpAdaptor httpClient;
		private Context context;
		private ApiCallBack callBack;
		private ProgressDialog dlg;
		private JsonWebCall<UserAuthResult> webCall;
        private LogContext logContext;
        private AuthParams authParams;
		
		public AuthApiTask(HttpAdaptor httpClient, Gson gson, Context context, ApiCallBack callBack, LogContext logContext, AuthParams authParams) {
			this.httpClient = httpClient;
			this.context = context;
			this.callBack = callBack;
			this.logContext = logContext;
			this.authParams = authParams;

			this.webCall = new JsonWebCall<UserAuthResult>(httpClient, gson, UserAuthResult.class);
		}
		
		@Override
	    protected AuthApiTaskResult doInBackground(PostRequest... reqs) {
			AuthApiTaskResult result = new AuthApiTaskResult();

            PostRequest req = reqs[0];
			
			synchronized(httpClient) {
				try {
                    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
                    int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
                    //int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
					
					if(errorCode == ConnectionResult.SUCCESS) {
						result.gcmId = registerWithGcm();
						
						if(result.gcmId != null && !result.gcmId.isEmpty()) {
							req.addStringPart("gcmId", result.gcmId);
							result.authResult = webCall.makeCall(req);
						}
					} else {
						result.playServicesAvailabilityError = errorCode;
                        result.playServicesAvailabilityErrorString = googleApiAvailability.getErrorString(errorCode);
						//result.playServicesAvailabilityErrorString = GooglePlayServicesUtil.getErrorString(errorCode);
					}
	    		} catch (Exception e) {
                    String errMsg = e.getMessage() == null?e.getClass().getSimpleName():e.getMessage();
                    String op = req.getStringPart(PAR_OP);
                    if(op != null) {
                        logContext.addLog(SyncLog.LVL_ERROR, op, errMsg);
                    } else {
                        logContext.addLog(SyncLog.LVL_ERROR, errMsg);
                    }
	    			Log.e(getClass().getSimpleName(), errMsg);
	    		}
			}
			
	        return result;
		}
		
		@Override
	    protected void onPostExecute(AuthApiTaskResult result) {
	    	if(dlg.isShowing()) {
	    		dlg.dismiss();
	    	}

	    	if(callBack != null) {
				callBack.onPostExecute(result);
			}
	    }
		
		@Override
	    protected void onPreExecute() {
			dlg = new ProgressDialog(context);
			dlg.setMessage("Please wait ...");
			dlg.show();
	    }

	    @Override
	    protected void onProgressUpdate(Void... values) {}
	    
	    private String registerWithGcm() {
            String gcmId = BalloPersistentState.getGcmId(context);

            if (gcmId.isEmpty()) {
            	for(int i=0; i<5; i++) {
            		if(i>0) {
            			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
            		}
	                try {
	                	/*
	                	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
	                	gcm.unregister();
	                    gcmId = gcm.register(((DefaultAuthPlugin)Lavolta.instance().getAuthPlugin()).gcmSenderId());
	                    */
	                    
	                    InstanceID.getInstance(context).deleteToken(authParams.getGmsSenderId(),"GCM");
	                    gcmId = InstanceID.getInstance(context).getToken(authParams.getGmsSenderId(),"GCM");
	                    break;
	                } catch (IOException ex) {
	                	Log.i(TAG, "Unable to register with GCM. " + ex.getMessage() + " Attempt " + i);
	                }
            	}
            	
            	if(gcmId != null && !gcmId.isEmpty()) {
                    BalloPersistentState.setGcmId(context, gcmId);
            	}
                return gcmId;
            } else {
            	return gcmId;
            }
		}
	}

}
