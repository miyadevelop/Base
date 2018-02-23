package com.lf.view.tools.imagecache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;


/**
 * 图片管理，处理图片异步读取、缓存和释放
 * @author ww
 *
 * 2016-4-9
 */
public class BitmapManager {

	private static BitmapManager mManager;
	private Context mContext;
	private final int MAX_SAVE_COUNT = 60;	//最大的imageView的数量

	//图片的缓存。key是图片的id，实质上是图片的网络端和本地路径的组合，路径代表的图片的唯一性
	private volatile HashMap<BitmapCellID,BitmapCell> mBitmapCells = new HashMap<BitmapCellID, BitmapCell>();

	private BitmapManager(Context context){
		mContext = context.getApplicationContext();
	}

	/**
	 * 获取到单例对象
	 */
	public static BitmapManager getInstance(Context context){
		if(mManager == null){
			mManager = new BitmapManager(context);
		}
		return mManager;
	}


	/**
	 * 异步加载图片
	 * @param context 用来区分图片的分组和生成BitmapCell的id值； 可以是Activity上下文或ApplicationContext
	 * @param imagePath 图片的网址、磁盘上的路径或者assets下的目录
	 * @param floder 图片存储时所在文件夹的路径
	 * @param callBack 图片加载完成后的回调
	 */
	public void getBitmap(final Context context,final String path,String saveName,
			String saveFolder,final BitmapLoadedCallBack callBack,BitmapOptions options)
	{
		synchronized (mManager) {
			Log.i("Lafeng", "mBitmapCells.size():"+mBitmapCells.size());
			Log.i("Lafeng", "mBitmapCells.size():"+path);
			if(null == path)
				return;
			if(mBitmapCells.size()>MAX_SAVE_COUNT){
				//释放无效的图片
				release();
			}

			BitmapCellID id = new BitmapCellID(path, saveName, saveFolder);
			//已经有了缓存的图片，就直接调用回调函数
			BitmapCell bitmapCell = mBitmapCells.get(id);
			if(null != bitmapCell)
			{				
				Bitmap bitmap = bitmapCell.getBitmap();
				if(null != bitmap){
					callBack.loadOver(bitmap, path);
					return;
				}
			}

			//以下是没有有效的缓存图片，需要从本地或者服务端去读取
			//添加缓存
			if(null == bitmapCell)
			{
				bitmapCell = new BitmapCell();
				bitmapCell.mId = id;
				bitmapCell.mGroupId = context.toString();
				mBitmapCells.put(id,bitmapCell);
			}
			bitmapCell.addCallBack(callBack);

			boolean exist = new File(id.mLocalPath).exists();
			if(exist){
				//在磁盘上有文件，直接从磁盘上读取
				DiskBitmapLoadListener listener = new DiskBitmapLoadListener() {

					@Override
					public void loadOver(Bitmap bitmap, String id) {
						loadBitmapOver(bitmap,new BitmapCellID(id, null, null));
					}
				};
				DiskBitmapLoadCenter.getInstance(context).startLoadBitmap(id.mPath,id.mLocalPath,options,listener );
			}else{
				//需要从网络上下载
				BitmapRequest bitmapRequest = BitmapBed.getInstance(context).load(path,saveName, saveFolder,options);
				bitmapRequest.bindData(id);
				BitmapRequestCallBack callBack2 = new BitmapRequestCallBack() {

					@Override
					public void onResult(Bitmap bitmap, Object id) {
						loadBitmapOver(bitmap,(BitmapCellID)id);
					}
				};
				bitmapRequest.bitmap(callBack2);
			}
		}
	}

	
	public Bitmap getBitmapNow(String path, String saveName, String saveFolder)
	{
		if(null == path)
			return null;

		BitmapCellID id = new BitmapCellID(path, saveName, saveFolder);
		//已经有了缓存的图片，就直接调用回调函数
		BitmapCell bitmapCell = mBitmapCells.get(id);
		if(null != bitmapCell)
		{				
			return bitmapCell.getBitmap();
		}
		return null;
	}
	

	public void addReference(String path,Object reference)
	{
		synchronized (mManager) {
			if(null == path)
				return;
			//获取与id对应的BitmapCell
			BitmapCellID id = new BitmapCellID(path, null, null);
			BitmapCell bitmapCell = mBitmapCells.get(id);
			if(null != bitmapCell)
			{
				//添加引用
				bitmapCell.addReference(reference);
			}
		}
	}


