package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.akeng.filteritout.R;
import com.akeng.filteritout.util.AndroidHelper;

public class TagActivity extends Activity implements MultiChoiceModeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		
		View layout=findViewById(R.id.layout_tag);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);

		
		GridView tagGridview = (GridView) findViewById(R.id.taggridview);
		tagGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		tagGridview.setMultiChoiceModeListener(this);
		tagGridview.setAdapter(new GridAdapter(TagActivity.this));
		tagGridview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Log.i("OnItemClick!", "position"+position);
				TagView tagText = (TagView) v.findViewById(R.id.tag_name);
				tagText.toggle();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tag, menu);
		return true;
	}
	
	
	public class GridAdapter extends BaseAdapter{
		private Context mContext;
		private List<String> categories;
		
		public GridAdapter(Context c) {
	        mContext = c;
	        
	        categories=new ArrayList<String>();
	        categories.add("美食");
	        categories.add("电影");
	        categories.add("财经");
	        categories.add("互联网");
	        categories.add("星座");
	        categories.add("文学");
	        categories.add("汽车");
	        		
	    }
		
		@Override
		public int getCount() {
			return categories.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TagView tagText;
			CheckBox tagCheck;
			
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.tag,null);
			}
		
			tagText = (TagView) convertView.findViewById(R.id.tag_name);
			tagText.setText(categories.get(position));
			tagCheck = (CheckBox) convertView.findViewById(R.id.tag_check);
			
			return convertView;
		}

	}


	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		 // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.activity_tag, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// TODO Auto-generated method stub
		
	}
	

}
