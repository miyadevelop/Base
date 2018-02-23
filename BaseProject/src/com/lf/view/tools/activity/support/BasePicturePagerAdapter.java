package com.lf.view.tools.activity.support;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * 图片适配器
 * @author zzq
 *
 */
public class BasePicturePagerAdapter extends PagerAdapter {

	public List<ImageView> mListViews;
	public List<String> mPicturePaths;
	private Context mContext;
	public BasePicturePagerAdapter(List<ImageView> mListViews, List<String> picturePaths, Context context) {
		this.mListViews = mListViews;
		this.mPicturePaths = picturePaths;
		mContext = context;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView myImageView = mListViews.get(position);
		Glide.with(mContext).load(mPicturePaths.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(myImageView);
		myImageView.setOnClickListener(onClickListener);
		container.addView(myImageView);
		return myImageView;
	}
	
	@Override
	public int getCount() {
		return mListViews.size(); 
	}
	
	@Override
	public int getItemPosition(Object object) {
		
		return POSITION_NONE;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0==arg1;
	}
	
	
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Activity activity = (Activity) mContext;
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				activity.finishAfterTransition();
//			}
//			else
			{
				activity.finish();
				activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}
	};
	
	
}
