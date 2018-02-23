package com.lf.view.tools.imagecache;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * 设置到ImageView上图片的管理
 * @author ludeyuan
 *
 */
public class HttpImagePath{

	/**
	 * 根据网址，获取到图片在本地存储的路径
	 * @param url 图片的网址
	 * @param context 防止没有SD卡，需要存储在内存上的情况
	 * @return
	 */
	public static String getPathOnSD(Context context,String url){
		String imageFolder;//文件存储文件夹的路径
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			imageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ "/"+context.getPackageName()+"/images";
		}else{
			String path = context.getFilesDir().toString();
			String del = path.substring(0, path.lastIndexOf("/"));
			imageFolder = del + "/images";
		}
		return getPathOnSD(url, imageFolder);
	}

	/**
	 * 根据网址和指定的文件夹，获取到图片在本地的存储路径
	 * @param url 图片的网址
	 * @param folder 指定存储的图片路径
	 * @return
	 */
	public static String getPathOnSD(String url,String folder){
		return folder + File.separator + BitmapSimpleName.gen(url);
	}

	public static String getPathOnSD(String url,String imageName,String folder){
		if(imageName==null)
			return getPathOnSD(url, folder);

		if(folder.endsWith(File.separator) || imageName.startsWith(File.separator))
			return folder + imageName;
		return folder + File.separator + imageName;
	}
}
