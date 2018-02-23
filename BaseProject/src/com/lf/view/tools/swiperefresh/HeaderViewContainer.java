package com.lf.view.tools.swiperefresh;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

/**
 * 下拉刷新布局头部的容器
 * @author ludeyuan
 *
 */
public class HeaderViewContainer extends RelativeLayout{

	private AnimationListener mListener;

	public HeaderViewContainer(Context context) {
		super(context);
	}

	public void setAnimationListener(Animation.AnimationListener listener) {
		mListener = listener;
	}

	@Override
	public void onAnimationStart() {
		super.onAnimationStart();
		if (mListener != null) {
			mListener.onAnimationStart(getAnimation());
		}
	}

	@Override
	public void onAnimationEnd() {
		super.onAnimationEnd();
		if (mListener != null) {
			mListener.onAnimationEnd(getAnimation());
		}
	}
}
