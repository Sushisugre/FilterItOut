package com.akeng.filteritout.util;

import android.content.Context;

import com.akeng.filteritout.listener.WeiboRequestListener;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;

public class OAuth2 {
	private Weibo mWeibo;
	private static Context context;
	private WeiboAuthListener listener;
	private static final String CONSUMER_KEY = "351883100";//appkey
    private static final String REDIRECT_URL = "http://apps.weibo.com/sfourtestapp";
	private static StatusesAPI statusesAPI;
	public static final String TAG = "OAuth2";
    
    public OAuth2(Context context,WeiboAuthListener listener){
    	this.context=context;
    	this.listener=listener;
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
    }
    
    public OAuth2(Context context){
    	//OAuth2(context,WeiboAuthListener listener);
    	this.context=context;
    }
    
    public void requestAccessToken(){
    	if(listener!=null)
    		mWeibo.authorize(context, listener);
    }
    
    public void refreshAccessToken(){
    	
    }
    
    public static void storeAccessToken(String userId,Oauth2AccessToken accessToken){
    	AccessTokenKeeper.keepAccessToken(context,accessToken,userId);
    }
    
    public void getFriendStatus(){
		statusesAPI.friendsTimeline((long)0, (long)0, 50, 1, false, FEATURE.ALL, true, new WeiboRequestListener(context));
    }
    
    public void getPublicStatus(){
		statusesAPI.publicTimeline(50, 1, false, new WeiboRequestListener(context));

    }
    
    public static Oauth2AccessToken getAccessToken() {
  		return AccessTokenKeeper.readAccessToken(context);
  	}


  	public static String getUserId() {
  		return AccessTokenKeeper.readUserId(context);
  	}

}
