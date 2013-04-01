package com.akeng.filteritout.main;

import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.listener.WeiboRequestListener;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.AndroidHelper;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;

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

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	
	private static List<Status> statusList;
	
	private static StatusesAPI statusesAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//TODO: activity use action bar without title
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_home);
		
		View layout=findViewById(R.id.pager);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);
		
		//
		statusesAPI=new StatusesAPI(AccessTokenKeeper.readAccessToken(this));

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
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
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
  			Fragment fragment = new WeiboSectionFragment(HomeActivity.this);
			Bundle args = new Bundle();
			args.putInt(WeiboSectionFragment.ARG_SECTION_NUMBER, position + 1);
			
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
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class WeiboSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String ARG_SECTION_TYPE = "section_type";
		public static final int SECTION_FRIENDS = 1;
		public static final int SECTION_RECOMMENDS = 2;
		private Context context;

		public WeiboSectionFragment() {
		}
		
		public WeiboSectionFragment(Context context) {
			this.context=context;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View v = inflater.inflate(R.layout.weibo_section, container, false);
			ListView statusList=(ListView)v.findViewById(R.id.Msglist);
	
			StatusAdapter statusAdapter = new StatusAdapter();
			
			getWeiboStatus();
			statusList.setAdapter(statusAdapter);
			
			
			//statusesAPI.friendsTimeline((long)0, (long)0, 50, 1, false, FEATURE.ALL, true, new WeiboRequestListener());

			return v;
		}
		
		public void getWeiboStatus(){
			
			if(getArguments().getInt(ARG_SECTION_NUMBER)==SECTION_FRIENDS){
				statusesAPI.friendsTimeline((long)0, (long)0, 50, 1, false, FEATURE.ALL, true, new WeiboRequestListener(context));
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER)==SECTION_RECOMMENDS){
				statusesAPI.publicTimeline(50, 1, false, new WeiboRequestListener(context));
			}
		}
	}

	
	public static class StatusAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 15;
		}

		@Override
		public Object getItem(int arg0) {
			//TODO how to use this?
			return statusList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(statusList.get(position).getUserId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.i("Get-View","This is position:" + position); 
			
			 if (convertView == null) {
			        convertView =  View.inflate(parent.getContext(), R.layout.weibo, null);
//			        bananaView = (ImageView) convertView.findViewById(R.id.banana);
//			        phoneView = (TextView) convertView.findViewById(R.id.phone);
//			        convertView.setTag(new ViewHolder(bananaView, phoneView));
			    } else {
//			        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//			        bananaView = viewHolder.bananaView;
//			        phoneView = viewHolder.phoneView;
			    }

//			    BananaPhone bananaPhone = getItem(position);
//			    phoneView.setText(bananaPhone.getPhone());
//			    bananaView.setImageResource(bananaPhone.getBanana());
			
			return convertView;
		}
		

	}
	
	static class StatusHolder {
		ImageView userProfileImage;
		TextView username;
		TextView statusText;
		TextView statusTime;
		ImageView statusImage;
		
		public void setUserProfileImage(ImageView userProfileImage) {
			this.userProfileImage = userProfileImage;
		}
		public void setUsername(TextView username) {
			this.username = username;
		}
		public void setStatusText(TextView statusText) {
			this.statusText = statusText;
		}
		public void setStatusTime(TextView statusTime) {
			this.statusTime = statusTime;
		}
		public void setStatusImage(ImageView statusImage) {
			this.statusImage = statusImage;
		}
		
		
		}
}
