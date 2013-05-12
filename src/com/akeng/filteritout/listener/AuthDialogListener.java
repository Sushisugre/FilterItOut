package com.akeng.filteritout.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.akeng.filteritout.entity.UserInfo;
import com.akeng.filteritout.main.HomeActivity;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class AuthDialogListener implements WeiboAuthListener {
	
	private Context context;
	
	public AuthDialogListener(Context context){
		this.context=context;
	}

    @Override
    public void onComplete(Bundle values) {
        String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        String user_id=values.getString("uid");
        Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
        if (accessToken.isSessionValid()) {            	
        	OAuth2.storeAccessToken(user_id, accessToken);

        	Toast.makeText(context, "认证成功", Toast.LENGTH_SHORT)
                    .show();
            
          //TODO: database?  
            UserInfo user=new UserInfo();
            user.setUserId(user_id);
            user.setToken(token);
            //add user to db
            if(user!=null){
                DataHelper helper=new DataHelper(context);
                String uid=user.getUserId();
                if(helper.HaveUserInfo(uid))
                {
                    helper.UpdateUserInfo(user);
                    Log.e("UserInfo", "update");
                }else
                {
                    helper.SaveUserInfo(user);
                    Log.e("UserInfo", "add");
                }
            }
            
            //to homepage
            Intent intent = new Intent();
            intent.setClass(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    
    @Override
    public void onError(WeiboDialogError e) {
        Toast.makeText(context,
                "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(context, "Auth cancel",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Toast.makeText(context,
                "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                .show();
    }
}

