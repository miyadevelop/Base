package com.lf.view.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lf.controler.tools.download.helper.FenYeMapLoader2;
import com.lf.controler.tools.download.helper.LoadParam;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.view.tools.FenYeAdapter.OnLoadListener;
import com.lf.view.tools.SimpleFenYeAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 分页加载界面，封装了SwipeRefreshLayout + ListView + Adapter + 加载状态显示
 * @author wangwei
 *
 */
public abstract class SimpleFenYeFragment<T> extends Fragment implements OnRefreshListener, OnLoadListener, OnItemClickListener{


	private MySimpleFenYeAdapter mAdapter;
	protected LoadParam mLoadParam;


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		IntentFilter inflater = new IntentFilter();
		inflater.addAction(getLoader().getAction());
		getContext().registerReceiver(mReceiver, inflater);

		SwipeRefreshLayout swipe = getSwipeRefreshLayout();
		if(null != swipe)
			swipe.setOnRefreshListener(this);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		getContext().unregisterReceiver(mReceiver);
		if(null != mLoadParam)
			getLoader().release(mLoadParam);
		if(null != mAdapter)
		{
			mAdapter.release();
			mAdapter = null;
		}
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {

			if(intent.getAction().equals(getLoader().getAction())){

				if(null == mLoadParam)
					return;

				//判断加载内容是不是当前显示的内容
				HashMap<String, String> params = mLoadParam.getParams();
				for(String key : params.keySet())
				{
					String param = params.get(key);
					if(null != param && !param.equals(intent.getStringExtra(key)))
						return;
				}
				
				SwipeRefreshLayout swipe = getSwipeRefreshLayout();
				if(null != swipe)
					swipe.setRefreshing(false);

				boolean status = intent.getBooleanExtra(NetLoader.STATUS, false);
				if(getLoader().isReachBottom(mLoadParam))
				{
					mAdapter.setLoadType(FenYeAdapter.TYPE_LOADED);
				}

				//bug:这里广播没有区分加载的参数，状态会出现不正确的情况
				if(status){	//从网络上加载数据成功
					mAdapter.notifyDataSetChanged();
				}else{	//从网络上加载数据失败
					mAdapter.setLoadType(FenYeAdapter.TYPE_LOAD_FAILD);
				}
			}
		}
	};

	@Override
	public void onRefresh() {
		if(null == mLoadParam)
			return;
		mAdapter.setLoadType(FenYeAdapter.TYPE_LOADING);
		getLoader().refreshResource(mLoadParam);
	}

	@Override
	public void onLoad() {
		mAdapter.setLoadType(FenYeAdapter.TYPE_LOADING);
		getLoader().loadResource(mLoadParam);
	}


	public abstract FenYeMapLoader2<T> getLoader();


	public void goToLoad(LoadParam param)
	{
		if(null != mLoadParam)
		{
			getLoader().release(mLoadParam);
		}
		mLoadParam = param;

		mAdapter = new MySimpleFenYeAdapter(getContext(),getLoader().get(mLoadParam));
		mAdapter.setColumnCount(getColumnCount());
		mAdapter.setOnLoadListener(this);
		getListView().setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(this);
	}


	public class MySimpleFenYeAdapter extends SimpleFenYeAdapter<T>
	{

		public MySimpleFenYeAdapter(Context context, ArrayList<T> objects) {
			super(context, objects);
		}

		@Override
		public ViewHolder<T> getViewHolder() {
			return SimpleFenYeFragment.this.getViewHolder();
		}

		@Override
		public View getLoadedView() {
			View ret = SimpleFenYeFragment.this.getLoadedView();
			if(null != ret)
				return ret;
			return super.getLoadedView();
		}


		@Override
		public View getLoadFailedView() {
			View ret = SimpleFenYeFragment.this.getLoadFailedView();
			if(null != ret)
				return ret;
			return super.getLoadFailedView();
		}


		@Override
		public View getLoadingView() {
			View ret = SimpleFenYeFragment.this.getLoadingView();
			if(null != ret)
				return ret;
			return super.getLoadingView();
		}
	}


	public abstract ViewHolder<T> getViewHolder();

	public abstract ListView getListView();

	public abstract SwipeRefreshLayout getSwipeRefreshLayout();

	public int getColumnCount()
	{
		return 1;
	}


	public MySimpleFenYeAdapter getAdapter()
	{
		return mAdapter;
	}

	public View getLoadedView() {
		return null;
	}


	public View getLoadFailedView() {
		return null;
	}


	public View getLoadingView() {
		return null;
	}
}
