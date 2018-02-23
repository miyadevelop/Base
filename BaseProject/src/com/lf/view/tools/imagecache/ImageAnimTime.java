package com.lf.view.tools.imagecache;

/**
 * 图片显示动画，如果MyImageView是放置在ArrayAdapter中，会导致动画一起出现，体验不好
 * 通过当前类，MyImageView加载动画都从这里获取，可以显示动画的开始显示时间
 * 即时这个类可能会无处释放，影响也不是很大
 * @author ludeyuan
 *
 */
public class ImageAnimTime {
	
	private static ImageAnimTime mImageAniTime;
	private final int TIME_DURATION=150;//动画持续的时间,和base_anim_imageview_show中对应
	private long mLastAniTime;						//上次动画显示的时间
	
	private ImageAnimTime(){
		mLastAniTime = System.currentTimeMillis();
	}
	
	public static ImageAnimTime getInstance(){
		if(mImageAniTime==null){
			mImageAniTime = new ImageAnimTime();
		}
		return mImageAniTime;
	}
	
	/**
	 * 获取到当前的动画需要延迟多久才可以显示
	 * @return
	 */
	public long getStartTimeOffset(){
		long time=System.currentTimeMillis();
		
		long startAni=0;
		if(time-TIME_DURATION*2/3<mLastAniTime){//需要设置时间间隔
			startAni=mLastAniTime+TIME_DURATION*2/3-time;
			mLastAniTime = mLastAniTime+TIME_DURATION*2/3;
		}else{
			mLastAniTime = time;
		}
		return startAni;
	}
}
