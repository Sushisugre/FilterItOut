package com.akeng.filteritout.listener;

import java.io.IOException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.akeng.filteritout.main.AuthorizeActivity;
import com.akeng.filteritout.main.HomeActivity;
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
		// TODO parce api response
		Log.i("Weibo Status", arg0);
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
