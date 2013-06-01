package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.RecommendParam;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.main.WeiboListView.RefreshListener;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener,RequestListener,RefreshListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	public WeiboSectionFragment friendSection;
	public WeiboSectionFragment recommendSection;
	private RecommendTask recommentor=null;

	public static List<Status> friendStatusList=new ArrayList<Status>();
	public static List<Status> publicStatusList=new ArrayList<Status>();
	private static OAuth2 oauth;
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_SECTION_TYPE = "section_type";
	public static final int SECTION_FRIENDS = 0;
	public static final int SECTION_RECOMMENDS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("Activity on create");

		super.onCreate(savedInstanceState);
		
		oauth=new OAuth2(HomeActivity.this);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		setContentView(R.layout.activity_home);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		//AndroidHelper.AutoBackground(this, mViewPager, R.drawable.app_bg_v, R.drawable.app_bg_h);
		mViewPager.setBackgroundResource(R.drawable.app_bg_v);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	public void onDestroy(){
		System.out.println("activity - On destroy - ");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            return true;
	        case R.id.menu_tag:
	        	Intent launchNewIntent = new Intent(HomeActivity.this,TagActivity.class);
	        	startActivityForResult(launchNewIntent, 0);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		refreshing(mViewPager.getCurrentItem());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	/**
	 * OAuth weibo request completed
	 */
	@Override
	public void onComplete(String arg0){
		
		int section = mViewPager.getCurrentItem();
		try {
			final List<Status> newList = OAuth2.parseResponse(arg0);

			if (section == SECTION_FRIENDS) {
				if (newList.size() > 0) {
					OAuth2.sinceId = OAuth2.sinceId > newList.get(0).getId() ? OAuth2.sinceId
							: newList.get(0).getId();
					OAuth2.maxId = OAuth2.maxId < (newList.get(
							newList.size() - 1).getId() - 1) ? OAuth2.maxId
							: (newList.get(newList.size() - 1).getId() - 1);
				}
			}

			// construct parameter for recommend task
			RecommendParam candidates = new RecommendParam();
			candidates.setSection(section);
			candidates.setStatus(newList);

			recommentor = new RecommendTask(this);
			recommentor.attach(this);
			recommentor.execute(candidates);

		} catch (WeiboException we) {
			final String errorMessage = we.getMessage();
			final int errorCode = we.getStatusCode();

			// toast error message
			this.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplication(), "Error:" + errorMessage,
							Toast.LENGTH_SHORT).show();
				}
			});

			// if token expired, go to authorizeActivity to refresh it
			if (OAuth2.TOKEN_ERRORS.contains(errorCode)) {
				Intent intent = new Intent();
				intent.setClass(this, AuthorizeActivity.class);
				startActivity(intent);
				this.finish();
			}
		}
	}
	


	@Override
	public void onError(WeiboException arg0) {
		Log.e("Weibo Status","Fail to get weibo, Status code: "+arg0.getStatusCode());
		String info;
		if(arg0.getStatusCode()==-1)
			info="无法连接服务器，请检查网络";
		else{
			info="错误代码："+arg0.getStatusCode();
		}
		
		final String message=info;
		this.runOnUiThread(new Runnable() {
			
		     public void run() {
		 		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		     }
		});
	}

	@Override
	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}
	
	private static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}
	
	public void updateSection(final int section){

			this.runOnUiThread(new Runnable() {
			     public void run() {
			    	 WeiboSectionFragment frament=(WeiboSectionFragment)getSupportFragmentManager().
			    			 findFragmentByTag(makeFragmentName(R.id.pager,section));
					frament.onUpdateContent();
			    }
			});

	}
	
	/**
	 * Listen to listview refresh event
	 */
	@Override
	public void refreshing(int section) {
		if(section==SECTION_FRIENDS)
			oauth.requestNewFriendStatus(this);
		if(section==SECTION_RECOMMENDS)
			oauth.requestPublicStatus(this);
	}

	@Override
	public void refreshed(Object obj) {
	}

	@Override
	public void more(int section) {
		if(section==SECTION_FRIENDS)
			oauth.requestEarlierFriendStatus(this);
		if(section==SECTION_RECOMMENDS)
			oauth.requestPublicStatus(this);
	}
	
	  
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public WeiboSectionFragment getItem(int position) {
			
			WeiboSectionFragment fragment = new WeiboSectionFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section_attention).toUpperCase();
			case 1:
				return getString(R.string.title_section_recommend).toUpperCase();
			}
			return null;
		}
		
	}


}
