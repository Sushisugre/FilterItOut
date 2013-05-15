package com.akeng.filteritout.main;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.akeng.filteritout.util.WeiboAnalyzer;

public class TextAnalysisService extends IntentService {
	Handler mMainThreadHandler = null;
	
	public TextAnalysisService() {
		super("TextAnalysisService");
		 mMainThreadHandler = new Handler();
	}
	
	public TextAnalysisService(String name) {
		super(name);
		 mMainThreadHandler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		System.out.println("Text Analysis Service");
		//test split word
		try{
		final String tokens=WeiboAnalyzer.splitStatus(arg0.getStringExtra("text"));
		
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
        		Toast.makeText(getApplicationContext(), tokens, Toast.LENGTH_SHORT).show();
            }
        });
		}
		    
		catch(IOException e){
			Log.e("WeiboAnalyzer", e.getMessage());
		}
		
	}

}
