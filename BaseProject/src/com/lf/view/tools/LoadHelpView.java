package com.lf.view.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lf.base.R;


/**
 * 反应加载状态的工具View，提供加载等待和加载失败的显示功能 及其 默认显示的样子
 * @author wangwei
 *
 */
public class LoadHelpView extends RelativeLayout{

	private View mWaitView;
	private View mFailedView;
	private View mNoDataView;
	public static final int STATUS_LOADING = 0; 
	public static final int STATUS_LOAD_FAILED = 1;
	public static final int STATUS_LOAD_OVER = 2;
	public static final int STATUS_NO_DATA = 3;//没有数据

	public LoadHelpView(Context context, AttributeSet attrs) {
		super(context, attrs);

		ImageView waitView = new ImageView(context);
		waitView.setImageResource(R.drawable.image_loading);
		waitView.setTag(getAnimation(0));
		setWaitView(waitView);

		ImageView failedView = new ImageView(context);
		failedView.setImageResource(R.drawable.image_load_failed);
		setFailedView(failedView);


		ImageView noDataView = new ImageView(context);
		noDataView.setImageResource(R.drawable.image_load_no_data);
		setNoDataView(noDataView);
	}


	public void setWaitView(View view)
	{
		if(null != mWaitView)
			removeView(mWaitView);
		mWaitView = view;
		LayoutParams lp = (LayoutParams)view.getLayoutParams();
		if(null == lp)//如果外界没定义LayoutParams，则赋值一个默认的
		{
			int width = UnitConvert.DpToPx(getContext(), 35);
			lp = new LayoutParams(width,width);
		}
		lp.addRule(CENTER_IN_PARENT);
		addView(view,lp);
	}


	public void setFailedView(View view)
	{
		if(null != mFailedView)
			removeView(mFailedView);
		mFailedView = view;

		LayoutParams lp = (LayoutParams)view.getLayoutParams();
		if(null == lp)//如果外界没定义LayoutParams，则赋值一个默认的
		{
			int width = UnitConvert.DpToPx(getContext(), 100);
			lp = new LayoutParams(width,width);
		}
		lp.addRule(CENTER_IN_PARENT);
		addView(view,lp);
	}


	public View getFailedView()
	{
		return mFailedView;
	}


	public void setNoDataView(View view)
	{
		if(null != mNoDataView)
			removeView(mNoDataView);
		mNoDataView = view;

		LayoutParams lp = (LayoutParams)view.getLayoutParams();
		if(null == lp)//如果外界没定义LayoutParams，则赋值一个默认的
		{
			int width = UnitConvert.DpToPx(getContext(), 100);
			lp = new LayoutParams(width,width);
		}
		lp.addRule(CENTER_IN_PARENT);
		addView(view,lp);
	}


	public View getNoDataView()
	{
		return mNoDataView;
	}

	public void setLoadingStatus(int status)
	{
		if(status == STATUS_LOADING)
		{
			if(null != mWaitView)
			{
				Animation animation = (Animation)mWaitView.getTag();
				if(null != animation)
					mWaitView.startAnimation(animation);
				mWaitView.setVisibility(View.VISIBLE);
			}
			if(null != mFailedView)
				mFailedView.setVisibility(View.GONE);

			if(null != mNoDataView)
				mNoDataView.setVisibility(View.GONE);
		}
		else if(status == STATUS_LOAD_FAILED)
		{
			if(null != mWaitView)
			{
				mWaitView.setVisibility(View.GONE);
				mWaitView.clearAnimation();
			}
			if(null != mFailedView)
				mFailedView.setVisibility(View.VISIBLE);

			if(null != mNoDataView)
				mNoDataView.setVisibility(View.GONE);
		}
		else if(status == STATUS_LOAD_OVER)
		{
			if(null != mWaitView)
			{
				mWaitView.setVisibility(View.GONE);
				mWaitView.clearAnimation();
			}
			if(null != mFailedView)
				mFailedView.setVisibility(View.GONE);

			if(null != mNoDataView)
				mNoDataView.setVisibility(View.GONE);
		}
		
		else if(status == STATUS_NO_DATA)
		{
			if(null != mWaitView)
			{
				mWaitView.setVisibility(View.GONE);
				mWaitView.clearAnimation();
			}
			if(null != mFailedView)
				mFailedView.setVisibility(View.GONE);

			if(null != mNoDataView)
				mNoDataView.setVisibility(View.VISIBLE);
		}
	}


	//	public void setLoader(FenYeLoader<?> loader)
	//	{
	//		mLoader = loader;
	//		IntentFilter filter = new IntentFilter();
	//		filter.addAction(mLoader.getAction());
	//		getContext().registerReceiver(mReceiver, filter);
	//	}
	//	
	//	
	//	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
	//
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//
	//			if(intent.getAction().equals(mLoader.getAction())){
	//
	//				boolean status = intent.getBooleanExtra(NetLoader.STATUS, false);
	//				if(mLoader.isReachBottom(mLoadParam))
	//				{
	//					mAdapter.setLoadType(FenYeAdapter.TYPE_LOADED);
	//				}
	//
	//				//bug:这里广播没有区分加载的参数，状态会出现不正确的情况
	//				if(status){	//从网络上加载数据成功
	//					mAdapter.notifyDataSetChanged();
	//				}else{	//从网络上加载数据失败
	//					mAdapter.setLoadType(FenYeAdapter.TYPE_LOAD_FAILD);
	//				}
	//			}
	//		}
	//	};


	private Animation getAnimation(int animationType)
	{
		Animation animation = null;
		if(animationType == 0)//顺时针旋转
		{
			animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setDuration(500);
			LinearInterpolator lir = new LinearInterpolator();//匀速旋转
			animation.setInterpolator(lir);
			animation.setFillAfter(false);
		}
		return animation;
	}

}
