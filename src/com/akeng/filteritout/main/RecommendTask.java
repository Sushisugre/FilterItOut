package com.akeng.filteritout.main;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.RecommendParam;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.akeng.filteritout.util.WeiboAnalyzer;

public class RecommendTask extends AsyncTask<RecommendParam, Void, List<Status>> {
	HomeActivity activity;
	List<com.akeng.filteritout.entity.Status> newList;
	int section;
	int filtered;
	
	public RecommendTask(HomeActivity activity){
		this.activity=activity;
	}

    void detach() {
        activity=null;
      }
      
      void attach(HomeActivity activity) {
        this.activity=activity;
      }

	@Override
	protected List<com.akeng.filteritout.entity.Status> doInBackground(
			RecommendParam... param) {

		System.out.println("Recommend Task");
		newList=param[0].getStatus();
		section=param[0].getSection();
		
		//TODO:Filtering
		filterDislike();
		
		
		return newList;
	}
	
	private void filterDislike(){
		
		List<String> dislike=getUserTag(Tag.DISLIKE);
		if(dislike==null)
			return;
		
		for(int i=0;i< newList.size();i++){
			String text=WeiboAnalyzer.cleanUpText(newList.get(i).getText());
			for(String tag:dislike){
				if(text.contains(tag)){
					newList.remove(i);
				}
			}
		}
	}
	
	private List<String> getUserTag(int type){
		
        DataHelper dataHelper=new DataHelper(activity);
        List<Tag> tags=dataHelper.getUserTags(AccessTokenKeeper.readUserId(activity), type);
        dataHelper.Close();
        
        if(tags==null)
        	return null;
        
        List<String> tagNames=new ArrayList<String>();
        for(Tag tag:tags){
        	tagNames.add(tag.getTagName());
        }
        
        return tagNames;
	}
	
	protected void onPostExecute(
			List<com.akeng.filteritout.entity.Status> filteredList) {
		
		//update weibo section
		
		if (section == HomeActivity.SECTION_FRIENDS) {
			HomeActivity.addToList(HomeActivity.friendStatusList, filteredList);
			if (newList.size() > 0) {
				OAuth2.sinceId = OAuth2.sinceId > newList.get(0).getId() ? OAuth2.sinceId
						: newList.get(0).getId();
				OAuth2.maxId = OAuth2.maxId < newList.get(newList.size() - 1)
						.getId() - 1 ? OAuth2.maxId : newList.get( newList.size() - 1).getId() - 1;
			}
		} else if (section == HomeActivity.SECTION_RECOMMENDS) {
			HomeActivity.addToList(HomeActivity.publicStatusList, filteredList);
		}

		// notify weibosection to update views
		activity.updateSection(section);

		// toast shows the new loaded number
		if (newList.size() == 0)
			Toast.makeText(activity.getApplication(),
					activity.getString(R.string.no_more_new),
					Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(activity.getApplication(),
					newList.size() + activity.getString(R.string.new_statuses),
					Toast.LENGTH_SHORT).show();

	}	
	

}
