package com.lf.view.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;


/**
 * 一次滑屏的Gallery
 * @author wangwei
 *
 */
public class PageGallery extends Gallery{


	public PageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


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
