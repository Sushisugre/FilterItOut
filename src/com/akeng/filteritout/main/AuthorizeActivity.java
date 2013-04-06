package com.akeng.filteritout.main;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.akeng.filteritout.R;
import com.akeng.filteritout.util.AndroidHelper;
import com.akeng.filteritout.util.OAuth2;
import com.akeng.filteritout.listener.AuthDialogListener;
import com.weibo.sdk.android.Oauth2AccessToken;

public class AuthorizeActivity extends Activity {
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
		
		
    	oauth=new OAuth2(this.getApplicationContext(), new AuthDialogListener(AuthorizeActivity.this));
		
		ImageButton btnStart=(ImageButton)diaView.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
            	
            	//TODO: what if auth fail
            	dialog.dismiss();

                oauth.requestAccessToken();

            }
            
        });
	}
	

}
