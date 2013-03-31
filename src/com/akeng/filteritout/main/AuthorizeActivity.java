package com.akeng.filteritout.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.UserInfo;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.AndroidHelper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class AuthorizeActivity extends Activity {
	//private Weibo mWeibo;
	//private static final String CONSUMER_KEY = "351883100";// 替换为开发者的appkey，例如"1646212860";
    //private static final String REDIRECT_URL = "http://apps.weibo.com/sfourtestapp";
    private Dialog dialog;
	public static Oauth2AccessToken accessToken;
    public static final String TAG = "sinasdk";
    public OAuth2 oauth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorize);
		
		View layout=findViewById(R.id.layout_authorize);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);

		
		View diaView=View.inflate(this, R.layout.dialog, null);
		dialog=new Dialog(AuthorizeActivity.this,R.style.dialog);
		dialog.setContentView(diaView);
		dialog.getWindow().setLayout(210,140);
		dialog.show();
		
		
    	oauth=new OAuth2(AuthorizeActivity.this, new AuthDialogListener());
        //mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		
		ImageButton btnStart=(ImageButton)diaView.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
            	Log.i("S4DEBUG", "----On click start----");
            	
            	//TODO: what if auth fail
            	dialog.dismiss();

                oauth.requestAccessToken();
            	//mWeibo.authorize(AuthorizeActivity.this, new AuthDialogListener());

            }
            
        });
	}
	
	
	
	class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String user_id=values.getString("uid");
            AuthorizeActivity.accessToken = new Oauth2AccessToken(token, expires_in);
            if (AuthorizeActivity.accessToken.isSessionValid()) {            	
            	oauth.storeAccessToken(user_id, accessToken);
//                AccessTokenKeeper.keepAccessToken(AuthorizeActivity.this,
//                        accessToken,user_id);
                Toast.makeText(AuthorizeActivity.this, "认证成功", Toast.LENGTH_SHORT)
                        .show();
                
              //TODO: database?  
//                UserInfo user=new UserInfo();
//                user.setUserId(user_id);
//                user.setToken(token);
//                //add user to db
//                if(user!=null){
//                    DataHelper helper=new DataHelper(AuthorizeActivity.this);
//                    String uid=user.getUserId();
//                    if(helper.HaveUserInfo(uid))
//                    {
//                        helper.UpdateUserInfo(user);
//                        Log.e("UserInfo", "update");
//                    }else
//                    {
//                        helper.SaveUserInfo(user);
//                        Log.e("UserInfo", "add");
//                    }
//                }
                
                //to homepage
                Intent intent = new Intent();
                intent.setClass(AuthorizeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
        
        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
	}
	

}
