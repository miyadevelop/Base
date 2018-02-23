package com.lf.view.tools.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lf.controler.tools.UncaughtExceptionHandler;
import com.lf.view.tools.activity.support.AssetsGallery;
import com.lf.view.tools.activity.support.GuideGalleryAdapter;
import com.lf.view.tools.activity.support.GuideVerticalPagerAdapter;
import com.lf.view.tools.activity.support.VerticalViewPager;
import com.lf.view.tools.activity.support.VerticalViewPager.OnPageChangeListener;
import com.lf.view.tools.imagecache.MyImageView;
import com.mobi.tool.MyR;

/**
 * 向导界面
 * @author zzq
 *
 */
public class BaseGuideActivity extends Activity{

	public static final String TAG = "BaseGuideActivity";
	//向导横向滑动
	public static final int GUIDE_HORIZATION = 0;
	//向导纵向滑动
	public static final int GUIDE_VERTICAL = 1;
	
	private AssetsGallery mHelpGallery;         //横向滑动

	
	private VerticalViewPager mVerticalViewPager; //纵向滑动
	
	private String mGoToClass;
	private Bundle mGoToBundle;

	//最后一项将显示的导向 子类需要的自定义View显示
	private LinearLayout mGuideLayout;
	//这个View用于缩小最后一项默认情况下的点击范围
	private View mTouchArea;

	//是否自己处理最后一项的点击 还是交给子类
	private boolean mDoSelf = true;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(MyR.layout(this, "activity_guide"));
		new UncaughtExceptionHandler(this);// 扑捉错误异常

		mGoToClass = getIntent().getStringExtra("go_to_class");
		mGoToBundle = getIntent().getExtras();

		initData();

		if(getGuideOrientation() == GUIDE_VERTICAL){
			mVerticalViewPager = (VerticalViewPager) findViewById(MyR.id(this, "guide_vertical_view"));
			mVerticalViewPager.setOnPageChangeListener(onVerticalPageChangeListener);
			
			ArrayList<MyImageView> verticalViews = new ArrayList<MyImageView>();
			for(String s:datas){
				MyImageView imageView = new MyImageView(this);
				verticalViews.add(imageView);
			}
			mVerticalViewPager.setAdapter(new GuideVerticalPagerAdapter(this,verticalViews,datas));
		}else{
			// 向导图片
			mHelpGallery = (AssetsGallery) findViewById(MyR.id(this, "guide_view_help"));
			mHelpGallery.init(mHandler);
			
			mHelpGallery.setAdapter(new GuideGalleryAdapter(this, datas));
		}
		mTouchArea = findViewById(MyR.id(this, "lasttoucharea"));
		mTouchArea.setOnClickListener(onClickListener);
		mGuideLayout = (LinearLayout) findViewById(MyR.id(this, "guide_guide_layout"));
		
	}
	
	
	@Override
	protected void onResume() {
		
		if(mHelpGallery != null && mHelpGallery.getAdapter() != null){
			GuideGalleryAdapter adapter = (GuideGalleryAdapter) mHelpGallery.getAdapter();
			adapter.notifyDataSetChanged();
		}
		if(mVerticalViewPager != null && mVerticalViewPager.getAdapter() != null){
			GuideVerticalPagerAdapter adapter = (GuideVerticalPagerAdapter) mVerticalViewPager.getAdapter();
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}


	/**
	 * mHandler:响应向导图片Gallery的翻页事件
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if (bundle.containsKey("index")) {
				int index = bundle.getInt("index");
				if (index == mHelpGallery.getCount() - 1) {
					
					// Button goToButton = (Button) findViewById(MyR.id(mContext,"button_go_to"));
					// goToButton.setVisibility(0);
					// goToButton.setOnClickListener(GuideActivity.this);
					// mOperationLayout.setVisibility(View.VISIBLE);
					mGuideLayout.setVisibility(View.VISIBLE);
					mTouchArea.setVisibility(View.VISIBLE);
				} else {
				
					mGuideLayout.setVisibility(View.GONE);
					mTouchArea.setVisibility(View.GONE);
					// mOperationLayout.setVisibility(View.INVISIBLE);
				}


			}
		}

	};



	@Override
	public void onBackPressed() {
		// 返回键无效
		// super.onBackPressed();
	}

	/**
	 * onKeyDown：捕捉返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK)
		// return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		System.gc();
	}
	
	
	private String[] datas;



	/**
	 * 添加自定义的导向View  用于当前Guide的最后一项
	 */
	protected void addGuideView(View view){
		if(view != null){
			mGuideLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));		
			mDoSelf = false;
		}
	}




	/**
	 * 初始化图片资源
	 */
	private void initData(){
		try {
			String pathName = "guide_images";
			datas = getAssets().list(pathName);
			for(int i = 0; i < datas.length; i++)
			{
				datas[i] = "assets/"+pathName + File.separator + datas[i];
			}


		} catch (IOException e) {
			Toast.makeText(this, "not found floder!", 1).show();
		}
	}

	
	
	/**
	 * 引导界面滑动方向
	 */
	protected int getGuideOrientation(){
		return GUIDE_HORIZATION;
	}

	
	OnPageChangeListener onVerticalPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			if (position == mVerticalViewPager.getAdapter().getCount() - 1) {
				
				// Button goToButton = (Button) findViewById(MyR.id(mContext,"button_go_to"));
				// goToButton.setVisibility(0);
				// goToButton.setOnClickListener(GuideActivity.this);
				// mOperationLayout.setVisibility(View.VISIBLE);
				mGuideLayout.setVisibility(View.VISIBLE);
				mTouchArea.setVisibility(View.VISIBLE);
			} else {
				
				mGuideLayout.setVisibility(View.GONE);
				mTouchArea.setVisibility(View.GONE);
				// mOperationLayout.setVisibility(View.INVISIBLE);
			}
			
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 界面点击事件
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTouchArea){
				if(mDoSelf){
					goActivity();
				}
			}
			
		}
	};
	
	/**
	 * 跳转相应界面
	 */
	private void goActivity(){
		Intent intent = new Intent();
		intent.setClassName(BaseGuideActivity.this, mGoToClass);
		intent.putExtras(mGoToBundle);
		startActivity(intent);
		finish();
	}
}
