package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.AndroidHelper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

public class TagActivity extends Activity implements RequestListener{
	
	private GridView favorGridview;
	private GridView dislikeGridview;
	private EditText newLike;
	private EditText newDislike;
	private String userId;
	private List<Tag> likeTags;
	private List<Tag> dislikeTags;
	private static final List<String> categories=Arrays.asList("美食", "电影", "财经","互联网","星座","文学","扯淡");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
		
		View layout=findViewById(R.id.layout_tag);
		AndroidHelper.AutoBackground(this, layout, R.drawable.app_bg_v, R.drawable.app_bg_h);
		userId=AccessTokenKeeper.readUserId(TagActivity.this);

		
		
        //get user tags from db
        DataHelper dataHelper=new DataHelper(TagActivity.this);
        likeTags=dataHelper.getUserTags(userId, Tag.FAVOR);
        dislikeTags=dataHelper.getUserTags(userId, Tag.DISLIKE);
        dataHelper.close();
        
        
        if(likeTags==null)
        	likeTags=new ArrayList<Tag>();
        if(dislikeTags==null)
        	dislikeTags=new ArrayList<Tag>();
		
        newLike=(EditText)findViewById(R.id.input_like);
        newDislike=(EditText)findViewById(R.id.input_dislike);
        
		
		favorGridview = (GridView) findViewById(R.id.favor);
		favorGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		favorGridview.setAdapter(new GridAdapter(TagActivity.this,Tag.FAVOR));
		
		
		dislikeGridview = (GridView) findViewById(R.id.dislike);
		dislikeGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		dislikeGridview.setAdapter(new GridAdapter(TagActivity.this,Tag.DISLIKE));
		
		//request user tags
		if(!AccessTokenKeeper.isConfigured(this)){
			OAuth2 oauth2=new OAuth2(TagActivity.this);
			oauth2.requestUserTags(this);
			AccessTokenKeeper.setFisrtTimeConfigured(this);
		}

        
		ImageButton btnAddLike=(ImageButton)findViewById(R.id.add_like);
		btnAddLike.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String newTag=newLike.getText().toString();
				if(newTag!=""){
					Tag tag=new Tag();
					tag.setUserId(userId);
					tag.setTagName(newTag);
					tag.setType(Tag.FAVOR);
					tag.setTime((new Date()).getTime());
					tag.setSelected(true);
					
					if(likeTags==null)
						likeTags=new ArrayList<Tag>();
					likeTags.add(tag);
					
					GridAdapter adapter=(GridAdapter)favorGridview.getAdapter();
					adapter.notifyDataSetChanged();
					newLike.setText("");
					
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(newLike.getWindowToken(), 0);					
				}
			}
		});
		
		ImageButton btnAddDislike=(ImageButton)findViewById(R.id.add_dislike);
		btnAddDislike.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String newTag=newDislike.getText().toString();
				if(newTag!=""){
					Tag tag=new Tag();
					tag.setUserId(userId);
					tag.setTagName(newTag);
					tag.setType(Tag.DISLIKE);
					tag.setTime((new Date()).getTime());
					tag.setSelected(true);
					
					if(dislikeTags==null)
						dislikeTags=new ArrayList<Tag>();
					dislikeTags.add(tag);
					
					GridAdapter adapter=(GridAdapter)dislikeGridview.getAdapter();
					adapter.notifyDataSetChanged();
					newDislike.setText("");
					
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(newDislike.getWindowToken(), 0);
				}				
			}
		});
		
		//click button save current tags in db
		ImageButton btnSave=(ImageButton)findViewById(R.id.save_tags);
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				DataHelper dataHelper=new DataHelper(TagActivity.this);
				
				//clear previous userTag
				dataHelper.clearAllTags();
				
				//add checked item to database
				for(int i=0;i<favorGridview.getCount();i++){
					
					boolean isChecked=favorGridview.getCheckedItemPositions().get(i);
					
					TagView item=(TagView)favorGridview.getChildAt(i).findViewById(R.id.tag_name);
					String tagName=item.getText().toString();
					isChecked=item.isChecked();
					Log.i("is Checked", "Position "+i+":"+isChecked);
					
					if(isChecked){
						if(userId==""){
						//TODO:
						}
						dataHelper.addTag(userId,tagName,Tag.FAVOR);	
					}
				}
				
				for(int i=0;i<dislikeGridview.getCount();i++){
					
					boolean isChecked=dislikeGridview.getCheckedItemPositions().get(i);
					
					TagView item=(TagView)dislikeGridview.getChildAt(i).findViewById(R.id.tag_name);
					String tagName=item.getText().toString();
					isChecked=item.isChecked();
					Log.i("is Checked", "Position "+i+":"+isChecked);
					
					if(isChecked){
						if(userId==""){
						//TODO:
						}
						dataHelper.addTag(userId,tagName,Tag.DISLIKE);	
					}
				}
				dataHelper.close();
				
	            Intent intent = new Intent();
	            intent.setClass(TagActivity.this, HomeActivity.class);
	            TagActivity.this.startActivity(intent);
			}
		});
	}
	
	
	public class GridAdapter extends BaseAdapter{
		private Context mContext;
		private List<Tag> tags;
		
		public GridAdapter(Context c,int type) {
	        mContext = c;
	        
	        if(type==Tag.FAVOR)
	        	tags=likeTags;
	        if(type==Tag.DISLIKE)
	        	tags=dislikeTags;
	        
		   int tagNum=tags.size();	

	        //add some default category
//	        if(Tag.FAVOR==type&&tagNum<4){
//	        	for(String cat:categories){
//	        		if(!tags.contains(cat))
//	        			tags.add(new Tag(cat,Tag.FAVOR));
//	        	}
//	        		
//	        }
	        
	        if(Tag.DISLIKE==type&&tagNum==0){
	        	tags.add(new Tag("中奖",Tag.DISLIKE));
	        	tags.add(new Tag("淘宝",Tag.DISLIKE));
	        	tags.add(new Tag("客户端",Tag.DISLIKE));
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
			Tag tag=tags.get(position);
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.tag,null);
			}
		
			tagText = (TagView) convertView.findViewById(R.id.tag_name);
			tagText.setText(tag.getTagName());
			tagText.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					
					TagView tagText = (TagView) v;
					tagText.toggle();
				}
			});

			
			if(tag.isSelected()){
				tagText.setChecked(true);
			}
			
			return convertView;
		}

	}


	@Override
	public void onComplete(String arg0) {
		List<Tag> weiboTag=OAuth2.parseTag(arg0);
		likeTags.addAll(weiboTag);
		HashSet<Tag> hs = new HashSet<Tag>();
		hs.addAll(likeTags);
		likeTags.clear();
		likeTags.addAll(hs);
		hs=null;
		
		this.runOnUiThread(new Runnable() {
			public void run() {
				GridAdapter adapter=(GridAdapter)favorGridview.getAdapter();
				adapter.notifyDataSetChanged();
			}
		});

	}


	@Override
	public void onError(WeiboException arg0) {
		Log.e("Weibo Status","Fail to get weibo, Status code: "+arg0.getStatusCode());
		String info;
		if(arg0.getStatusCode()==-1)
			info="无法连接服务器，请检查网络";
		else{
			info="错误："+arg0.getStatusCode()+","+arg0.getMessage();
		}
		
		final String message=info;
		this.runOnUiThread(new Runnable() {
			
		     public void run() {
		 		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		     }
		});		
	}


	@Override
	public void onIOException(IOException arg0) {
		arg0.printStackTrace();
	}


}
