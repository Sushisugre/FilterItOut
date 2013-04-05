package com.akeng.filteritout.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.listener.AuthDialogListener;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

public class OAuth2 {
	
	private static final String CONSUMER_KEY = "351883100";//appkey
    private static final String REDIRECT_URL = "http://apps.weibo.com/sfourtestapp";
	public static final String FRIEND_STATUS="friend";
	public static final String PUBLIC_STATUS="public";
	
	private static Weibo mWeibo;
	private static Context context;
	private static WeiboAuthListener listener;
	private static StatusesAPI statusesAPI;
	public static String response;
	public static final String TAG = "OAuth2";
	public static long sinceId=0;
	public static long maxId=0;
	public static Date lastUpdated;
    
    public OAuth2(Context context,WeiboAuthListener listener){
    	this.context=context;
    	this.listener=listener;
        this.mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
    	this.statusesAPI=new StatusesAPI(AccessTokenKeeper.readAccessToken(context));
    }
    
    public OAuth2(Context context){
    	//OAuth2(context,WeiboAuthListener listener);
    	this.context=context;
    	this.listener= new AuthDialogListener(context);
        this.mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
    	this.statusesAPI=new StatusesAPI(AccessTokenKeeper.readAccessToken(context));
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
    
    public void requestNewFriendStatus(RequestListener listener){
    	statusesAPI.friendsTimeline(sinceId, 0, 15, 1, false, FEATURE.ALL, false, listener);
    }
    
    public void requestEarlierFriendStatus(RequestListener listener){
    	statusesAPI.friendsTimeline(0, maxId, 15, 1, false, FEATURE.ALL, false, listener);
    }
    
    public void requestFriendStatus(RequestListener listener){
		statusesAPI.friendsTimeline(sinceId, maxId, 15, 1, false, FEATURE.ALL, false, listener);

    }
    
    public void requestPublicStatus(RequestListener listener){
		statusesAPI.publicTimeline(15, 1, false, listener);
    }
    
    public static List<Status> parseResponse(){
    	
		 List<Status> statusList=new ArrayList<Status>();
		 Log.i("JSON Response", response);
    	try{
    		JSONObject jsonResponse=new JSONObject(response);
        	JSONArray data=jsonResponse.getJSONArray("statuses");
        	for(int i=0;i<data.length();i++)
            {
                JSONObject d=data.getJSONObject(i);
               // Log.i("JSON Object", d.toString());
                if(d!=null){
                	
                	if(d.has("advertises")){
                		continue;
                	}
                	
                	Status status=OAuth2.setStatusInfo(d);
                	Status retweetedStatus;
               
                    if(d.has("retweeted_status")){
                        JSONObject r=d.getJSONObject("retweeted_status");
                    	retweetedStatus=OAuth2.setStatusInfo(r);
                    	status.setRetweetedStatus(retweetedStatus);
                    }

                    
                    //Date date=new Date(time);
                    //time=ConvertTime(date);

                    
                    statusList.add(status);
                }

            }

    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
    	
    	return statusList;
    }
    
    private static Status setStatusInfo(JSONObject obj){
    	
    	Status status=new Status();
    	
    	try{
        	JSONObject u=obj.getJSONObject("user");
        	
            if(obj.has("thumbnail_pic")){
            	status.setHaveImage(true);
            	status.setThumbnailPic(obj.getString("thumbnail_pic"));
            	status.setMiddlePic(obj.getString("bmiddle_pic"));
              
            	//Log.e("thumbnail_pic", obj.getString("thumbnail_pic"));
                //Log.e("bmiddle_pic",obj.getString("bmiddle_pic"));
            }
            
            status.setId(obj.getString("id"));
            status.setText(obj.getString("text"));
            status.setTime(obj.getString("created_at"));
            status.setUserId(u.getString("id"));
            status.setUsername(u.getString("screen_name"));
            status.setUserProfilePic(u.getString("profile_image_url"));
            status.setRepostsCount(obj.getInt("reposts_count"));
            status.setCommentsCount(obj.getInt("comments_count"));
            status.setAttitudesCount(obj.getInt("attitudes_count"));
            
           // Log.e("userIcon", u.getString("profile_image_url"));  
          Log.e("Status Test", obj.getString("text"));

    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
      
    	
    	return status;
    }
    
    public static Oauth2AccessToken getAccessToken() {
  		return AccessTokenKeeper.readAccessToken(context);
  	}


  	public static String getUserId() {
  		return AccessTokenKeeper.readUserId(context);
  	}

}
