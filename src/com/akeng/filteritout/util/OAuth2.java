package com.akeng.filteritout.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.listener.AuthDialogListener;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FavoritesAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.TagsAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

public class OAuth2 {
	
	private static final String CONSUMER_KEY = "351883100";//appkey
    private static final String REDIRECT_URL = "http://apps.weibo.com/sfourtestapp";
	public static final String FRIEND_STATUS="friend";
	public static final String PUBLIC_STATUS="public";
	public static final Set<String> TOKEN_ERRORS = new HashSet<String>(Arrays.asList(new String[]{"21315","21327","21316","21317","21314"}));
	
	private static Weibo mWeibo;
	private static Context mContext;
	private static WeiboAuthListener listener;
	private static StatusesAPI statusesAPI;
	private static TagsAPI tagsAPI;
	private static FavoritesAPI favoritesAPI;
	public static final String TAG = "OAuth2";
	public static long sinceId=0;
	public static long maxId=0;
	public static Date lastUpdated;
    
    public OAuth2(Context context,WeiboAuthListener listener){
    	this.init(context, listener);
    }
    
    public OAuth2(Context context){
    	this.init(context, new AuthDialogListener(context));
    }
    
    public void init(Context context,WeiboAuthListener listener){
    	mContext=context;
    	if(listener==null)
    		listener= new AuthDialogListener(context);
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
    	statusesAPI=new StatusesAPI(AccessTokenKeeper.readAccessToken(context));
    }
    
    public void requestAccessToken(){
    	if(listener==null){
    		listener= new AuthDialogListener(mContext);
    	}
		System.out.println("request access token");

    		mWeibo.authorize(mContext, listener);
    	
    }
    
    public void refreshAccessToken(){
    	
    }
    
    public static void storeAccessToken(String userId,Oauth2AccessToken accessToken){
    	AccessTokenKeeper.keepAccessToken(mContext,accessToken,userId);
    }
    
    public void requestNewFriendStatus(RequestListener listener){
    	requestFriendStatus(sinceId,0,listener);
    }
    
    public void requestEarlierFriendStatus(RequestListener listener){
    	requestFriendStatus(0,maxId,listener);
    }
    
    public void requestFriendStatus(long sinceId,long maxId,RequestListener listener){
    		statusesAPI.friendsTimeline(sinceId, maxId, 15, 1, false, FEATURE.ALL, false, listener);
    }
    
    public void requestPublicStatus(RequestListener listener){
		statusesAPI.publicTimeline(200, 1, false, listener);
    }
    
    /**
     * User tags
     * @param listener
     */
    public void requestUserTags(RequestListener listener){
    	tagsAPI= new TagsAPI(AccessTokenKeeper.readAccessToken(mContext));
		tagsAPI.tags(Long.parseLong(getUserId()), 20, 1, listener);
    }
    /**
     * User favorite status tags
     * @param listener
     */
    public void requestFavoriteTags(RequestListener listener){
    	favoritesAPI.tags(20, 1, listener);
    }
    /**
     * User favorite status
     * @param listener
     */
    public void requestFavorites(RequestListener listener){
    	favoritesAPI.favorites(50, 1, listener);
    }
    
    public static List<Tag> parseTag(String response){
    	List<Tag> tagList=new ArrayList<Tag>();
    	
    	try{
        	JSONArray jsonArray=new JSONArray(response);
        	for(int i=0;i<jsonArray.length();i++){
        		JSONObject object=jsonArray.getJSONObject(i);
        		Iterator<String> it=object.keys();
        		while(it.hasNext()){
        			String key=it.next();
        			if(!key.equals("weight")){
        				Tag tag=new Tag();
        				tag.setTagName(object.getString(key));
        				tag.setType(Tag.FAVOR);
        				tagList.add(tag);
        			}
        		}
        	}
    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
    	
    	return tagList;
    }
    
    public static List<Status> parseResponse(String response)throws WeiboException{
    	
		 List<Status> statusList=new ArrayList<Status>();
		// Log.i("JSON Response", response);
    	try{
    		JSONObject jsonResponse=new JSONObject(response);
    		//api error
    		if(jsonResponse.has("error")){
    			jsonResponse.getString("error");
    			jsonResponse.getInt("error_code");
    			
    			throw new WeiboException(jsonResponse.getString("error"),jsonResponse.getInt("error_code"));
    		}
    		
    		
        	JSONArray data=jsonResponse.getJSONArray("statuses");
        	for(int i=0;i<data.length();i++)
            {
                JSONObject d=data.getJSONObject(i);
                if(d!=null){
                	
                	if(d.has("advertises")){
                		continue;
                	}
                	
                	Status status=OAuth2.setStatusInfo(d);
                	Status retweetedStatus;
               
                    if(d.has("retweeted_status")){
                        JSONObject r=d.getJSONObject("retweeted_status");
                    	retweetedStatus=OAuth2.setStatusInfo(r);
                    	retweetedStatus.setType(Status.RETWEET);
                    	status.setRetweetedStatus(retweetedStatus);
                    }

                    
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
              
            }
            
            status.setId(obj.getLong("id"));
            status.setText(obj.getString("text"));
            status.setTime(obj.getString("created_at"));
            status.setUserId(u.getString("id"));
            status.setUsername(u.getString("screen_name"));
            status.setUserProfilePic(u.getString("profile_image_url"));
            status.setRepostsCount(obj.getInt("reposts_count"));
            status.setCommentsCount(obj.getInt("comments_count"));
            status.setAttitudesCount(obj.getInt("attitudes_count"));
            
            u=null;
            obj=null;
            
           // Log.e("userIcon", u.getString("profile_image_url"));  

    	}
    	catch(JSONException e){
    		e.printStackTrace();
    	}
      
    	
    	return status;
    }
    
    public static Oauth2AccessToken getAccessToken() {
  		return AccessTokenKeeper.readAccessToken(mContext);
  	}


  	public static String getUserId() {
  		return AccessTokenKeeper.readUserId(mContext);
  	}


}
