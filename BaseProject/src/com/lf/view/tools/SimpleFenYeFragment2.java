package com.lf.view.tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lf.base.R;
import com.lf.view.tools.FenYeAdapter.OnLoadListener;
import com.lf.view.tools.SimpleFenYeAdapter.ViewHolder;

import java.util.ArrayList;


/**
 * 分页加载界面
 * @author wangwei
 *
 */
public abstract class SimpleFenYeFragment2<T> extends SimpleFenYeFragment<T> implements OnRefreshListener, OnLoadListener, OnItemClickListener{



	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getContext(), R.layout.base_layout_simple_fenye, null);
	}

	@Override
	public ListView getListView() {
		return (ListView) getView().findViewById(R.id.simple_fenye_list);
	}

	@Override
	public SwipeRefreshLayout getSwipeRefreshLayout() {
		return (SwipeRefreshLayout)getView();
	}
	
	
	/**
	 * 基于Bean2View的通用view holder
	 * @author wangwei
	 *
	 */
	public class SimpleViewHolder extends ViewHolder<T>
	{

		ArrayList<View> mHolderViews;
		private int mLayoutId;
		private Class<T> mClass;
		private View mConvertView;
		
		public SimpleViewHolder(int layoutId, Class<T> tClass)
		{
			mLayoutId = layoutId;
			mClass = tClass;
		}

		@Override
		public View initView() {
			//将需要赋值的view放入mHolderViews
			View convertView = View.inflate(getContext(), mLayoutId, null);
			mHolderViews = Bean2View.holdViews(mClass, (ViewGroup)convertView);
			mConvertView = convertView;
			return convertView;
		}

		@Override
		public void initData(T t) {
			//给mHolderViews中的view赋值
			Bean2View.showViews(getContext(), t, mHolderViews);
		}
	}
}
