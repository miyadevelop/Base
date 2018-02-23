package com.lf.view.tools.imagecache;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.lf.controler.tools.BitmapUtils;
import com.lf.tools.log.MyLog;
import com.lf.view.tools.UnitConvert;
import com.lf.view.tools.imagecache.MyImageView;

/**
 * 圆角图片
 * @author wangwei
 *
 */
public class RoundCornerImageView extends MyImageView{

	private int mBorderColor = Color.BLACK;	//图片边框的颜色
	private int mBorderWidth = 0;	//图片的边框大小

	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private float mDrawableRadius = 20;

	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context,attrs);
		if(attrs != null){
			TypedArray ta = context.obtainStyledAttributes(attrs, com.lf.base.R.styleable.RoundCornerImageView);
			String radius = ta.getString(com.lf.base.R.styleable.RoundCornerImageView_civ_conner_radius);

			if(null != radius)
			{
				if(radius.contains("dp"))
				{
					radius = radius.replace("dp", "");
					mDrawableRadius = UnitConvert.DpToPx(context, Float.parseFloat(radius));
				}
				else if(radius.contains("dip"))
				{
					radius = radius.replace("dip", "");
					mDrawableRadius = UnitConvert.DpToPx(context, Float.parseFloat(radius));
				}
				else if(radius.contains("px"))
				{
					radius = radius.replace("px", "");
					mDrawableRadius = Integer.parseInt(radius);
				}
				else
				{
					radius = radius.replace("@", "");
					int id = Integer.parseInt(radius);
					mDrawableRadius = (int)getContext().getResources().getDimension(id);
				}
			}
		}
	}

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup(null);
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

		if(null != mBitmapPaint)
			canvas.drawRoundRect(mDrawableRect, mDrawableRadius, mDrawableRadius, mBitmapPaint);

		if (null != mBorderPaint && mBorderWidth != 0) {
			canvas.drawRoundRect(mDrawableRect, mDrawableRadius, mDrawableRadius, mBorderPaint);
		}
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
		invalidate();
	}


	public void setRoundCornerRadius(int radius)
	{
		mDrawableRadius = radius;
		invalidate();
	}


	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		Bitmap bitmap = BitmapUtils.drawable2Bitmap(drawable);
		setup(bitmap);
	}


	private void setup(Bitmap bitmap){

		if (getWidth() == 0 && getHeight() == 0) {
			return;
		}

		if (bitmap == null) {

			if(null != getDrawable())
			{
				bitmap = BitmapUtils.drawable2Bitmap(getDrawable());
			}
		}

		if (bitmap == null) {
			invalidate();
			return;
		}

		//Shader.TileMode.CLAMP:拉伸
		BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(bitmapShader);

		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());

		mDrawableRect.set(mBorderRect);
		mDrawableRect.inset(mBorderWidth, mBorderWidth);

		Matrix shaderMatrix = new Matrix();
		shaderMatrix.set(null);
		shaderMatrix.setScale(mDrawableRect.width() / (float) bitmapWidth, mDrawableRect.height() / (float) bitmapHeight);
		bitmapShader.setLocalMatrix(shaderMatrix);
		invalidate();
	}

}
