package com.lf.tools.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lf.controler.tools.SoftwareData;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.QZoneShareContent;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享的包装<br>
 * 
 * @author LinChen,code when 2015年2月9日
 */
public class ShareManager {
	private static ShareManager instance;
	/**
	 * 手机QQ包名
	 */
	private static final String PKG_MOBILE_QQ = "com.tencent.mobileqq";
	/**
	 * QQ空间包名
	 */
	private static final String PKG_QQ_ZONE = "com.qzone";
	/**
	 * 某些特殊的分享平台需要这个参数，否则分享不了，这个值提供给外部
	 */
//	public static final String TARGET_URL = "http://www.lovephone.com.cn/money/website.json?num=";

	private static String DEF_URL ;

	private String title = "分享";
	private String content = "";
	private IShareListener shareListener;
	private ShareImage shareImage;
	private QZoneShareContent qzoneShare;
	private ShareBean mShareBean;

	private ShareManager(Activity activity) {

		String qq_appId = SoftwareData.getMetaData("qqappid", activity);
		String qq_secret = SoftwareData.getMetaData("qqappkey", activity);
		String wx_appId = SoftwareData.getMetaData("wxappid", activity);
		String wx_secret = SoftwareData.getMetaData("wxsecret", activity);

//		String qq_appId = activity.getString(R.string.share_channel_qq_app_id);
//		String qq_secret = activity.getString(R.string.share_channel_qq_app_key);
//		String wx_appId = activity.getString(R.string.share_channel_wechat_id);
//		String wx_secret = activity.getString(R.string.share_channel_wechat_secret);
		PlatformConfig.setWeixin(wx_appId, wx_secret);
		PlatformConfig.setQQZone(qq_appId, qq_secret);
	}


	public static ShareManager getInstance(Activity activity) {
		if (instance == null) {
			instance = new ShareManager(activity);
		}
		return instance;
	}


	
	/**
	 * 设置分享的内容
	 * @param bean
	 */
	public void setShareBean(ShareBean bean){
		mShareBean = bean;
//		return this;
	}

	/**
	 * 直接将内容发送到指定分享平台
	 */
	public void directShare(Activity activity,ShareBean bean, SharePlatform sharePlatform,IShareListener shareListener) {
		doShare(activity, getSHARE_MEDIA(sharePlatform), bean, shareListener);
	}
	
	public void openShare(Activity activity,ShareBean bean,IShareListener shareListener) {
//		umService.openShare(activity, this);
		setShareListener(shareListener);
		 new ShareAction(activity).setDisplayList(
         		SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,
         		SHARE_MEDIA.WEIXIN_CIRCLE)
                 .withTitle(bean.getTitle())
                 .withText(bean.getContent())
                 .withMedia(bean.getImage())
                 .withTargetUrl(bean.getUrl())
                 .setCallback(UMShareListener)
                 .open();
	}


	public void doShare(Activity activity,SHARE_MEDIA media,ShareBean bean,IShareListener shareListener){
		setShareListener(shareListener);
		ShareAction shareAction = new ShareAction(activity);
		 shareAction.withText(bean.getContent());
		 shareAction.withMedia(bean.getImage());
		 shareAction.withTitle(bean.getTitle());
		 shareAction.withTargetUrl(bean.getUrl());
		 shareAction.setPlatform(media).setCallback(UMShareListener).share();
	}

	public void postShare(Activity activity, PopupWindow popupWindow) {
		popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}


	/**
	 * 设置分享的监听
	 * 
	 * @param shareListener
	 */
	public void setShareListener(IShareListener shareListener) {
		this.shareListener = shareListener;
	}

	/**
	 * 获取封装好的平台
	 * 
	 * @param media
	 * @return
	 */
	private SharePlatform getPlatform(SHARE_MEDIA media) {
		SharePlatform platform = null;
		if (SHARE_MEDIA.WEIXIN == media) {
			platform = SharePlatform.WEIXIN;
		} else if (SHARE_MEDIA.WEIXIN_CIRCLE == media) {
			platform = SharePlatform.WEIXIN_FRIEND;
		} else if (SHARE_MEDIA.QQ == media) {
			platform = SharePlatform.QQ;
		} else if (SHARE_MEDIA.QZONE == media) {
			platform = SharePlatform.QQ_ZONE;
		} else if (SHARE_MEDIA.SINA == media) {
			platform = SharePlatform.SINA;
		} else if (SHARE_MEDIA.TENCENT == media) {
			platform = SharePlatform.TENCENT;
		} else if (SHARE_MEDIA.SMS == media) {
			platform = SharePlatform.SMS;
		}
		return platform;
	}
	
