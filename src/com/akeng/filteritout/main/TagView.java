package com.akeng.filteritout.main;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.TextView;

import com.akeng.filteritout.entity.Tag;
import com.akeng.filteritout.util.DataHelper;

public class TagView extends TextView implements Checkable{

	private boolean isChecked;
	public TagView(Context context) {
		super(context);
		init();
	}
	
	public TagView(Context context,AttributeSet attr){
		super(context,attr);
		init();
	}
	
	public void init(){
		isChecked=false;
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked=checked;
	}

	@Override
	public void toggle() {
		isChecked = !isChecked;
		
		DataHelper dataHelper=new DataHelper(this.getContext());
		if(isChecked)
			dataHelper.addTag("1", "TestTag",Tag.FAVOR);
		else
			dataHelper.removeTag("1", "TestTag",Tag.FAVOR);
		
		dataHelper.Close();
		Log.i("--Toggle--", "isChecked "+isChecked());
	}
	
	//add checkState into drawable state
	protected int[] onCreateDrawableState(int extraSpace) {
	    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
	    
	    int[] CheckedStateSet = {android.R.attr.state_checked};
	    if (isChecked()) {
	        mergeDrawableStates(drawableState, CheckedStateSet);
	    }
	    return drawableState;
	}

}
