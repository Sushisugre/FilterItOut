package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.akeng.filteritout.util.WeiboAnalyzer;


public class WeiboSectionFragment extends Fragment{
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private List<Status> statusList;
	public WeiboListView statusListView;
	private Activity fragmentActivity;
	
	public WeiboSectionFragment() {
		statusList=new ArrayList<Status>( );
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
		
		fragmentActivity=this.getActivity();
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
		//	Log.i("Get-View", "This is position:" + position);

			ImageView userProfileImage;
			TextView username;
			TextView statusText;
			TextView statusTime;
			ImageView statusImage;
			LikeButton btnLike;
			ImageButton btnDelete;
			
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
						
						if(status.isLike())
							new RecordStatusTask().execute(status);

					}});
				
				btnDelete.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
						Status status=statusList.get(statusPosition);
						status.setDeleted(true);
						
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
			String result="";
			
			try{
				result=WeiboAnalyzer.splitStatus(raw[0].getText());
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
			//TODO : save to database
	        DataHelper dataHelper=new DataHelper(fragmentActivity);
			String userId=AccessTokenKeeper.readUserId(fragmentActivity);
	        dataHelper.addStatus(userId, raw[0], result);
	        dataHelper.Close();

			
			return result;
		}
		
	    protected void onPostExecute(String result) {
    		Toast.makeText(fragmentActivity, result, Toast.LENGTH_SHORT).show();
	    }		
	}
	
	

}