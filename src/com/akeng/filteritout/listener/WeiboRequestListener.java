package com.akeng.filteritout.listener;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.akeng.filteritout.activities.HomeActivity;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class WeiboRequestListener implements RequestListener {
	
	private Context context;
	
	public WeiboRequestListener(){
		
	}
	
	public WeiboRequestListener(Context context){
		this.context=context;
	}

	@Override
	public void onComplete(String arg0) {
		// TODO Auto-generated method stub
		Log.i("Weibo Status", arg0);
	}

	@Override
	public void onError(WeiboException arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onIOException(IOException arg0) {
		// TODO Auto-generated method stub

	}

}
