package com.akeng.filteritout.listener;

import java.io.IOException;

import org.json.JSONArray;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.akeng.filteritout.main.HomeActivity;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class WeiboRequestListener implements RequestListener {
	
	private Context context;
	private String type;

	
	public WeiboRequestListener(){
		
	}
	
	public WeiboRequestListener(Context context,String type){
		this.context=context;
		this.type=type;
	}

	@Override
	public void onComplete(String arg0){
		OAuth2.response=arg0;
		if(type.equals(OAuth2.FRIEND_STATUS)){
			HomeActivity.friendStatusList=OAuth2.parseResponse();
			Log.e("Status Number", ""+HomeActivity.friendStatusList.size());

		}
		else if(type.equals(OAuth2.PUBLIC_STATUS)){
			HomeActivity.publicStatusList=OAuth2.parseResponse();
			Log.e("Status Number", ""+HomeActivity.publicStatusList.size());


		}
	}

	@Override
	public void onError(WeiboException arg0) {
		Log.e("Weibo Status","Fail to get weibo, Status code: "+arg0.getStatusCode());
		Toast.makeText(context, "获取微博失败："+arg0.getStatusCode(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}

}
