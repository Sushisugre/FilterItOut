package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.akeng.filteritout.R;
import com.akeng.filteritout.util.AndroidHelper;

public class TagActivity extends Activity{
	
	private GridView tagGridview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		
		View layout=findViewById(R.id.layout_tag);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);

		
		tagGridview = (GridView) findViewById(R.id.taggridview);
		tagGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		tagGridview.setAdapter(new GridAdapter(TagActivity.this));
		tagGridview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Log.i("OnItemClick!", "position"+position);
				TagView tagText = (TagView) v.findViewById(R.id.tag_name);
				tagText.toggle();
				
				for(int i=0;i<7;i++)
					Log.i("Checked iTem", "position "+i+" "+tagGridview.getCheckedItemPositions().get(i));

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
			return categories.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TagView tagText;
			
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.tag,null);
			}
		
			tagText = (TagView) convertView.findViewById(R.id.tag_name);
			tagText.setText(categories.get(position));
			
			return convertView;
		}

	}


}
