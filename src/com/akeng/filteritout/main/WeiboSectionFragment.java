package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.util.OAuth2;


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
			OAuth2.sinceId=statusList.get(0).getId();
			OAuth2.maxId=statusList.get(statusList.size()-1).getId()-1;
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
						
						//TODO
						Intent intent=new Intent(v.getContext(),TextAnalysisService.class);
						intent.putExtra("text", status.getText());
						intent.putExtra("type", 0);
						v.getContext().startService(intent);
					}});
				
				btnDelete.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						
						Status status=statusList.get(statusPosition);
						status.setDeleted(true);
						
						
						//TODO : save to database
						
						Intent intent=new Intent(v.getContext(),TextAnalysisService.class);
						intent.putExtra("text", status.getText());
						intent.putExtra("type", 1);
						v.getContext().startService(intent);
						
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
	
	

}