package com.lf.view.tools.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.lf.app.App;
import com.lf.base.R;
import com.lf.view.tools.MyImageButton;

import java.util.ArrayList;
import java.util.List;


/**
 * ViewPager+Fragment+Tab，常规的主页框架
 * @author wangwei
 *
 */
public abstract class TabFragment extends Fragment implements OnItemClickListener, OnPageChangeListener{

	/**
	 * 一页数据
	 * @author wangwei
	 *
	 */
	public class Page
	{
		public Fragment mFragment;
		public String mImage;
		public String mText;
	}

	public ArrayList<Page> mPages;//所有页面


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_layout_tab, null);
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mPages = new ArrayList<Page>();
		//初始化页面
		initPage();

		//初始化底部Tab
		GridView tab = getTab();
		tab.setChoiceMode(1);//只能选中一项
		TabAdapter mGroupAdapter = new TabAdapter(getContext(), mPages);
		tab.setAdapter(mGroupAdapter);
		tab.setOnItemClickListener(this);
		tab.setSelection(0);
		tab.setNumColumns(mPages.size());
		tab.setItemChecked(0, true);
		tab.setFadingEdgeLength(0);
		tab.setSelector(new ColorDrawable(Color.TRANSPARENT));

		//初始化ViewPager
		ViewPager viewPager = getViewPager();
		viewPager.addOnPageChangeListener(this);
		viewPager.setAdapter(new MyFrageStatePagerAdapter(getChildFragmentManager()));
		viewPager.setCurrentItem(0);
		viewPager.setOffscreenPageLimit(3);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		getViewPager().setCurrentItem(position);
	}


	/**
	 * 添加一页
	 * @param text
	 * @param image
	 * @param fragment
	 */
	protected void addPage(String text, String image, Fragment fragment)
	{
		Page page = new Page();
		page.mText = text;
		page.mImage = image;
		page.mFragment = fragment;
		mPages.add(page);
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	int mCurPosition = -1;
	@Override
	public void onPageSelected(int position) {

		//这里通过改变GridView的Selection和Checked来改变按钮的颜色，但这种方法仅在targetSdkVersion为11以上才有效
		GridView tab = getTab();
		tab.setSelection(position);
		tab.setItemChecked(position, true);
		mCurPosition = position;
		
		mPages.get(position).mFragment.onResume();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		mPages.clear();
	}


	/**
	 * 底部Tab的Adapter
	 * @author wangwei
	 *
	 */
	public class TabAdapter extends ArrayAdapter<Page>{

		public TabAdapter(Context context, List<Page> objects) {
			super(context, 0, objects);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(null == convertView)
			{
				convertView = getTabItemView();
				viewHolder = new ViewHolder();
				viewHolder.mTextView = (TextView)convertView.findViewById(App.id("tab_text"));
				viewHolder.mImageView = (MyImageButton)convertView.findViewById(App.id("tab_image"));
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			if(null != viewHolder.mTextView && null != getItem(position).mText && getItem(position).mText.length() > 0)
				viewHolder.mTextView.setText(getItem(position).mText);
			if(null != viewHolder.mImageView && null != getItem(position).mImage && getItem(position).mImage.length() > 0)
				viewHolder.mImageView.setImageDrawable(App.drawable(getItem(position).mImage));
			return convertView;
		}


		public class ViewHolder
		{
			public TextView mTextView;
			public MyImageButton mImageView;
		}
	}



	/** 
	 * 定义自己的ViewPager适配器。 
	 * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。 
	 */  
	class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter  
	{  

		public MyFrageStatePagerAdapter(FragmentManager fm)   
		{  
			super(fm);  
		}  

		@Override  
		public Fragment getItem(int position) {  
			return mPages.get(position).mFragment;  
		}  

		@Override  
		public int getCount() {  
			return mPages.size();  
		}  

	}  

	protected abstract void initPage();


	//	protected abstract View getContentView();
	//	
	//	
	//	protected abstract ViewPager getViewPager();
	//	
	//	
	//	protected abstract GridView getTab();
	//	
	//	
	//	protected abstract View getTabItemView();



	public ViewPager getViewPager()
	{

		return (ViewPager)getView().findViewById(App.id("tab_viewpager"));
	}


	public GridView getTab()
	{
		return (GridView)getView().findViewById(App.id("tab_grid_tab"));
	}


	protected View getTabItemView()
	{
		return View.inflate(getContext(), App.layout("base_item_tab_2"), null);
	}

}
