package com.lf.controler.tools.download.helper;

import java.io.IOException;
import java.io.InputStream;


import com.lf.controler.tools.StringUtil;
import com.lf.controler.tools.download.DownloadListener;
import com.lf.controler.tools.download.DownloadTask;

/**
 * 网络加载后，对服务器返回的即过处理，解析成可以客户端可以看懂的流
 * @author ludeyuan
 *
 */
public abstract class NetLoaderResponse implements DownloadListener,NetLoaderResult<String>{
	
	
	@Override
	public void onDownloadStart(DownloadTask arg0) {}
	
	@Override
	public void onDownloadRefresh(DownloadTask arg0, int arg1) {}
	
	@Override
	public void onDownloadOver(int flag, DownloadTask task, InputStream is) {
		if(flag==DownloadListener.SUCCESS){//下载数据成功
			try {
				String str = StringUtil.from(is);
				onSuccess(str);
			} catch (IOException e) {
				onErr(DownloadListener.DOWNLOAD_ERR);
				e.printStackTrace();
			}
		}else if (flag == DownloadListener.DOWNLOAD_ERR) {
			onErr(DownloadListener.DOWNLOAD_ERR);
		}else if (flag == DownloadListener.SERVER_ERR) {
			onErr(DownloadListener.SERVER_ERR);
		}
	}
	
	@Override
	public void onDownloadPause(DownloadTask arg0) {}
}
