package com.lf.view.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lf.controler.tools.DeviceData;
import com.mobi.tool.MyR;

/**
 * 标题栏
 *
 */
public class TitleBar extends LinearLayout{

	protected LinearLayout mLayoutLeft;//左边按钮布局
	protected MyImageButton mBtnLeft;//左边按钮
	protected LinearLayout mLayoutRight;//右边按钮布局
	protected MyImageButton mBtnRight;//右边按钮
	protected MyImageButton mBtnRightBackup;//右边备份按钮，右数第二个按钮
	protected TextView mTextTitle;//标题文字
	protected RelativeLayout mLayout;//整体标题栏布局
	public static int LAYOUT = -1;

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TitleBar(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs){
		if(-1 == LAYOUT)
			LayoutInflater.from(context).inflate(MyR.layout(context, "base_layout_titlebar"), this);
		else
			LayoutInflater.from(context).inflate(LAYOUT, this);
		mLayoutLeft = (LinearLayout) findViewById(MyR.id(context, "titlebar_layout_left"));
		mBtnLeft = (MyImageButton) findViewById(MyR.id(context, "titlebar_btn_left"));
		//默认点击返回
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((Activity)getContext()).finish();
			}
		};
		mBtnLeft.setOnClickListener(listener);
		mLayoutRight = (LinearLayout) findViewById(MyR.id(context, "titlebar_layout_right"));
		mBtnRight = (MyImageButton) findViewById(MyR.id(context, "titlebar_btn_right"));
		mBtnRightBackup = (MyImageButton) findViewById(MyR.id(context, "titlebar_btn_right_backup"));
		mTextTitle = (TextView) findViewById(MyR.id(context, "titlebar_text_title"));
		mLayout = (RelativeLayout) findViewById(MyR.id(context, "titlebar_layout"));

		parseStyle(context, attrs);
	}


	/**
	 * 读取配置文件中定义的属性
	 * @param
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void parseStyle(Context context, AttributeSet attrs){
		if(attrs != null){
			TypedArray ta = context.obtainStyledAttributes(attrs, com.lf.base.R.styleable.TitleBar);
			String title = ta.getString(com.lf.base.R.styleable.TitleBar_titleBarTitleText);
			mTextTitle.setText(title);
			int color = ta.getColor(com.lf.base.R.styleable.TitleBar_titleBarTitleTextColor, -1);
			if(-1 != color)
				mTextTitle.setTextColor(color);

			String left = ta.getString(com.lf.base.R.styleable.TitleBar_titleBarBtnLeft);
			if("null".equals(left))
			{
				mBtnLeft.setVisibility(View.GONE);
			}
			else
			{
				Drawable leftDrawable = ta.getDrawable(com.lf.base.R.styleable.TitleBar_titleBarBtnLeft);
				if (null != leftDrawable) {
					mBtnLeft.setImageDrawable(leftDrawable);
				}
			}
			Drawable rightDrawable = ta.getDrawable(com.lf.base.R.styleable.TitleBar_titleBarBtnRight);
			if (null != rightDrawable) {
				mBtnRight.setImageDrawable(rightDrawable);
				mBtnRight.setVisibility(View.VISIBLE);//布局文件中，该按钮默认不可见，如果设置了图片，则改为可见
			}
			Drawable rightBackupDrawable = ta.getDrawable(com.lf.base.R.styleable.TitleBar_titleBarBtnRightBackup);
			if (null != rightBackupDrawable) {
				mBtnRightBackup.setImageDrawable(rightBackupDrawable);
				mBtnRightBackup.setVisibility(View.VISIBLE);//布局文件中，该按钮默认不可见，如果设置了图片，则改为可见
			}

			Drawable background = ta.getDrawable(com.lf.base.R.styleable.TitleBar_titleBarBg);
			if(null != background) {
				//这里设置背景，需要根据手机的版本号，来使用对应的方法
				if(DeviceData.getOSVer()>=16){
					mLayout.setBackground(background);
				}else{
					mLayout.setBackgroundDrawable(background);
				}

			}
			ta.recycle();
		}
	}

	//	public void setLeftBtnResource(int resId) {
	//		mBtnLeft.setImageResource(resId);
	//	}
	//
	//	public void setRightBtnResource(int resId) {
	//		mBtnRight.setImageResource(resId);
	//	}

	/**
	 * 设置左侧按钮的点击事件（默认是返回事件）
	 */
	public void setLeftBtnClickListener(OnClickListener listener){
		mBtnLeft.setOnClickListener(listener);
	}

	/**
	 * 设置右侧按钮的点击事件（默认没有点击事件）
	 */
	public void setRightBtnClickListener(OnClickListener listener){
		mBtnRight.setOnClickListener(listener);
	}

	/**
	 * 设置右侧备用按钮的点击事件（默认没有点击事件，且按钮在右侧按钮的左侧）
	 */
	public void setRightBackupBtnClickListener(OnClickListener listener){
		mBtnRightBackup.setOnClickListener(listener);
	}

	public void setLeftBtnVisibility(int visibility){
		mBtnLeft.setVisibility(visibility);
	}

	public void setRightBtnisibility(int visibility){
		mBtnRight.setVisibility(visibility);
	}

	public void setTitle(String title){
		mTextTitle.setText(title);
	}

	//	public void setBackgroundColor(int color){
	//		mLayout.setBackgroundColor(color);
	//	}
	//
	//	public LinearLayout getLeftLayout(){
	//		return mLayoutLeft;
	//	}
	//
	//	public LinearLayout getRightLayout(){
	//		return mLayoutRight;
	//	}

}
