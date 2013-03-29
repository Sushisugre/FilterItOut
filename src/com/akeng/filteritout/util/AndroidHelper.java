package com.akeng.filteritout.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class AndroidHelper {

   //get screen orient
	public static int ScreenOrient(Activity activity)
	    {
	        int orient = activity.getRequestedOrientation(); 
	        if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
	            //width>height,horizontal  
	             WindowManager windowManager = activity.getWindowManager();  
	             Display display = windowManager.getDefaultDisplay();  
	             //TODO:change deprecated method
	             int screenWidth  = display.getWidth();  
	             int screenHeight = display.getHeight();  
	             orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	        }
	        return orient;
	    }
	
	public static void AutoBackground(Activity activity,View view,int Background_v, int Background_h)
    {
        int orient=ScreenOrient(activity);
        if (orient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //vertical
            view.setBackgroundResource(Background_v);
        }else{ //horizontal
            view.setBackgroundResource(Background_h);
        }  
    }
}
