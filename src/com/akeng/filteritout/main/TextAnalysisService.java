package com.akeng.filteritout.main;

import java.io.IOException;

import com.akeng.filteritout.util.WeiboAnalyzer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TextAnalysisService extends IntentService {

	public TextAnalysisService() {
		super("TextAnalysisService");
	}
	
	public TextAnalysisService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		System.out.println("Text Analysis Service");
		//test split word
		try{
		WeiboAnalyzer.splitStatus(arg0.getStringExtra("text"));
		}
		catch(IOException e){
			Log.e("WeiboAnalyzer", e.getMessage());
		}
		
	}

}
