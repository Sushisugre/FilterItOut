package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.WeiboAnalyzer;


public class WeiboSectionFragment extends Fragment{
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private List<Status> statusList;
	public WeiboListView statusListView;
	
	public WeiboSectionFragment() {
		statusList=new ArrayList<Status>( );
	}

	@Override
	public void onResume() {
		System.out.println("------On Resume-----");
		updateList();
		super.onResume();
	}
	
	

	@Override
	public void onPause() {
		System.out.println("------On Pause-----");
		new CacheStatusTask().execute(statusList);
		
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.weibo_section, container, false);
		statusListView = (WeiboListView) v.findViewById(R.id.Msglist);
		StatusAdapter statusAdapter = new StatusAdapter();
		statusListView.setAdapter(statusAdapter);
		statusListView.setRefreshListener((HomeActivity)getActivity());
		statusListView.setSection(getArguments().getInt(HomeActivity.ARG_SECTION_NUMBER));
		
		return v;
	}
	
	public void onUpdateContent() {

		int section = this.getArguments().getInt(
				HomeActivity.ARG_SECTION_NUMBER);

		if (section == HomeActivity.SECTION_FRIENDS){
			statusList = HomeActivity.friendStatusList;
		}
		if (section == HomeActivity.SECTION_RECOMMENDS)
			statusList = HomeActivity.publicStatusList;
		
		updateList();
		//statusListView.setSelectionAfterHeaderView();
		statusListView.finishFootView();

	}
	
	public void updateList(){
		HeaderViewListAdapter ha = (HeaderViewListAdapter) statusListView.getAdapter();
		StatusAdapter adapter=(StatusAdapter)ha.getWrappedAdapter();
				adapter.notifyDataSetChanged();
	}
	

	//public class StatusAdapter extends BaseAdapter {
	public class StatusAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			return statusList.size();
		}

		@Override
		public Object getItem(int arg0) {

			return statusList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(statusList.get(position).getUserId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView userProfileImage;
			TextView username;
			TextView statusText;
			TextView statusTime;
			ImageView statusImage;
			LikeButton btnLike;
			ImageButton btnDelete;
			View retweet;
			TextView retweetUser;
			TextView retweetText;
			
			Status status = (Status) getItem(position);
			if(status.isDeleted()==true)
				return null;

//			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.weibo,null);
				userProfileImage = (ImageView) convertView.findViewById(R.id.wbicon);
				username = (TextView) convertView.findViewById(R.id.wbuser);
				statusText = (TextView) convertView.findViewById(R.id.wbtext);
				statusTime = (TextView) convertView.findViewById(R.id.wbtime);
				statusImage = (ImageView) convertView.findViewById(R.id.wbimage);
				btnLike = (LikeButton) convertView.findViewById(R.id.btn_like);
				btnDelete = (ImageButton) convertView.findViewById(R.id.btn_delete);


//				StatusHolder viewHolder = new StatusHolder();
//				viewHolder.username=username;
//				viewHolder.userProfileImage=userProfileImage;
//				viewHolder.statusText=statusText;
//				viewHolder.statusTime=statusTime;
//				viewHolder.statusImage=statusImage;
//				viewHolder.btnDelete=btnDelete;
//				viewHolder.btnLike=btnLike;
				
				final int statusPosition=position;
				
				btnLike.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						LikeButton button=(LikeButton)v;
						button.toggle();
						Status status=statusList.get(statusPosition);
						boolean isLike=status.isLike();
						status.setLike(!isLike);
						
						if(status.hasRetweetedStatus()){
							status.getRetweetedStatus().setType(Status.FAVOR+Status.RETWEET);
						}
						
						//if(status.isLike())
							new RecordStatusTask().execute(status);

					}});
				
				btnDelete.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
						Status status=statusList.get(statusPosition);
						status.setDeleted(true);
						if(status.hasRetweetedStatus()){
							status.getRetweetedStatus().setType(Status.DISLIKE+Status.RETWEET);
						}
						
						new RecordStatusTask().execute(status);
						
						//delete the status
						statusList.remove(statusPosition);
						updateList();

					}});
				
//				convertView.setTag(viewHolder);
//				//mark with statue id
//				viewHolder.btnLike.setTag(status.getId());
				
//			} else {
//				StatusHolder viewHolder = (StatusHolder) convertView.getTag();
//				userProfileImage = viewHolder.userProfileImage;
//				username = viewHolder.username;
//				statusText = viewHolder.statusText;
//				statusTime = viewHolder.statusTime;
//				statusImage = viewHolder.statusImage;
//				btnDelete = viewHolder.btnDelete;
//				btnLike = viewHolder.btnLike;
//				//update tage
//				((StatusHolder) convertView.getTag()).btnLike.setTag(status.getId());
//			}

			
			username.setText(status.getUsername());
			statusText.setText(status.getText());
			statusTime.setText(status.getTime());
			btnLike.setChecked(statusList.get(statusPosition).isLike());
			if(status.getRetweetedStatus()!=null){
				retweet=View.inflate(parent.getContext(), R.layout.retweet,null); 
				retweetText=(TextView)retweet.findViewById(R.id.rttext);
				retweetUser=(TextView)retweet.findViewById(R.id.rtuser);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				LinearLayout weiboContent=(LinearLayout)convertView.findViewById(R.id.wbcontent);
				weiboContent.addView(retweet, lp);
				//((RelativeLayout)convertView).addView(retweet, lp);
				retweetText.setText(status.getRetweetedStatus().getText());
				retweetUser.setText(status.getRetweetedStatus().getUsername());
			}

			return convertView;
		}

		private class StatusHolder {

			ImageView userProfileImage;
			TextView username;
			TextView statusText;
			TextView statusTime;
			ImageView statusImage;
			LikeButton btnLike;
			ImageButton btnDelete;

		}
	}
	
	private class RecordStatusTask extends AsyncTask<Status, Void, String>{

		@Override
		protected String doInBackground(
				com.akeng.filteritout.entity.Status... raw) {
			
				String keys="";
				String text;
				if(raw[0].getRetweetedStatus()==null)
					text=raw[0].getText();
				else
					text=raw[0].getRetweetedStatus().getText();
				
			try{
				String content=WeiboAnalyzer.cleanUpText(text);
		        Map<String,Integer> keyMap=WeiboAnalyzer.splitStatus(content);
		      
		        Iterator<String> it=keyMap.keySet().iterator();
		        while(it.hasNext()){
		        	String key=(String)it.next();
		        	keys=keys+key+":"+keyMap.get(key)+",";
		        }
		        System.out.println(keys);
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			
			
	        DataHelper dataHelper=new DataHelper(getActivity());
			String userId=AccessTokenKeeper.readUserId(getActivity());
	        dataHelper.addStatus(userId, raw[0], keys);
	        dataHelper.close();

			return keys;
		}
		
	    protected void onPostExecute(String result) {
    		Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
	    }		
	}
	
	private class CacheStatusTask extends AsyncTask<List<Status>, Void, String>{

		@Override
		protected String doInBackground(
				List<com.akeng.filteritout.entity.Status>... params) {
			
			if(params[0].isEmpty())
				return null;
			
	        DataHelper dataHelper=new DataHelper(getActivity());
	        
	        dataHelper.clearCachedStatus(params[0].get(0).getSection());
	        
	        for(int i=0;i<params[0].size()&&i<15;i++){
			String userId=AccessTokenKeeper.readUserId(getActivity());
	        dataHelper.addStatus(userId, params[0].get(i), null);
	        }
	        dataHelper.close();
	        
			return null;
		}
		
	}
	

}