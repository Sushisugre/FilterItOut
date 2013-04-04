package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;


public class WeiboSectionFragment extends Fragment{
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private List<Status> statusList;
	private ListView statusListView;
	
	public WeiboSectionFragment() {
		statusList=new ArrayList<Status>( );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.weibo_section, container, false);
		statusListView = (ListView) v.findViewById(R.id.Msglist);
		int section = this.getArguments().getInt(
				HomeActivity.ARG_SECTION_NUMBER);
		StatusAdapter statusAdapter = new StatusAdapter(section);
		statusListView.setAdapter(statusAdapter);

		return v;
	}
	
	 

	public void onUpdateContent() {
		Log.e("Update-List", "--------Test-------");

		int section = this.getArguments().getInt(
				HomeActivity.ARG_SECTION_NUMBER);

		if (section == HomeActivity.SECTION_FRIENDS)
			statusList = HomeActivity.friendStatusList;
		if (section == HomeActivity.SECTION_RECOMMENDS)
			statusList = HomeActivity.publicStatusList;
		
		StatusAdapter adapter=(StatusAdapter)statusListView.getAdapter();
				adapter.notifyDataSetChanged();

	}
	

	public class StatusAdapter extends BaseAdapter {

		public StatusAdapter(int type) {
		Log.e("Create Status Adapter", "---Create Adapter---");

		}

		@Override
		public int getCount() {
			Log.e("Get Count", "View Count: "+statusList.size());
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
			Log.i("Get-View", "This is position:" + position);

			ImageView userProfileImage;
			TextView username;
			TextView statusText;
			TextView statusTime;
			ImageView statusImage;

			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.weibo,
						null);
				userProfileImage = (ImageView) convertView
						.findViewById(R.id.wbicon);
				username = (TextView) convertView.findViewById(R.id.wbuser);
				statusText = (TextView) convertView.findViewById(R.id.wbtext);
				statusTime = (TextView) convertView.findViewById(R.id.wbtime);
				statusImage = (ImageView) convertView
						.findViewById(R.id.wbimage);

				StatusHolder viewHolder = new StatusHolder();
				viewHolder.setUsername(username);
				viewHolder.setUserProfileImage(userProfileImage);
				viewHolder.setStatusText(statusText);
				viewHolder.setStatusTime(statusTime);
				viewHolder.setStatusImage(statusImage);
				convertView.setTag(viewHolder);
			} else {
				StatusHolder viewHolder = (StatusHolder) convertView.getTag();
				userProfileImage = viewHolder.getUserProfileImage();
				username = viewHolder.getUsername();
				statusText = viewHolder.getStatusText();
				statusTime = viewHolder.statusTime;
				statusImage = viewHolder.getStatusImage();
			}

			Status status = (Status) getItem(position);
			username.setText(status.getUsername());
			statusText.setText(status.getText());
			statusTime.setText(status.getTime());

			return convertView;
		}

		private class StatusHolder {

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

			public ImageView getUserProfileImage() {
				return userProfileImage;
			}

			public TextView getUsername() {
				return username;
			}

			public TextView getStatusText() {
				return statusText;
			}

			public TextView getStatusTime() {
				return statusTime;
			}

			public ImageView getStatusImage() {
				return statusImage;
			}

		}
	}
}