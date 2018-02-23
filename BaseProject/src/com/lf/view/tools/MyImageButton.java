package com.lf.view.tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lf.controler.tools.BitmapUtils;


/**
 * 重写ImageButton，让ImageButton可以给它的图形填颜色，从而一个项目中，同样形状的一个按钮显示不同颜色时，只需要做一张素材图片
 * @author wangwei
 *
 */
public class MyImageButton extends ImageView{

	private int mColor = -100;//-1是白色0xffffff，所以不能用-1做默认值
	private ColorStateList mColorStateList;
	private Paint mColorPaint;
	private Paint mMaskPaint;

	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context, attrs);

	}

	public MyImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs){

		mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mColorPaint = new Paint();
		
		TypedArray ta = context.obtainStyledAttributes(attrs, com.lf.base.R.styleable.MyImageButton);
		int color = ta.getColor(com.lf.base.R.styleable.MyImageButton_my_color, -1);

		mColorStateList = ta.getColorStateList(com.lf.base.R.styleable.MyImageButton_my_color);
		mColor = color;
		String srcId =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "src");
		if(null != srcId && -1 != color)
		{
			srcId = srcId.replace("@", "");
			int id = Integer.parseInt(srcId);
			setImageDrawable(context.getResources().getDrawable(id));
		}
		else if(null != getDrawable())
		{
			setImageDrawable(getDrawable());
		}
		
//		setBackgroundResource(MyR.drawable(context, "button_click_bg"));

		setScaleType(ScaleType.FIT_XY);
		try
		{
			//设置完背景后要重新设置padding，否则padding将会失效
			String paddingId =  attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "padding");
			if(null != paddingId)
			{
				if(paddingId.contains("dp"))
				{
					paddingId = paddingId.replace("dp", "");
					int padding = UnitConvert.DpToPx(context, Float.parseFloat(paddingId));
					setPadding(padding, padding, padding, padding);	
				}
				else if(paddingId.contains("dip"))
				{
					paddingId = paddingId.replace("dip", "");
					int padding = UnitConvert.DpToPx(context, Float.parseFloat(paddingId));
					setPadding(padding, padding, padding, padding);	
				}
				else if(paddingId.contains("px"))
				{
					paddingId = paddingId.replace("px", "");
					int padding = Integer.parseInt(paddingId);
					setPadding(padding, padding, padding, padding);	
				}
				else
				{
					paddingId = paddingId.replace("@", "");
					int id = Integer.parseInt(paddingId);
					int padding = (int)getContext().getResources().getDimension(id);
					setPadding(padding, padding, padding, padding);	
				}
			}
			else
			{
				setPadding(0, 0, 0, 0);	
			}
		}catch (Exception exception)
		{
			exception.printStackTrace();
		}
		ta.recycle();
	}


//	@Override
//	public void setImageBitmap(Bitmap bm) {
//		super.setImageBitmap(bm);
//	}


	@Override
	public void setImageResource(int resId) {
		setImageDrawable(getContext().getResources().getDrawable(resId));
	}


	@Override
	public void setImageDrawable(Drawable drawable) {
		int color = mColor;
		if(null != mColorStateList)
			color = mColorStateList.getColorForState(getDrawableState(), mColor);
		if(-100 == color || color == 0)
		{
			super.setImageDrawable(drawable);
			return;
		}
		Bitmap bitmap = BitmapUtils.drawable2Bitmap(drawable);
		bitmap = replaceBitmapColor(bitmap, color);
		Drawable drawable2 = BitmapUtils.bitmap2Drawable(getContext(), bitmap);
		super.setImageDrawable(drawable2);
	}


	/**
	 * 给某个Bitmap涂色
	 * @param newColor
	 * @return
	 */
	public Bitmap replaceBitmapColor(Bitmap oldBitmap,int newColor)
	{
		//		newColor = newColor % 0x1000000;
		//		//相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
		//		Bitmap mBitmap = oldBitmap.copy(Config.ARGB_8888, true);
		//		//循环获得bitmap所有像素点
		//		int mBitmapWidth = mBitmap.getWidth();          
		//		int mBitmapHeight = mBitmap.getHeight();           
		//		for (int i = 0; i < mBitmapHeight; i++) {          
		//			for (int j = 0; j < mBitmapWidth; j++) {              
		//				//获得Bitmap 图片中每一个点的color颜色值
		//				//将需要填充的颜色值如果不是              
		//				//在这说明一下 如果color 是全透明 或者全黑 返回值为 0              
		//				//getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000               
		//				//而不透明黑色是0xFF000000 如果不计算透明部分就都是0了       
		//				int color = mBitmap.getPixel(j, i);
		//				//将颜色值存在一个数组中 方便后面修改             
		//
		//				if (color != 0) {   
		//					mBitmap.setPixel(j, i,  color - ( 0xffffff - newColor));  //将非空白区域的颜色 与 newColor进行叠加            
		//				}                      
		//
		//			}   
		//		}
		//		return mBitmap;

		if(newColor < 0x1000000)
		{
			newColor = 0xFF000000 + newColor;//加上alpha位
		}
		Bitmap result = Bitmap.createBitmap(oldBitmap.getWidth(), oldBitmap.getHeight(), Config.ARGB_8888);
		//将遮罩层的图片放到画布中
		Canvas mCanvas = new Canvas(result);

		mColorPaint.setColor(newColor);
		mCanvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()), mColorPaint);
		mCanvas.drawBitmap(oldBitmap, 0, 0, mMaskPaint);
//		paint.setXfermode(null);
		return result;
	}


	@Override
	public void setActivated(boolean activated) {
		super.setActivated(activated);
		setImageDrawable(getDrawable());
	}
}
