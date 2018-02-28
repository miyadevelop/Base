package com.lf.view.tools.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lf.view.tools.imagecache.MyImageView;
import com.mobi.tool.MyR;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片浏览
 * @author zzq
 *
 */
public class BaseImagePreActivity extends AppCompatActivity implements OnPageChangeListener{

	//第几页
	private TextView mCurrentPageTextView;

	//图片资源
	private ViewPager mPictureViewPager;	
	//当前第几页
	private int mCurrentIndex;
	//图片集合
	private ArrayList<ImageView> mPictureList = new ArrayList<ImageView>();
	
	
	//预览图片宽
	private int IMAGE_WIDTH;
	//预览图片高
	private int IMAGE_HEIGHT;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(MyR.layout(this, "activity_picture_preview"));
		ArrayList<String> pathsList = getIntent().getStringArrayListExtra("imagepaths");
		initView();
		initData(pathsList);
		reSetView();
	}


	private void initView(){
		mCurrentIndex = getIntent().getIntExtra("task_big_image_index", 0);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		IMAGE_WIDTH = dm.widthPixels;
		IMAGE_HEIGHT = dm.heightPixels;
		mCurrentPageTextView = (TextView) findViewById(MyR.id(this, "picture_preview_currentpage"));
		mPictureViewPager= (ViewPager) findViewById(MyR.id(this, "picture_preview_viewpager"));
		mPictureViewPager.setOnPageChangeListener(this);

		//设置过渡动画
		ViewCompat.setTransitionName(mPictureViewPager, "transitionPic");

	}

	
	/**
	 * 加载图片资源完成
	 */
	public void initData(ArrayList<String> pathsList){
		for(String s:pathsList){
			MyImageView imageView = new MyImageView(this);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			mPictureList.add(imageView);
		}
		BasePicturePagerAdapter adapter = new BasePicturePagerAdapter(mPictureList,pathsList,this);
		mPictureViewPager.setAdapter(adapter);
		mPictureViewPager.setCurrentItem(mCurrentIndex );
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		reSetView();

	}
	
	private void reSetView(){
		mCurrentPageTextView.setText(mPictureViewPager.getCurrentItem() + 1 +"/" + mPictureViewPager.getAdapter().getCount());
	}
	
	
	/**
	 * 设置文字颜色大小，背景并显示
	 * @param textSize 文字大小
	 * @param textColor 文字颜色
	 * @param background 背景
	 */
	protected void setTextStyle(int textSize, int textColor, int background){
		mCurrentPageTextView.setTextColor(textColor);
		mCurrentPageTextView.setTextSize(textSize);
		mCurrentPageTextView.setBackgroundResource(background);
		mCurrentPageTextView.setVisibility(View.VISIBLE);
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		mCurrentPageTextView = null;
		mPictureViewPager = null;
		super.onDestroy();
	}


	/**
	 * 图片适配器
	 * @author zzq
	 *
	 */
	public class BasePicturePagerAdapter extends PagerAdapter {

		public List<ImageView> mListViews;
		public List<String> mPicturePaths;
		private Context mContext;
		public BasePicturePagerAdapter(List<ImageView> mListViews, List<String> picturePaths, Context context) {
			this.mListViews = mListViews;
			this.mPicturePaths = picturePaths;
			mContext = context;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView myImageView = mListViews.get(position);
			Glide.with(mContext).load(mPicturePaths.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(myImageView);
			myImageView.setOnClickListener(onClickListener);
			container.addView(myImageView);
			return myImageView;
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public int getItemPosition(Object object) {

			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0==arg1;
		}


		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Activity activity = (Activity) mContext;
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				activity.finishAfterTransition();
//			}
//			else
				{
					activity.finish();
					activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		};


	}

}
