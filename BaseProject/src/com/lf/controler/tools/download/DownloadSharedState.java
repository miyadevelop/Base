package com.lf.controler.tools.download;

public class DownloadSharedState {

	public static final int STATE_START = -1;
	public static final int STATE_PAUSE = -2;
	public static final int STATE_SUCCESS = -3;
	public static final int STATE_SERVER_ERR = -4;
	public static final int STATE_DOWNLOAD_ERR = -5;
	public static final int STATE_DOWNLOAD_REPEAT = -6;
	public static final int STATE_INTERRUPTED = -7;
	public static final int STATE_NET_ERR = -8;
	public static final int STATE_TOAST = -9;

	private int state = STATE_START;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public DownloadSharedState() {
	}
}
