package com.lf.tools.share;

import android.graphics.Bitmap;

/**
 * 分享的内容
 * @author ludeyuan
 *
 */
public class ShareBean {
	private String mUrl;		//分享的网址
	//hzc 20160913修改，将图片由bitmap改为ShareImage
	private ShareImage mImage;	//分享的图片
	private String mTitle;		//分享的标题
	private String mContent;	//分享的内容

	public void setUrl(String str){
		mUrl = str;
	}

	public String getUrl(){
		return mUrl;
	}

	public void setImage(ShareImage image){
		mImage = image;
	}

	public ShareImage getImage(){
		return mImage;
	}

	public void setTitle(String str){
		mTitle = str;
	}

	public String getTitle(){
		return mTitle;
	}


	public void setContent(String str){
		mContent = str;
	}

	public String getContent(){
		return mContent;
	}
}
