package com.lf.view.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lf.view.tools.ScreenParameter;


/**
 * 在沉浸模式的时候，放在界面最顶部的view
 * @author wangwei
 *
 */
public class StatusBar extends View{

	public StatusBar(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (VERSION.SDK_INT >= 19) {
			((Activity)getContext()).getWindow().addFlags(67108864);
//			((Activity)getContext()).getWindow().addFlags(134217728);
			ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)getLayoutParams();
			lp.height = ScreenParameter.getNotificationBarHeight(getContext());
			setLayoutParams(lp);
//			setFitsSystemWindows(true);
		}
		else
		{
			setVisibility(View.GONE);
		}
	}

}
