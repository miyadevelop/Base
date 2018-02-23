package com.lf.view.tools.imagecache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lf.base.R;
import com.lf.controler.tools.BitmapUtils.BitmapOptions;
import com.lf.view.tools.imagecache.BitmapManager.BitmapCellID;

/**
 * 继承系统的ImageView控件，通过设置图片的来源（assets、sd或网络），可以自动实现图片的显示
 * drawable下的图片使用系统默认的方法就可以实现
 * @author ludeyuan
 *
 */
public class MyImageView extends ImageView{

	private BitmapCellID mImageId = null;	//当前需要显示的地址
	private BitmapCellID mCurImageId = null;//当前正显示的图片的地址
	private Drawable mDefaultDrawable=null;	// 默认的图片
	private BitmapOptions mOptions;			//图片的显示质量
	private boolean mShowAnim = true;		//图片显示的时候有动画
	private boolean mReleaseOnWindowGone = false;//Activity退到后台时是否自动释放，true释放，false不释放

	public MyImageView(Context context) {
		this(context,null);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOptions = new BitmapOptions();
	}

	public final void setShowAnim(boolean flag){
		mShowAnim = flag;
	}

	/**
	 * 设置图片的显示质量
	 * @param options
	 */
	public final void setBitmapOptions(BitmapOptions options){
		mOptions = options;
	}

	/**
	 * 设置图片没有加载时候的默认图片
	 * @param defaultDrawabe 默认的图片(drawable下的图片)
	 */
	public void setImageDefault(Drawable defaultDrawabe){
		mDefaultDrawable = defaultDrawabe;
	}
	//
	//	/**
	//	 * 设置图片加载失败时，显示的图片
	//	 * @param failDrawabe 加载失败的图片(drawable下的图片)
	//	 */
	//	public void setImageFailed(Drawable failDrawabe){
	//		mFailDrawable = failDrawabe;
	//	}

	/**
	 * 为ImageView赋图片的地址
	 * @param imagePath 网址、assets或者SD卡上的图片绝对路径
	 * 注意：如果设计到网络图片的下载，会使用默认的缓存路径。
	 */
	public final void setImagePath(String imagePath){
		if(null == imagePath || "null".equals(imagePath))
			return;


		//先设置一个默认的路径
		String imageFolder;//文件存储文件夹的路径
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			imageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ "/"+getContext().getPackageName()+"/images";
		}else{
			String path = getContext().getFilesDir().toString();
			String del = path.substring(0, path.lastIndexOf("/"));
			imageFolder = del + "/images";
		}
		setImagePath(imagePath, imageFolder);
	}

	public final void setImagePath(String imagePath,String folder){
		setImagePath(imagePath,null,folder);
	}


	/**
	 * 为ImageView赋图片的地址
	 * @param path 网址、assets或者SD卡上的图片绝对路径
	 * @param saveFolder 网络图片时，需要缓存的路径
	 */
	public final void setImagePath(String path,String saveName,String saveFolder){
		BitmapCellID id = new BitmapCellID(path, saveName, saveFolder);
		//如果当前的MyImageView已经有图片，且tag一致，就不处理
		if(id.equals(mImageId)){
			return;
		}
		setImageDrawable(mDefaultDrawable);
		if(null != mImageId)
			BitmapManager.getInstance(getContext()).removeReference(getPath(), this);
		mImageId = id;
		BitmapManager.getInstance(getContext()).getBitmap(getContext(), path, saveName,saveFolder,mCallBack,mOptions);
		BitmapManager.getInstance(getContext()).addReference(getPath(), this);
	}

	/**
	 * 图片加载完成后的回调
	 */
	private BitmapLoadedCallBack mCallBack = new BitmapLoadedCallBack() {

		@Override
		public void loadOver(Bitmap bitmap, String path) {
			if(isSamle(mImageId, path)){
				//				if(bitmap==null){	//加载失败，设置加载失败的图片
				//					setImageDrawable(mFailDrawable);
				//				}else
				//				{

				if(!isSamle(mCurImageId, path))
				{
					if(mShowAnim && (getAnimation()==null || getAnimation().hasEnded())){
						Animation anim = AnimationUtils.loadAnimation(getContext(),
								R.anim.base_anim_imageview_show);
						startAnimation(anim);
					}
					mCurImageId = mImageId;
					//					mBitmap = bitmap;
					MyImageView.super.setImageBitmap(bitmap);
					//				}
				}
			}
		}
	};


	//	public Bitmap mBitmap;

	/**
	 * 判断imagePath中要加载图片和当前MyImageView上显示的是否一样
	 * @param imagePath
	 * @return
	 */
	private boolean isSamle(BitmapCellID id ,String path){

		if(null == id)
			return false;

		return id.equals(path);
	}


	/**
	 * 获取当前需要显示的图片的path
	 * @param
	 * @return
	 */
	private String getPath()
	{
		if(null == mImageId)
			return null;

		return mImageId.mPath;
	}


	/**
	 * 重写ImageView, 避免使用已回收的bitmap
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		try{
			super.onDraw(canvas);
		}catch(Exception e){
			setImageDrawable(null);
		}
	}


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(((Activity)getContext()).isFinishing())
			setImageBitmap(null);
		mImageId = null;
		mCurImageId = null;
		BitmapManager.getInstance(getContext()).removeReference(getPath(), this);
		BitmapManager.getInstance(getContext()).release(getPath());
	}


	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);

		if(mReleaseOnWindowGone && (View.GONE == visibility || View.INVISIBLE == visibility))
		{
			BitmapManager.getInstance(getContext()).removeReference(getPath(), this);
			BitmapManager.getInstance(getContext()).release(getPath());
		}
	}


	/**
	 * 设置Activity退到后台时是否自动释放
	 * @param release true释放，false不释放
	 */
	public void setReleaseOnWindowGone(boolean release)
	{
		mReleaseOnWindowGone = release;
	}
}
