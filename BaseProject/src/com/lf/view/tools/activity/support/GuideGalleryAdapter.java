package com.lf.view.tools.activity.support;

import com.lf.view.tools.imagecache.MyImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView.ScaleType;

public class GuideGalleryAdapter extends ArrayAdapter<String>{
		
		private final String TAG = "GuideGalleryAdapter";
	
		private String[] mData;
		
	
		public GuideGalleryAdapter(Context context,String[] data) {
			super(context, 0, data);
			mData = data;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Context context = getContext();
			if(convertView == null){
				Log.d(TAG, "当前的position--->" + position);
				MyImageView imageView = new MyImageView(context);
				imageView.setShowAnim(false);
				imageView.setImagePath(mData[position]);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				convertView = imageView;
			}
			
			return convertView;

		}

		@Override
		public final int getCount() {
			return mData.length;
		}

		@Override
		public final String getItem(int position) {
			return mData[position];
		}

		@Override
		public final long getItemId(int position) {
			return position;
		}
		
		

	
}
