package com.lf.entry.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.lf.entry.Entry;

import java.util.List;


/**
 * 入口适配器
 * @author wangwei
 *
 */
public class EntryAdapter extends ArrayAdapter<Entry>{

	public EntryAdapter(Context context, List<Entry> objects) {
		super(context, 0, objects);
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryItemView itemView;
		if(null == convertView)
		{
			itemView = getView();
		}
		else
			itemView = (EntryItemView)convertView;

		itemView.setEntry(getItem(position),getImageId(),getTextId());

		return itemView;
	}


	/**
	 * 获取一个入口的布局，子类建议重写此方法，简化开发
	 * @return
	 */
	protected EntryItemView getView()
	{
		EntryItemView entryItemView = new EntryItemView(getContext());
		ImageView imageView = new ImageView(getContext());
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setAdjustViewBounds(true);
		imageView.setId(1);
		entryItemView.addView(imageView, new RelativeLayout.LayoutParams(-1, -2));
		return entryItemView;
	}


	protected int getImageId()
	{
		return 1;
	}

	protected int getTextId()
	{
		return 2;
	}

	
	@Override
	public int getCount() {
		return super.getCount();
	}
}
