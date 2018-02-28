package com.lf.entry.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lf.entry.Entry;
import com.lf.entry.EntryManager;


/**
 * 入口布局，简单将布局和入口进行了封装，默认情况下，入口仅显示一张图片
 * @author wangwei
 *
 */
public class EntryItemView extends RelativeLayout{


	public EntryItemView(Context context) {
		super(context);
		setOnClickListener(mOnClickListener);
	}


	public EntryItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(mOnClickListener);
	}


	public EntryItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnClickListener(mOnClickListener);
	}



	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {

			if(null != mEntry)
				EntryManager.getInstance(getContext()).goTo(getContext(), mEntry);
		}
	};
	

	Entry mEntry;
	public void setEntry(Entry entry, int imageViewId, int textViewId)
	{
		mEntry = entry;
		try {
//			{
//				MyImageView imageView = (MyImageView) findViewById(imageViewId);
//				if (null != imageView)
//					imageView.setImagePath(entry.getImage());
//			}

			{
				ImageView imageView = (ImageView) findViewById(imageViewId);
				if (null != imageView)
					Glide.with(getContext()).load(entry.getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
			}
		
			TextView textView = (TextView)findViewById(textViewId);
			if(null != textView)
				textView.setText(entry.getText());
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
