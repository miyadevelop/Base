package com.lf.view.tools.activity.support;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

/**
 * AssetsGallery：向导图片Gallery
 * @author ww
 *
 */
public class AssetsGallery extends Gallery{

	private Context mContext;
	private Handler mHandler;
	
	public AssetsGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setOnItemSelectedListener(mOnItemSelectedListener);
	}

	
	/**
	 * init:初始化向导图片和发送翻页信息的Handler
	 * @param pathName：存放向导图片的assets下的文件夹名
	 * @param handler
	 */
	public void init(Handler handler)
	{
		mHandler = handler;
		
	}

	
	/**
	 * 响应翻页事件
	 */
	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("index", arg2);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};


	/**
	 * onFling:重写该函数是为了可以更轻松地让Gallery每次只滑一屏
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX<-10) { 
			return super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null); 
			} else if (velocityX>10) { 
				return super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null); 
			} else { 
			return super.onFling(e1, e2, velocityX, velocityY); 
			}
	}
	



	
	
}