package com.lf.controler.tools.download;

import android.content.Context;

public class WifiUpdateDownload extends MultiFunDownload {

	public WifiUpdateDownload(Context context, String url) {
		super(context, url);
		setAutoInstall(false);
	}

	public void install() {
		if (isDownloadFinish()) {
			boolean flag = isAutoInstall();
			setAutoInstall(true);
			start();
			setAutoInstall(flag);
		}
	}

	public void setActivate(boolean isActivate) {
		setAutoInstall(isActivate);
	}

}
