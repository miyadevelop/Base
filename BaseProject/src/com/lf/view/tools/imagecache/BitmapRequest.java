package com.lf.view.tools.imagecache;

import com.lf.controler.tools.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 处理bitmap的请求
 * 
 * @author LinChen,code when 2015年3月31日
 */
public class BitmapRequest {

	private Context context;
	private BitmapStatus bitmapStatus;
	private BitmapFetch bitmapFetch;
	private int width = 0, height = 0;
	private Config config = Config.ARGB_8888;
	private Object bindData;

	public BitmapRequest(Context context, BitmapStatus status, BitmapFetch bitmapFetch) {
		this.context = context;
		this.bitmapStatus = status;
		this.bitmapFetch = bitmapFetch;
	}

	/**
	 * 缩放图片
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public BitmapRequest size(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * 绑定额外的数据
	 * 
	 * @param object
	 * @return
	 */
	public BitmapRequest bindData(Object object) {
		bindData = object;
		return this;
	}

	/**
	 * 异步加载一张图片
	 * 
	 * @param cb
	 *            当获取图片失败，比如是因为网络连接有问题时，回调会返回null
	 */
	public void bitmap(final BitmapRequestCallBack cb) {
		bitmapFetch.bitmap(new CallBack<Bitmap>() {
			Bitmap result = null;

			@Override
			public void onResult(Bitmap t) {
				if (t == null) {
					Bitmap errBitmap = bitmapStatus.getErrBitmap();
					if (errBitmap != null) {
						t = errBitmap.copy(config, true);
					}
				}

				if (t != null) {
					result = BitmapUtils.scaleWithWH(t, width, height);
				}

				bitmapStatus.getHandler().post(new Runnable() {

					@Override
					public void run() {
						cb.onResult(result, bindData);
					}
				});
			}
		});
	}
}
