package com.akeng.filteritout.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.akeng.filteritout.R;
import com.akeng.filteritout.entity.RecommendParam;
import com.akeng.filteritout.entity.Status;
import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.AccessTokenKeeper;
import com.akeng.filteritout.util.DataHelper;
import com.akeng.filteritout.util.OAuth2;
import com.akeng.filteritout.util.StatusComparator;
import com.akeng.filteritout.util.WeiboAnalyzer;

public class RecommendTask extends AsyncTask<RecommendParam, Void, List<Status>> {
	HomeActivity activity;
	List<com.akeng.filteritout.entity.Status> newList;
	List<String> likeTags;
	List<String> dislikeTags;
	int section;
	int filtered;
	
	public RecommendTask(HomeActivity activity){
		this.activity=activity;
		this.filtered=0;
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

		newList = param[0].getStatus();
		section = param[0].getSection();

		// get model
		DataHelper dataHelper = new DataHelper(activity);
		List<Map<String, Integer>> likeList = dataHelper.getModels(Tag.FAVOR);
		List<Map<String, Integer>> dislikeList = dataHelper.getModels(Tag.DISLIKE);
		dataHelper.close();

		//get tags
		likeTags = getUserTag(Tag.FAVOR);
		dislikeTags = getUserTag(Tag.DISLIKE);

		filterDislike(newList);

		try {
			for (int i = 0; i < newList.size(); i++) {

				com.akeng.filteritout.entity.Status status = newList.get(i);
				status.setSection(section);
				String content = WeiboAnalyzer.cleanUpText(status.getText());

				// filter statuses that are too short
				if (content.length() < 40) {
					if (section == HomeActivity.SECTION_RECOMMENDS) {
						newList.remove(i);
						i--;
					}
					continue;
				}

				Map<String, Integer> candidateMap = WeiboAnalyzer.splitStatus(content);
			
				//similarity with dislike status	
				double dislikeWeight=0;
				if (dislikeList != null) 
					dislikeWeight = getWeight(dislikeList, candidateMap);

				if (dislikeWeight > 0.1) {
					Log.e("dislike weight>0.1", content);
					newList.remove(i);
					i--;
					this.filtered++;
					continue;
				}

				double likeWeight = 0;
				if (likeList != null)
					likeWeight = getWeight(likeList, candidateMap);

				if (section == HomeActivity.SECTION_RECOMMENDS) {
					if (likeWeight < 0.1) {

						if (containTag(likeTags, content)) {
							likeWeight = 0.2;
						} else {
							Log.e("like weight<0.1", content);
							 newList.remove(i);
							 i--;
							 continue;
						}

					}
				}

				status.setWeight(likeWeight);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(newList,new StatusComparator());
		return newList;
	}
	
	private boolean containTag(List<String> taglist,String text){
		boolean isContain=false;
		
		if(taglist==null)
			return false;
		
		for(String tag:taglist){
			if(text.contains(tag)){
				System.out.println("contain tag:"+tag);
				isContain=true;
			}
		}
		
		return isContain;
	}
	
	private void filterDislike(List<com.akeng.filteritout.entity.Status> statusList){
		
		for(int i=0;i< statusList.size();i++){
			String text=WeiboAnalyzer.cleanUpText(statusList.get(i).getText());
				if(containTag(dislikeTags,text)){
					statusList.remove(i);
					i--;
					System.out.println("filtered dislike tag "+text);
					this.filtered++;
				}
		}
	}
	
	private double getWeight(List<Map<String, Integer>> modelList,Map<String, Integer> candidate){
		double statusWeight = 0.0;
		for (Map<String, Integer> model : modelList) {
			double sim=getSimilarity(candidate, model);
			statusWeight = statusWeight>sim? statusWeight:sim;
		}
		System.out.println(statusWeight);
		return statusWeight;
	}
	
	private double getSimilarity(Map<String,Integer> model,Map<String,Integer> candidate){
		Set<String> keys=new HashSet<String>(model.keySet());
		Set<String> candidateKey=candidate.keySet();
		keys.addAll(candidateKey);
		
		Iterator<String> it=keys.iterator();
		int product=0;
		double lenthModel=0;
		double lenthCandidate=0;
		while(it.hasNext()){
			String keyword=it.next();
			
			//lenth
			if(model.get(keyword)!=null)
				lenthModel+=Math.pow(model.get(keyword),2);
			
			if(candidate.get(keyword)!=null)
				lenthCandidate+=Math.pow(candidate.get(keyword),2);

			//calcualte product
			if(model.get(keyword) != null&&candidate.get(keyword)!=null)
				product=product+model.get(keyword)*candidate.get(keyword);
		}
		lenthModel=Math.sqrt(lenthModel);
		lenthCandidate=Math.sqrt(lenthCandidate);
		
		return product/(lenthModel*lenthCandidate);
	}
	
	private List<String> getUserTag(int type){
		
        DataHelper dataHelper=new DataHelper(activity);
        List<Tag> tags=dataHelper.getUserTags(AccessTokenKeeper.readUserId(activity), type);
        dataHelper.close();
        
        if(tags==null)
        	return null;
        
        List<String> tagNames=new ArrayList<String>();
        for(Tag tag:tags){
        	tagNames.add(tag.getTagName());
        }
        
        return tagNames;
	}
	
	/**
	 * Add filtered new status list to the display list
	 * @param statusList
	 * @param newList
	 */
	private void addToList(List<com.akeng.filteritout.entity.Status> statusList, List<com.akeng.filteritout.entity.Status> newList) {
		if (newList.size() > 0) {
			// returned first id < OAuth2.maxId, earlier status
			int index = newList.get(0).getId()< OAuth2.maxId ? statusList.size() : 0;
			statusList.addAll(index, newList);
		}
	}
	
	/**
	 * Update Section
	 */
	protected void onPostExecute(
			List<com.akeng.filteritout.entity.Status> filteredList) {
		
		//update weibo section
		
		if (section == HomeActivity.SECTION_FRIENDS) {
			addToList(HomeActivity.friendStatusList, filteredList);

		} else if (section == HomeActivity.SECTION_RECOMMENDS) {
			addToList(HomeActivity.publicStatusList, filteredList);
		}

		// notify weibosection to update views
		activity.updateSection(section);
		

		// toast shows the new loaded number
		
		String noStatus="";
		String filteredNum="";
		
		if (section == HomeActivity.SECTION_FRIENDS) {
			noStatus = activity.getString(R.string.no_more_new);
			filteredNum = filtered
					+ activity.getString(R.string.filtered_statuses);
		} else if (section == HomeActivity.SECTION_RECOMMENDS) {
			noStatus = activity.getString(R.string.no_more_recommend);
		}
		
		if (newList.size() == 0){
			Toast.makeText(activity.getApplication(),noStatus+" "+filteredNum,
					Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(activity.getApplication(),
					newList.size() + activity.getString(R.string.new_statuses)+","+filteredNum,
					Toast.LENGTH_SHORT).show();
			}

	}	
	

}
