package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.akeng.filteritout.R;
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

	public static List<Status> friendStatusList=new ArrayList<Status>();
	public static List<Status> publicStatusList=new ArrayList<Status>();
	private static OAuth2 oauth;
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_SECTION_TYPE = "section_type";
	public static final int SECTION_FRIENDS = 0;
	public static final int SECTION_RECOMMENDS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//TODO: activity use action bar without title
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_home);
		
		View layout=findViewById(R.id.pager);
		//AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);
		layout.setBackgroundResource(R.drawable.app_bg_v);
		
		oauth=new OAuth2(HomeActivity.this);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.

//		Bundle args1 = new Bundle();
//		friendSection = new WeiboSectionFragment();
//		args1.putInt(ARG_SECTION_NUMBER, SECTION_FRIENDS);
//		friendSection.setArguments(args1);
//		recommendSection = new WeiboSectionFragment();
//		Bundle args2 = new Bundle();
//		args2.putInt(ARG_SECTION_NUMBER, SECTION_RECOMMENDS);
//		recommendSection.setArguments(args2);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		

		mViewPager = (ViewPager) findViewById(R.id.pager);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		 requestStatus();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public void onComplete(String arg0){
		
		Log.i("On request complete", "On request completed!");
		
		OAuth2.response=arg0;
		int section=mViewPager.getCurrentItem();
		final List<Status> newList=OAuth2.parseResponse();
		
		if(section==SECTION_FRIENDS){
			if(newList.size()>0){
				//returned first id < OAuth2.maxId, earlier status
				int index=Long.parseLong(newList.get(0).getId())<OAuth2.maxId ?
						friendStatusList.size():0;
				friendStatusList.addAll(index,newList);
			}
			

		}
		else if(section==SECTION_RECOMMENDS){
			publicStatusList.addAll(newList);

		}
		updateSection(section);
	
		//toast shows the new loaded number
		
		this.runOnUiThread(new Runnable() {
		     public void run() {
		 		if(newList.size()==0)
					Toast.makeText(getApplication(),getString(R.string.no_more_new), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplication(),newList.size()+getString(R.string.new_statuses), Toast.LENGTH_SHORT).show();
		     }
		});
		
	
	}

	@Override
	public void onError(WeiboException arg0) {
		Log.e("Weibo Status","Fail to get weibo, Status code: "+arg0.getStatusCode());
		Toast.makeText(this, "获取微博失败："+arg0.getStatusCode(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}
	
	private static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}
	
	
	public void requestStatus(){
		
		if(mViewPager.getCurrentItem()==SECTION_FRIENDS){
			oauth.requestEarlierFriendStatus(this);
		}
		else if(mViewPager.getCurrentItem()==SECTION_RECOMMENDS){
			oauth.requestPublicStatus(this);
		}
	}
	
	public void updateSection(final int section){
		Log.e("Update-List", "--------Test Update Section-------");
//			Log.e("Section number", "Section: "+section);
//			Log.e("Fragment tag", "Frament Tag: "+this.getTag());
//			Log.e("Maked Tag","Maked Tag: "+makeFragmentName(R.id.pager,section));
			this.runOnUiThread(new Runnable() {
				
			     public void run() {
			    	 WeiboSectionFragment frament=(WeiboSectionFragment)getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,section));
					frament.onUpdateContent();
						
//			    	 ListView statusList = (ListView) 
//								getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,section)).
//								getView().findViewById(R.id.Msglist);	
//						statusList.invalidateViews();
			    }
			});

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
			Log.e("Frament Adapter", "---get Item------");
			
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

	@Override
	public Object refreshing() {
		// TODO Auto-generated method stub
		oauth.requestNewFriendStatus(this);
		return null;
	}

	@Override
	public void refreshed(Object obj) {
		// TODO Auto-generated method stub
		Log.i("Refreshed", "Refreshed!!!!");
		
		
	}

	@Override
	public void more() {
		oauth.requestEarlierFriendStatus(this);
	}


}
