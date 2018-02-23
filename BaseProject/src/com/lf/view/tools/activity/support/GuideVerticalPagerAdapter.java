package com.lf.view.tools.activity.support;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lf.view.tools.imagecache.MyImageView;

public class GuideVerticalPagerAdapter extends VerticalPagerAdapter{
	
	private String[] mDatas;
	
	private Context mContext;
	
	private List<MyImageView> mViewsList;
	
	public GuideVerticalPagerAdapter(Context context,List<MyImageView> views, String[] datas){
		mContext = context;
		mDatas = datas;
		mViewsList = views;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		MyImageView imageView = mViewsList.get(position);
		imageView.setImagePath(mDatas[position]);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setShowAnim(false);
		container.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return imageView;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((VerticalViewPager) container).removeView(mViewsList.get(position));
	}
	
	@Override
	public int getItemPosition(Object object) {
		
		return POSITION_NONE;
	}
}
