package com.lf.view.tools;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;

import java.util.ArrayList;

public abstract class SimpleFenYeAdapter<T> extends FenYeAdapter<T>{

	public SimpleFenYeAdapter(Context context, ArrayList<T> objects) {
		super(context, objects);
	}

	

	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView) {

		ViewHolder<T> holder;

		if(null == convertView)
		{
			holder = getViewHolder();
//			convertView = View.inflate(getContext(), MyR.layout(getContext(), Config.mItemLayout), null);
			convertView = holder.initView();
			convertView.setTag(holder);
			convertView.setLayoutParams(new TableRow.LayoutParams(-1,-2,1));
			
		}
		else
		{
			holder = (ViewHolder<T>)convertView.getTag();
		}

		holder.initData(getItem(position));
			
		return convertView;
	}


	public static abstract class ViewHolder<T>
	{
		public ViewHolder(){};
		public abstract View initView();
		public abstract void initData(T t);
	}
	
	
	public abstract ViewHolder<T> getViewHolder();
	
}
