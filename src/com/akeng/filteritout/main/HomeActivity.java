package com.akeng.filteritout.main;

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

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.util.OAuth2;

public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener {

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
		Bundle args1 = new Bundle();
		friendSection = new WeiboSectionFragment();
		args1.putInt(ARG_SECTION_NUMBER, SECTION_FRIENDS);
		friendSection.setArguments(args1);
		recommendSection = new WeiboSectionFragment();
		Bundle args2 = new Bundle();
		args2.putInt(ARG_SECTION_NUMBER, SECTION_RECOMMENDS);
		recommendSection.setArguments(args2);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
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
	
	
	public void requestStatus(){
		
		if(mViewPager.getCurrentItem()==SECTION_FRIENDS){
			oauth.requestFriendStatus(mSectionsPagerAdapter.getItem(SECTION_FRIENDS));
		}
		else if(mViewPager.getCurrentItem()==SECTION_RECOMMENDS){
			oauth.requestPublicStatus(mSectionsPagerAdapter.getItem(SECTION_RECOMMENDS));

		}
	}
	
	public void updateSection(){
		//mSectionsPagerAdapter.
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
			if(position==SECTION_FRIENDS)
				return friendSection;
			if(position==SECTION_RECOMMENDS)
				return recommendSection;
			
			return null;
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
