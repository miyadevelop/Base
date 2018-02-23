package com.lf.view.tools.settings;

import java.lang.reflect.Field;

import android.content.Context;
import android.widget.ImageView;

/**
 * 设置界面的imageView
 * @author ww
 *
 */
public class ImageViewModule extends ImageView{

	public ImageViewModule(Context context) {
		super(context);
	}
	

	/**
	 * 设置imageView要显示的图片
	 * @param imageName 图片在drawable文件夹中的名称
	 */
	public void setImageResource(String imageName) {
	
		int id = R.drawable(getContext(),imageName);
		super.setImageResource(id);
	}

	
	/**
	 * 由于android中的R文件的路径在应用的主包名下，作为SDK，主包名会随着应用的不同而变，
	 * 本类是将android中的R类的资源进行转变，使本SDK可以引用res下的资源
	 * @author ww
	 *
	 */
	private static class R
	{
		private static Class<?> drawable = null;


		public static int drawable(Context context,String paramString)
		{
			if(null == drawable)
				try {
					drawable = Class.forName(context.getPackageName() + ".MyR$drawable");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}  
				return format(drawable, paramString);
		}

	
		private static int format(Class<?> paramClass, String paramString)
		{
			if (paramClass == null)
			{
				throw new IllegalArgumentException("ResClass is not initialized.");
			}
			try
			{
				Field localField = paramClass.getField(paramString);
				int k = localField.getInt(paramString);
				return k;
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
			}
			return -1;
		}
	}

}
