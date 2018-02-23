package com.lf.view.tools.swiperefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 默认的下拉样式
 * @author ludeyuan
 *
 */
public class CircleProgressView extends View implements Runnable{
	private final int PEROID = 16;	//周期
	private int mStartAngle = 0;	//开始绘制的时候，绘制的角度
	private int mSwipeAngle;		//偏移的角度 
	private int mWidth,mHeight;		//View的宽、高

	private RectF mBgRect;			//背景的区域
	private Paint mBgPaint;			//背景的画布
	private RectF mOvalRect;		//圆形箭头的区域
	private Paint mProgressPaint;

	private int mProgressBarColor = 0xffcccccc;	//进度条的颜色
	private int mCirclrBgColor = 0xffffffff;	//进度条背景的颜色
	private int mShadowColor = 0xff999999;		//阴影的颜色

	private float mDensity;
	private int mSpeed = 8;
	private boolean mIsOnDraw = false;			//正在绘制
	private boolean mIsRunning = false;			//正在执行

	public CircleProgressView(Context context) {
		this(context,null);
	}

	public CircleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//这里不确定用this，第三个参数传0有什么影响，所以单独写一份代码
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mDensity = metrics.density;
	}

	public CircleProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mDensity = metrics.density;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawArc(getBgRect(),0, 360, false,createBgPaint());
		int index = mStartAngle / 360;
		if (index % 2 == 0) {
			mSwipeAngle = (mStartAngle % 720) / 2;
		} else {
			mSwipeAngle = 360 - (mStartAngle % 720) / 2;
		}
		canvas.drawArc(getOvalRect(), mStartAngle, mSwipeAngle, false, createPaint());
	}

	@Override
	public void run() {
		while(mIsOnDraw){
			mIsRunning = true;
			long startTime = System.currentTimeMillis();
			mStartAngle +=mSpeed;
			postInvalidate();
			long time = System.currentTimeMillis() - startTime;
			if(time <PEROID){
				try{
					Thread.sleep(PEROID - time);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}

	/**-- 创建画笔 --*/
	private Paint createPaint(){
		if(mProgressPaint==null){
			mProgressPaint = new Paint();
			mProgressPaint.setStrokeWidth((int)mDensity*3);
			mProgressPaint.setStyle(Paint.Style.STROKE);
			mProgressPaint.setAntiAlias(true);
		}
		mProgressPaint.setColor(mProgressBarColor);
		return mProgressPaint;
	}

	/**-- 创建背景画布 --*/
	@SuppressLint("NewApi")
	private Paint createBgPaint(){
		if(mBgPaint==null){
			mBgPaint = new Paint();
			mBgPaint.setColor(mCirclrBgColor);
			mBgPaint.setStyle(Paint.Style.FILL);
			mBgPaint.setAntiAlias(true);
			if (Build.VERSION.SDK_INT >= 11) {
				this.setLayerType(LAYER_TYPE_SOFTWARE, mBgPaint);
			}
			mBgPaint.setShadowLayer(4.0f,0.0f, 2.0f, mShadowColor);
		}
		return mBgPaint;
	}

	/**-- 获取绘制的背景区域 --*/
	private RectF getBgRect(){
		mWidth = getWidth();
		mHeight = getHeight();
		if(mBgRect==null){
			int offset = (int)(mDensity*2);
			mBgRect = new RectF(offset, offset,mWidth-offset, mHeight-offset);
		}
		return mBgRect;
	}

	/**-- 获取绘制的箭头区域 --*/
	private RectF getOvalRect(){
		mWidth = getWidth();
		mHeight = getHeight();
		if(mOvalRect==null){
			int offset = (int)(mDensity*8);
			mOvalRect = new RectF(offset, offset,mWidth - offset, mHeight - offset);
		}
		return mOvalRect;
	}

	/**
	 * 正在执行
	 * @return
	 */
	public boolean isRunning() {
		return mIsRunning;
	}

	/**
	 * 设置进度条背景的颜色
	 * @param circleBackgroundColo 十六进制的颜色值
	 */
	public void setCircleBackgroundColor(int circleBackgroundColor) {
		mCirclrBgColor = circleBackgroundColor;
	}

	/**
	 * 正在绘制
	 * @param draw
	 */
	public void setOnDraw(boolean draw){
		mIsOnDraw = draw;
	}

	/**
	 * 设置进度条的颜色
	 * @param progressBarColor 十六进制的颜色值
	 */
	public void setProgressBarColor(int progressBarColor){
		mProgressBarColor = progressBarColor;
	}

	/**
	 * 设置进度条的阴影颜色
	 * @param shadowColor 十六进值的颜色值
	 */
	public void setShadowColor(int shadowColor) {
		mShadowColor = shadowColor;
	}

	/**
	 * 设置转动的速率
	 * @param speed
	 */
	public void setSpeed(int speed) {
		mSpeed = speed;
	}

	/**
	 * 滑动的距离改变，刷新界面
	 * @param distance
	 */
	public void setPullDistance(int distance){
		mStartAngle = distance*2;
		postInvalidate();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		mIsOnDraw = false;
		super.onDetachedFromWindow();
	}
}
