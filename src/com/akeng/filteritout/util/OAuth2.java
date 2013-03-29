package com.akeng.filteritout.util;

import android.content.Context;
import android.widget.Toast;

import com.akeng.filteritout.activities.AuthorizeActivity;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;

public class OAuth2 {
	private Weibo mWeibo;
	private Context context;
	private WeiboAuthListener listener;
	private static final String CONSUMER_KEY = "351883100";//appkey
    private static final String REDIRECT_URL = "http://apps.weibo.com/sfourtestapp";
    public static Oauth2AccessToken accessToken;
    public static String userId;
  

	public static final String TAG = "sinasdk";
    
    public OAuth2(Context context,WeiboAuthListener listener){
    	this.context=context;
    	this.listener=listener;
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
    }
    
    public void requestAccessToken(){
        mWeibo.authorize(context, listener);
    }
    
    public void refreshAccessToken(){
    	
    }
    
    public void storeAccessToken(String userId,Oauth2AccessToken accessToken){
    	OAuth2.accessToken = accessToken;
    	OAuth2.userId = userId;
    	AccessTokenKeeper.keepAccessToken(context,accessToken,userId);
    }
    
    public void sentRequest(String url,String param){

    }
    
    public static Oauth2AccessToken getAccessToken() {
  		return accessToken;
  	}


  	public static String getUserId() {
  		return userId;
  	}

}