	public void removeReference(String path,Object reference)
	{
		synchronized (mManager) {
			if(null == path)
				return;
			//获取与id对应的BitmapCell
			BitmapCellID id = new BitmapCellID(path, null, null);
			BitmapCell bitmapCell = mBitmapCells.get(id);
			if(null != bitmapCell)
			{
				//删除引用
				bitmapCell.removeReference(reference);
			}
		}
	}


	/**
	 * 释放指定界面的图片资源
	 * @param 指定界面的context
	 */
	public void release(Context context)
	{
		synchronized (mManager) {
			String groupId = context.toString();
			ArrayList<BitmapCellID> releaseIds = new ArrayList<BitmapCellID>();
			Iterator<BitmapCellID> ids = mBitmapCells.keySet().iterator();
			//遍历所有的缓存
			while(ids.hasNext()){
				BitmapCellID id = ids.next();
				BitmapCell bitmapCell = mBitmapCells.get(id);
				//如果缓存的分组id和context的id值相同
				if(bitmapCell.mGroupId.equals(groupId))
				{
					bitmapCell.release();
					releaseIds.add(id);
				}
			}
			//移除缓存
			for(BitmapCellID id : releaseIds)
			{
				mBitmapCells.remove(id);
			}
		}
	}


	/**
	 * 释放指定界面的指定图片资源
	 * @param context 指定界面的context
	 * @param path 指定图片的路径
	 */
	public void release(String path)
	{
		Log.i("Lafeng", "release1");
		if(null == path)
			return;
		Log.i("Lafeng", "release2");
		BitmapCellID id = new BitmapCellID(path, null, null);
		BitmapCell bitmapCell = mBitmapCells.get(id);
		if(null != bitmapCell && bitmapCell.mReferences.size() == 0)
		{
			Log.i("Lafeng", "release3");
			bitmapCell.release();
			mBitmapCells.remove(id);
		}
	}


	/**
	 * 释放没有用处的图片，即mReferences为空的BitmapCell
	 */
	private void release()
	{
		synchronized (mManager) {
			ArrayList<BitmapCellID> releaseIds = new ArrayList<BitmapCellID>();
			Iterator<BitmapCellID> ids = mBitmapCells.keySet().iterator();
			while(ids.hasNext()){
				BitmapCellID id = ids.next();
				BitmapCell bitmapCell = mBitmapCells.get(id);

				if(bitmapCell.mReferences.size() == 0)
				{
					bitmapCell.release();
					releaseIds.add(id);
				}
			}
			for(BitmapCellID id : releaseIds)
			{
				mBitmapCells.remove(id);
			}
		}
	}


