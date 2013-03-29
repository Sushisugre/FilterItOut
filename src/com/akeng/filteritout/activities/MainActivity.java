package com.akeng.filteritout.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.akeng.filteritout.R;
import com.akeng.filteritout.data.UserInfo;
import com.akeng.filteritout.util.AndroidHelper;
import com.akeng.filteritout.util.DataHelper;

public class MainActivity extends FragmentActivity{

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View layout=findViewById(R.id.layout);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);
		
		//TODO:Check token expired and renew
		//now simply request token every time
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, AuthorizeActivity.class);
        startActivity(intent);
		
//		//get user list
//		DataHelper dbHelper=new DataHelper(this);
//        List<UserInfo> userList= dbHelper.GetUserList(true);
//        if(userList.isEmpty())//go to AuthorizeActivity for OAuth at the first time
//        {
//               Intent intent = new Intent();
//               intent.setClass(MainActivity.this, AuthorizeActivity.class);
//               startActivity(intent);
//        }
//        else//get UserID,Access Token,Access Secret if not null
//            //get username/image from api
//		{
//			for (UserInfo user : userList) {
//				// TODO: exist user
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, HomeActivity.class);
//				startActivity(intent);
//			}
//		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}



}
