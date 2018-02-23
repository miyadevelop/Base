package com.lf.view.tools.barcode;

import java.util.Hashtable;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 解析二维码，获取到二维码的值
 * @author ludeyuan
 *
 */
public class DecodingBarcode {

	/**
	 * 根据二维码的bitmap进行解析
	 * @return 返回二维码的地址
	 */
	public static String decodingBitmap(Bitmap bitmap){
		//解析转换类型UTF-8
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		//新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
		RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
		//将图片转换成二进制图片
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
		//初始化解析对象
		QRCodeReader reader = new QRCodeReader();
		//开始解析
		Result result = null;
		try {
			result = reader.decode(binaryBitmap, hints);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(result!=null){
			return result.toString();
		}else{
			return null;
		}
	}

	/**
	 * 根据二维码的地址进行解析
	 * @return	返回二维码的地址
	 */
	public static String decodingPath(String path){

		//获取到待解析的图片
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		//如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
		//并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
		options.inJustDecodeBounds = true;
		//以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
		options.inSampleSize = options.outHeight / 400;
		if(options.inSampleSize <= 0){
			options.inSampleSize = 1; //防止其值小于或等于0
		}
		/**
		 * 辅助节约内存设置
		 * 
		 * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
		 * options.inPurgeable = true; 
		 * options.inInputShareable = true; 
		 */
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path,options);
		String decodingValues = decodingBitmap(bitmap);
		if(bitmap!=null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap=null;
		}
		return decodingValues;
	}
}
