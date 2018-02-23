package com.lf.view.tools;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobi.tool.MyR;

public class ProgressView extends RelativeLayout{
	
	
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public ProgressView(Context context) {
		super(context);
		initView();
	}

	private void initView(){
		View view=LayoutInflater.from(getContext()).inflate(
				MyR.layout(getContext(),"layout_progress_bar"),null);
		ImageView poiImgae=(ImageView)view.findViewById(
				MyR.id(getContext(),"progress_bar_positive"));
		ImageView negImgae=(ImageView)view.findViewById(
				MyR.id(getContext(),"progress_bar_negative"));
		poiImgae.startAnimation(AnimationUtils.loadAnimation(
				getContext(), MyR.anim(getContext(),"roate_positive")));
		negImgae.startAnimation(AnimationUtils.loadAnimation(
				getContext(), MyR.anim(getContext(),"roate_negative")));
		addView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
	}
}