	/**
	 * 图片加载结束时调用
	 * @param bitmap 图片 
	 * @return
	 */
	private void loadBitmapOver(final Bitmap bitmap,final BitmapCellID id){
		synchronized (mManager) {
			if(null == bitmap){
				return;
			}
			new Handler(mContext.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					//					BitmapCellID id = new BitmapCellID(path, null, null);
					BitmapCell bitmapCell = mBitmapCells.get(id);
					if(null == bitmapCell)
					{
						return;
					}
					bitmapCell.setBitmap(bitmap);
					bitmapCell.callBack();
				}
			});
		}
	}


	//	/**
	//	 * 定义图片id的组合规则
	//	 * @param imagePath 网址、SD卡路径或者assets下的目录
	//	 * @return 返回组合完的id
	//	 */
	//	private String getId(Context context,String imagePath){
	//		String contextName;
	//		if(context instanceof Activity){
	//			contextName = ((Activity)context).toString();
	//		}else{
	//			contextName = "ApplicationContext";
	//		}
	//		return contextName + UNDERLINE + imagePath;
	//	}


	//	/**
	//	 * 是否是本地的资源
	//	 * @param imagePathSource 图片的来源
	//	 * @param saveName 图片在本地存储的名称
	//	 * @param saveFolder 图片在本地存储的文件夹
	//	 * @return true:本地 false:网络图片
	//	 */
	//	private boolean isLocalResource(String imagePathSource,String saveName,
	//			String saveFolder){
	//		if (imagePathSource.startsWith("http")){
	//			final String fileName = HttpImagePath.getPathOnSD(imagePathSource,saveName, saveFolder);
	//			if((new File(fileName)).length()>0){
	//				return true;
	//			}else{
	//				return false;
	//			}
	//		}
	//		return true;
	//	}





	/**
	 * 与一张图片相关的数据，用来辅助图片的读取和释放
	 * @author ww
	 *
	 * 2016-4-11
	 */
	public class BitmapCell
	{
		private SoftReference<Bitmap> mBitmap;//图片
		public ArrayList<String> mReferences = new ArrayList<String>();//被应用的地方，如果此列表为空，则表示没有任何引用
		public ArrayList<BitmapLoadedCallBack> mCallBacks = new ArrayList<BitmapLoadedCallBack>();//图片读取完成后的回调
		public String mGroupId = "";//图片群组，表示是一组图片，例如一个界面的图片，供释放该组图片的时候使用
		public BitmapCellID mId;//图片的id，用来找到图片或者表示是哪一张图片


		public Bitmap getBitmap()
		{
			if(mBitmap == null || mBitmap.get()== null || mBitmap.get().isRecycled())
				return null;
			return mBitmap.get();
		}


		public void release()
		{
			Bitmap bitmap = getBitmap();
			if(null != bitmap)
			{
				bitmap.recycle();
				bitmap = null;
				mBitmap.clear();
				//				Log.i("Lafeng", "bitmap release");
			}
		}

		public void setBitmap(Bitmap bitmap)
		{
			//			Bitmap oldBitmap = getBitmap();
			//			if(oldBitmap != null && !oldBitmap.equals(bitmap))
			//			{
			//				release();
			//			}
			mBitmap = new SoftReference<Bitmap>(bitmap);
		}


		public void addCallBack(BitmapLoadedCallBack callBack)
		{
			if(!mCallBacks.contains(callBack))
			{
				mCallBacks.add(callBack);
			}
		}


		public void removeCallBack()
		{
			mCallBacks.clear();
		}


		public void addReference(Object reference)
		{
			if(!mReferences.contains("" + reference.hashCode()))
				mReferences.add("" + reference.hashCode());
		}


		public void removeReference(Object reference)
		{
			mReferences.remove("" + reference.hashCode());
		}


		public void callBack()
		{
			for(BitmapLoadedCallBack callBack : mCallBacks)
			{
				callBack.loadOver(mBitmap.get(), mId.mPath);
			}
			mCallBacks.clear();
		}
	}


	public static class BitmapCellID
	{
		public String mPath;//存储的地址
		public String mLocalPath;//本地储存地址


		public BitmapCellID(String path,String saveName, String saveFolder)
		{
			mPath = path;
			if (null != mPath && mPath.startsWith("http")){
				mLocalPath = HttpImagePath.getPathOnSD(mPath,saveName, saveFolder);
			}
			else
				mLocalPath = mPath;
		}

		@Override
		public boolean equals(Object object)
		{
			if(object instanceof BitmapCellID)
			{
				BitmapCellID objectBitmapCellID = (BitmapCellID)object;

				if(mLocalPath.equals(objectBitmapCellID.mLocalPath))
					return true;

				if(mPath.equals(objectBitmapCellID.mPath))
					return true;

				if(mPath.equals(objectBitmapCellID.mLocalPath))
					return true;

				if(mLocalPath.equals(objectBitmapCellID.mPath))
					return true;

			}

			if(object instanceof String)
			{
				if(mLocalPath.equals(object))
					return true;

				if(mPath.equals(object))
					return true;
			}
			return false;
		}



		@Override
		public int hashCode() {
			return mPath.hashCode();
		}

		//		/**
		//		 * 获取到资源的本地路径
		//		 * @param imagePathSource
		//		 * @param saveName
		//		 * @param saveFolder
		//		 * @return 资源的本地路径
		//		 */
		//		private String getLocalResourcePath(String imagePathSource,String saveName,
		//				String saveFolder){
		//			if (imagePathSource.startsWith("http")){
		//				final String fileName = HttpImagePath.getPathOnSD(imagePathSource,saveName, saveFolder);
		//				return fileName;
		//			}
		//			return imagePathSource;
		//		}
	}

}
