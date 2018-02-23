package com.mobi.tool;

import android.content.Context;

import java.lang.reflect.Field;

public class MyR {
	private static Class<?> id = null;
	private static Class<?> drawable = null;
	private static Class<?> layout = null;
	private static Class<?> anim = null;
	private static Class<?> style = null;
	private static Class<?> string = null;
	private static Class<?> array = null;
	private static Class<?> color = null;
	private static Class<?> raw = null;
	private static Class<?> xml = null;
	private static Class<?> menu = null;
	private static Class<?> dimen = null;
	private static Class<?> attr = null;
	private static Class<?> bool = null;
	private static Class<?> integer = null;

	public static int attr(Context context, String paramString) {
		if (attr == null)
			try {
				attr = Class.forName(context.getPackageName() + ".R$attr");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(attr, paramString);
	}

	public static int bool(Context context,String paramString){
		if(bool==null){
			try{
				bool = Class.forName(context.getPackageName() + ".R$bool");
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
		}
		return format(bool, paramString);
	}

	public static int integer(Context context,String paramString){
		if(integer==null){
			try{
				integer = Class.forName(context.getPackageName() + ".R$integer");
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
		}
		return format(integer, paramString);
	}

	public static int dimen(Context context, String paramString) {
		if (dimen == null)
			try {
				dimen = Class.forName(context.getPackageName() + ".R$dimen");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(dimen, paramString);
	}

	public static int anim(Context context, String paramString) {
		if (anim == null)
			try {
				anim = Class.forName(context.getPackageName() + ".R$anim");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(anim, paramString);
	}

	public static int id(Context context, String paramString) {
		if (id == null)
			try {
				id = Class.forName(context.getPackageName() + ".R$id");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(id, paramString);
	}

	public static int drawable(Context context, String paramString) {
		if (drawable == null)
			try {
				drawable = Class.forName(context.getPackageName() + ".R$drawable");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(drawable, paramString);
	}

	public static int layout(Context context, String paramString) {
		if (layout == null)
			try {
				layout = Class.forName(context.getPackageName() + ".R$layout");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(layout, paramString);
	}

	public static int style(Context context, String paramString) {
		if (style == null)
			try {
				style = Class.forName(context.getPackageName() + ".R$style");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(style, paramString);
	}

	public static int string(Context context, String paramString) {
		if (string == null)
			try {
				string = Class.forName(context.getPackageName() + ".R$string");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(string, paramString);
	}

	public static int array(Context context, String paramString) {
		if (array == null)
			try {
				array = Class.forName(context.getPackageName() + ".R$array");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(array, paramString);
	}

	public static int raw(Context context, String paramString) {
		if (raw == null)
			try {
				raw = Class.forName(context.getPackageName() + ".R$raw");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(raw, paramString);
	}

	public static int color(Context context, String paramString) {
		if (color == null)
			try {
				color = Class.forName(context.getPackageName() + ".R$color");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(color, paramString);
	}

	public static int xml(Context context, String paramString) {
		if (xml == null)
			try {
				xml = Class.forName(context.getPackageName() + ".R$xml");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(xml, paramString);
	}

	public static int menu(Context context, String paramString) {
		if (menu == null)
			try {
				menu = Class.forName(context.getPackageName() + ".R$menu");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return format(menu, paramString);
	}

	private static int format(Class<?> paramClass, String paramString) {
		if (paramClass == null) {
			throw new IllegalArgumentException("ResClass is not initialized.");
		}
		try {
			Field localField = paramClass.getField(paramString);
			return localField.getInt(paramString);
		} catch (Exception localException) {
//			MyLog.i(paramString + " can not found");
//			localException.printStackTrace();
		}
		return -1;
	}
}
