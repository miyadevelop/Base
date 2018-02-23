package com.lf.view.tools.barcode;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


/**
 * 二维码和条形码生成、读取、扫描工具
 * @author ww
 *
 * 2016-1-22
 */
public class Code {


	/**
	 * 生成二维码
	 * @param string 二维码中包含的文本信息
	 * @param width 生成的正方形二维码图片的宽
	 * @return 返回二维码图片，在text不正确或者某些异常情况下，会返回null
	 */
	public static Bitmap create2DCode(String text,int width) {

		if (text == null || "".equals(text) || text.length() < 1) {
			return null;
		}

		try {

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, width, hints);
			int[] pixels = new int[width * width];
			for (int y = 0; y < width; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}

				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, width,Bitmap.Config.RGB_565);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, width);

			return bitmap;

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}




	/**
	 * 生成带logo的二维码
	 * @param text 二维码中包含的文本信息
	 * @param logo logo图片
	 * @param width 生成的正方形二维码图片的宽
	 * @param logoWidth 最大不超过width的一半
	 * @return 返回二维码图片，在text不正确或者某些异常情况下，会返回null
	 */
	public static Bitmap create2DCode(String text,Bitmap logo, int width, int logoWidth) {
		
		if (text == null || "".equals(text) || text.length() < 1) {
			return null;
		}

		if(logoWidth > width/2)
		{
			//由于容错性最高只能遮挡30%，所以这里限定一下logo的最大宽度
			logoWidth = width/4;
		}
		else
			logoWidth = logoWidth/2;
		//将logo图片按martix设置的信息缩放
		float sx = (float) 2 * logoWidth / logo.getWidth();
		float sy = (float) 2 * logoWidth/ logo.getHeight();
		Matrix m = new Matrix();
		m.setScale(sx, sy);//设置缩放信息
		logo = Bitmap.createBitmap(logo, 0, 0,logo.getWidth(), logo.getHeight(), m, false);
		
		try {
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);			
			BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, width, hints);
			int halfW = width / 2;
			int halfH = width / 2;

			int[] pixels = new int[width * width];//定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
			for (int y = 0; y < width; y++) {//从行开始迭代矩阵
				for (int x = 0; x < width; x++) {//迭代列

					if (bitMatrix.get(x, y)) {//如果有黑块点，记录信息
						pixels[y * width + x] = 0xff000000;//记录黑块信息
					}
					else {
						pixels[y * width + x] = 0xffffffff;
					}

					if (x > halfW - logoWidth && x < halfW + logoWidth
							&& y > halfH - logoWidth
							&& y < halfH + logoWidth) {//该位置用于存放图片信息
						//记录图片每个像素信息
						pixels[y * width + x] = pixels[y * width + x] + logo.getPixel(x - halfW
								+ logoWidth, y - halfH + logoWidth); } 
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, width,Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap
			bitmap.setPixels(pixels, 0, width, 0, 0, width, width);
			return bitmap;

		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * 识别二维码
	 * @param bitmap 二维码图片
	 * @return 识别结果的文本，如果识别失败，返回空串
	 */
	public static String read2DCode(Bitmap bitmap) {

		Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

		// 获得待解析的图片
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result;
		try {
			result = reader.decode(bitmap1, hints);
			// 得到解析后的文字
			return result.getText();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * 前往扫描
	 * 
	 *
	 * @param resuleCode startActivityForResult中的resuleCode
	 * @return
	 */
	public static void scan(Activity activity ,int resuleCode)
	{
        Intent intent3 = new Intent(activity, CaptureActivity.class);  
        activity.startActivityForResult(intent3, resuleCode);  
	}
}
