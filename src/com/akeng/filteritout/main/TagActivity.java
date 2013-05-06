package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.AndroidHelper;
import com.akeng.filteritout.util.DataHelper;

public class TagActivity extends Activity{
	
	private GridView tagGridview;
	private String userId;
	private static final List<String> categories=Arrays.asList("美食", "电影", "财经","互联网","星座","文学","扯淡");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		
		View layout=findViewById(R.id.layout_tag);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);
		userId=AccessTokenKeeper.readUserId(TagActivity.this);

		
		tagGridview = (GridView) findViewById(R.id.taggridview);
		tagGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		tagGridview.setAdapter(new GridAdapter(TagActivity.this));
		//TODO: that's interting
//		tagGridview.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position,
//					long id) {
//				TagView tagText = (TagView) v.findViewById(R.id.tag_name);
//				tagText.toggle();
//			}
//			
//		});
		
		//click button save current tags in db
		ImageButton btnSave=(ImageButton)findViewById(R.id.save_tags);
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				DataHelper dataHelper=new DataHelper(TagActivity.this);
				
				//clear previous userTag
				dataHelper.clearAllTags();
				
				//add checked item to database
				for(int i=0;i<tagGridview.getCount();i++){
					
					boolean isChecked=tagGridview.getCheckedItemPositions().get(i);
					
					if(isChecked){
						View item=tagGridview.getChildAt(i);
						String tagName=((TextView)item.findViewById(R.id.tag_name)).getText().toString();
						if(userId==""){
						//TODO:
						}
						dataHelper.addTag(userId,tagName,Tag.FAVOR);	
					}
					
				}
				dataHelper.Close();
				
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
		private List<String> tags;
		private int tagNum=0; //record origin user tag number
		
		public GridAdapter(Context c) {
	        mContext = c;
	        
	        tags=new ArrayList<String>();
	        
	        //get user tags from db
	        DataHelper dataHelper=new DataHelper(TagActivity.this);
	        List<Tag> userTags=dataHelper.getUserTags(userId, Tag.FAVOR);
	        dataHelper.Close();
	        
	        tagNum=userTags.size();
	        Log.i("tag num", "Num "+tagNum);
	        
	        for(Tag tag:userTags){
	        	tags.add(tag.getTagName());
	        }
	        
	        //add some default category
	        if(tagNum<7){
	        	for(String cat:categories){
	        		if(!tags.contains(cat))
	        			tags.add(cat);
	        	}
	        		
	        }
	        
	        		
	    }
		
		@Override
		public int getCount() {
			return tags.size();
		}

		@Override
		public Object getItem(int arg0) {
			return tags.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override //no scroll, only call once for each item
		public View getView(int position, View convertView, ViewGroup parent) {
			TagView tagText;
			
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.tag,null);
			}
		
			tagText = (TagView) convertView.findViewById(R.id.tag_name);
			tagText.setText(tags.get(position));
			tagText.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					TagView tagText = (TagView) v;
					tagText.toggle();
				}
				
			});

			
			if(position<=tagNum-1){
				tagText.setChecked(true);
			}
			
			return convertView;
		}

	}


}
