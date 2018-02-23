package com.lf.view.tools.imagecache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;
import com.lf.tools.log.MyLog;

/**
 * 圆形的头像（应该继承MyImageView,因为BitmapOption和以及默认图片）
 * @author ludeyuan
 *
 */
public class CircleImageView extends ImageView{

	private final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private final int COLORDRAWABLE_DIMENSION = 2;

	private final int DEFAULT_BORDER_WIDTH = 0;	//图片默认的边框大小
	private final int DEFAULT_BORDER_COLOR = Color.BLACK; //图片默认的边框颜色
	private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;

	private int mBorderColor = DEFAULT_BORDER_COLOR;	//图片边框的颜色
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;	//图片的边框大小

	private int mImageSize = -1;						//图片的宽、高

	private String mCustomTag;

	private int mFillColor = DEFAULT_FILL_COLOR;

	private boolean mReady;	//已经初始化
	private boolean mSetupPending;	//setup函数处理过
	private boolean mBorderOverlay;

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();
	private final Paint mFillPaint = new Paint();

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private ColorFilter mColorFilter;

	private float mDrawableRadius;
	private float mBorderRadius;

	public CircleImageView(Context context) {
		super(context);
		init();
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(ScaleType.CENTER_CROP);

		//		TypedArray a = context.obtainStyledAttributes(attrs,com.lf.base.MyR.styleable.CircleImageView, defStyle, 0);
		//		
		//		mBorderWidth = a.getDimensionPixelSize(com.lf.base.MyR.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
		//		mBorderColor = a.getColor(com.lf.base.MyR.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);

		//		a.recycle();

		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	@Override
	public ScaleType getScaleType() {
		return ScaleType.CENTER_CROP;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		//重写控件的setScaleType,防止修改ImageViewscaleType的样式
		if(scaleType!=ScaleType.CENTER_CROP){
			MyLog.e(String.format("ScaleType %s not supported",scaleType));
		}
	}

	@Override
	public void setAdjustViewBounds(boolean adjustViewBounds) {
		//重写控件方法，防止修改
		if(adjustViewBounds){
			MyLog.e(String.format("adjustViewBounds not supported."));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap == null) {
			return;
		}

		if (mFillColor != Color.TRANSPARENT) {
			canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mDrawableRadius, mFillPaint);
		}
		canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mDrawableRadius, mBitmapPaint);
		if (mBorderWidth != 0) {
			canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mBorderRadius, mBorderPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	/**
	 * 设置边框颜色
	 * @param borderColor
	 */
	public void setBorderColor(@ColorInt int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	/**
	 * 设置边框颜色
	 * @param borderColor
	 */
	public void setBorderColorResource(@ColorRes int borderColorRes) {
		setBorderColor(getContext().getResources().getColor(borderColorRes));
	}

	public int getFillColor() {
		return mFillColor;
	}

	public void setFillColor(@ColorInt int fillColor) {
		if (fillColor == mFillColor) {
			return;
		}

		mFillColor = fillColor;
		mFillPaint.setColor(fillColor);
		invalidate();
	}

	public void setFillColorResource(@ColorRes int fillColorRes) {
		setFillColor(getContext().getResources().getColor(fillColorRes));
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	/**
	 * 设置边框的大小
	 * @param borderWidth：图片边框的大小，默认为0
	 */
	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	public boolean isBorderOverlay() {
		return mBorderOverlay;
	}

	public void setBorderOverlay(boolean borderOverlay) {
		if (borderOverlay == mBorderOverlay) {
			return;
		}

		mBorderOverlay = borderOverlay;
		setup();
	}

	/**
	 * 设置图片的大小，宽或高
	 * @param imageWidth
	 */
	public void setImageSize(int imageWidthOrHeight){
		mImageSize = imageWidthOrHeight;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	/**
	 * 设置圆形头像的路径
	 * @param imagePath 网址、assets或者SD卡上的图片绝对路径
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
	 * 设置圆形头像的路径
	 * @param imagePath 网址、assets或者SD卡上的图片绝对路径
	 * @param folder:网络图片时，需要缓存的路径
	 */
	public final void setImagePath(String imagePath,String imageName,String folder){
//		Log.i("Lafeng", "imagePath:" + imagePath);
//		Log.i("Lafeng", "mCustomTag:" + mCustomTag);
		if(mCustomTag!=null && mCustomTag.equals(imagePath)){
			//Tag还在，说明正在加载，就不需要再处理
			return;
		}
		if(null != mCustomTag)
			BitmapManager.getInstance(getContext()).removeReference(mCustomTag,this);
		mCustomTag = imagePath;
		BitmapOptions options = new BitmapOptions();
		options.config = BITMAP_CONFIG;
		if(mImageSize>0){
			options.requireWidth = mImageSize;
			options.requireHeight = mImageSize;
		}
//		Log.i("Lafeng", "imagePath:" + imagePath);
		BitmapManager.getInstance(getContext()).getBitmap(getContext(), imagePath,imageName, folder,new BitmapLoadedCallBack() {
			@Override
			public void loadOver(Bitmap bitmap, String loadingTag) {
				
//				Log.i("Lafeng", "set bitmap:" + bitmap);
				//只有满足条件时候，才为图片赋值
				if(mCustomTag!=null && loadingTag.contains(mCustomTag)){
					if(bitmap==null){	//加载失败，设置加载失败的图片
						setImageDrawable(null);
					}else{
				
//						mBitmap = bitmap;
//						setup();
						setImageBitmap(bitmap);
					}
//					mCustomTag = null;
				}
			}
		},options);
		BitmapManager.getInstance(getContext()).addReference(mCustomTag, this);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		if (cf == mColorFilter) {
			return;
		}

		mColorFilter = cf;
		mBitmapPaint.setColorFilter(mColorFilter);
		invalidate();
	}

	/**
	 * 初始化，设置图片的填充以及默认的样式
	 */
	private void init() {
		super.setScaleType(ScaleType.CENTER_CROP);//均衡的图像缩放，图片大于等于相应的坐标
		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private void setup(){
		
//		Log.i("Lafeng", "setup");
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (getWidth() == 0 && getHeight() == 0) {
			return;
		}

//		Log.i("Lafeng", "setup:"+mBitmap);
		if (mBitmap == null) {
			invalidate();
			return;
		}

		//Shader.TileMode.CLAMP:拉伸
		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		mFillPaint.setStyle(Paint.Style.FILL);
		mFillPaint.setAntiAlias(true);
		mFillPaint.setColor(mFillColor);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f);

		mDrawableRect.set(mBorderRect);
		if (!mBorderOverlay) {
			mDrawableRect.inset(mBorderWidth, mBorderWidth);
		}
		mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	//	@Override
	//	protected void onDetachedFromWindow() {
	//		super.onDetachedFromWindow();
	//		BitmapManager.getInstance(getContext()).onReleaseImage(this);
	//	}
	//	
	//	@Override
	//	public void onWindowFocusChanged(boolean hasWindowFocus) {
	//		super.onWindowFocusChanged(hasWindowFocus);
	//		if(!hasWindowFocus){
	//			BitmapManager.getInstance(getContext()).onReleaseUnConnect();
	//		}
	//	}

	//	@Override
	//	protected void onDetachedFromWindow() {
	//		setImageDrawable(null);
	//		BitmapManager.getInstance(getContext()).onRelease(getContext(), mCustomTag);
	//		super.onDetachedFromWindow();
	//	}
	//	
	//	@Override
	//	public void destroyDrawingCache() {
	//		setImageDrawable(new ColorDrawable());
	//		BitmapManager.getInstance(getContext()).onRelease(getContext(),mCustomTag);
	//		super.destroyDrawingCache();
	//	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
//		setImageBitmap(null);
//		BitmapManager.getInstance(getContext()).removeReference(mCustomTag, this);
//		BitmapManager.getInstance(getContext()).release(mCustomTag);
		
		release();
	}
	
	
	public void release()
	{
		if(mCustomTag == null)
			return;
		mBitmap = null;
		setImageBitmap(null);
		BitmapManager.getInstance(getContext()).removeReference(mCustomTag, this);
		BitmapManager.getInstance(getContext()).release(mCustomTag);
		mCustomTag = null;
	}
}
