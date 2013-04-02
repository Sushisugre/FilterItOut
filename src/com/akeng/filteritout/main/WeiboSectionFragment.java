package com.akeng.filteritout.main;

import java.io.IOException;
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
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class WeiboSectionFragment extends Fragment implements RequestListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private List<Status> statusList;
	
	public WeiboSectionFragment() {
		statusList=new ArrayList<Status>( );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.weibo_section, container, false);
		ListView statusList = (ListView) v.findViewById(R.id.Msglist);
		int section = this.getArguments().getInt(
				HomeActivity.ARG_SECTION_NUMBER);
		StatusAdapter statusAdapter = new StatusAdapter(section);
		// this.requestStatus();
		statusList.setAdapter(statusAdapter);

		return v;
	}

	public void onUpdateContent() {
		Log.e("Update-List", "--------Test-------");

	}
	
	@Override
	public void onComplete(String arg0){
		OAuth2.response=arg0;
		if(getArguments().getInt(HomeActivity.ARG_SECTION_NUMBER)==HomeActivity.SECTION_FRIENDS){
			statusList=OAuth2.parseResponse();
//			HomeActivity.friendStatusList=OAuth2.parseResponse();
//			Log.e("Status Number", ""+HomeActivity.friendStatusList.size());

		}
		else if(getArguments().getInt(HomeActivity.ARG_SECTION_NUMBER)==HomeActivity.SECTION_FRIENDS){
//			HomeActivity.publicStatusList=OAuth2.parseResponse();
//			Log.e("Status Number", ""+HomeActivity.publicStatusList.size());

			statusList=OAuth2.parseResponse();
		}
	}

	@Override
	public void onError(WeiboException arg0) {
		Log.e("Weibo Status","Fail to get weibo, Status code: "+arg0.getStatusCode());
		Toast.makeText(getActivity(), "获取微博失败："+arg0.getStatusCode(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}

	public class StatusAdapter extends BaseAdapter {

		private List<Status> list;

		public StatusAdapter(int type) {
			if (type == HomeActivity.SECTION_FRIENDS)
				list = HomeActivity.friendStatusList;
			if (type == HomeActivity.SECTION_RECOMMENDS)
				list = HomeActivity.publicStatusList;
		}

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