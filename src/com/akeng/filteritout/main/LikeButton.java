package com.akeng.filteritout.main;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.ImageView;

public class LikeButton extends ImageView  implements Checkable{

	private boolean isChecked=false;

	public LikeButton(Context context){
		super(context);
	}
	
	public LikeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		this.isChecked=checked;
	}

	@Override
	public void toggle() {
		isChecked = !isChecked;
		Log.i("Toggle", "Like button is Checked: "+isChecked);
	}
	
	//add checkState into drawable state
	public int[] onCreateDrawableState(int extraSpace) {
	    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
	    
	    int[] CheckedStateSet = {android.R.attr.state_checked};
	    if (isChecked()) {
	        mergeDrawableStates(drawableState, CheckedStateSet);
	    }
	    return drawableState;
	}

}