	private SHARE_MEDIA getSHARE_MEDIA(SharePlatform platform) {
		if (platform == SharePlatform.WEIXIN) {
			return SHARE_MEDIA.WEIXIN;
		} else if (platform == SharePlatform.WEIXIN_FRIEND) {
			return SHARE_MEDIA.WEIXIN_CIRCLE;
		} else if (platform == SharePlatform.QQ) {
			return SHARE_MEDIA.QQ;
		} else if (platform == SharePlatform.QQ_ZONE) {
			return SHARE_MEDIA.QZONE;
		} else if (platform == SharePlatform.SINA) {
			return SHARE_MEDIA.SINA;
		} else if (platform == SharePlatform.TENCENT) {
			return SHARE_MEDIA.TENCENT;
		} else if (platform == SharePlatform.SMS) {
			return SHARE_MEDIA.SMS;
		}
		return null;
	}

	/**
	 * 用于判断是否是官方的app
	 * 
	 * @return
	 */
	public static boolean isOffical(Context context) {
		try {
			Class.forName(context.getPackageName() + ".wxapi.WXEntryActivity");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 系统的原生分享
	 * @param context
	 * @param bean
	 */
	public void localShare(Context context,ShareBean bean){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		// 查询所有可以分享的Activity
		List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
		                PackageManager.MATCH_DEFAULT_ONLY);
		    if (!resInfo.isEmpty()) {
		            List<Intent> targetedShareIntents = new ArrayList<Intent>();
		            for (ResolveInfo info : resInfo) {
		                Intent targeted = new Intent(Intent.ACTION_SEND);
		                targeted.setType("text/plain");
		                ActivityInfo activityInfo = info.activityInfo;
		                // 分享出去的内容
		                targeted.putExtra(Intent.EXTRA_TEXT, bean.getContent());
		                // 分享出去的标题
		                targeted.putExtra(Intent.EXTRA_SUBJECT, bean.getTitle());
		                targeted.setPackage(activityInfo.packageName);
		                targeted.setClassName(activityInfo.packageName, info.activityInfo.name);
		                PackageManager pm = context.getApplicationContext().getPackageManager();
		                //获取qq微信，qq空间，朋友圈
		                String s = info.loadLabel(pm).toString();
		                String name = info.activityInfo.applicationInfo.loadLabel(pm).toString();
		                boolean isneed = s.contains("空间")||s.contains("朋友圈")||name.contains("QQ")||name.contains("微信");
		                boolean needfilter = info.loadLabel(pm).toString().contains("收藏")|| info.loadLabel(pm).toString().contains("电脑");
		                if(isneed&&!needfilter){
		                	targetedShareIntents.add(targeted);
		                }
		            }
		            // 选择分享时的标题
		            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "选择分享");
		            if (chooserIntent == null) {
		                return;
		            }
		            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
		            try {
		            	context.startActivity(chooserIntent);
		            } catch (android.content.ActivityNotFoundException ex) {

		                Toast.makeText(context, "找不到分享的应用", Toast.LENGTH_SHORT).show();
		            }}
	}


	private SHARE_MEDIA toInnerMedia(SharePlatform platform) {
		if (platform == SharePlatform.QQ) {
			return SHARE_MEDIA.QQ;
		} else if (platform == SharePlatform.QQ_ZONE) {
			return SHARE_MEDIA.QZONE;
		} else if (platform == SharePlatform.SINA) {
			return SHARE_MEDIA.SINA;
		} else if (platform == SharePlatform.SMS) {
			return SHARE_MEDIA.SMS;
		} else if (platform == SharePlatform.TENCENT) {
			return SHARE_MEDIA.TENCENT;
		} else if (platform == SharePlatform.WEIXIN) {
			return SHARE_MEDIA.WEIXIN;
		} else if (platform == SharePlatform.WEIXIN_FRIEND) {
			return SHARE_MEDIA.WEIXIN_CIRCLE;
		}
		return null;
	}

	
	private UMShareListener UMShareListener = new UMShareListener() {
		
		@Override
		public void onResult(SHARE_MEDIA arg0) {
			if(shareListener!=null)
			shareListener.onSuccess(getPlatform(arg0));
		}
		
		@Override
		public void onError(SHARE_MEDIA arg0, Throwable arg1) {
			if(shareListener!=null)
			shareListener.onFail(getPlatform(arg0));
		}
		
		@Override
		public void onCancel(SHARE_MEDIA arg0) {
			if(shareListener!=null)
			shareListener.onFail(getPlatform(arg0));
		}
	};

}