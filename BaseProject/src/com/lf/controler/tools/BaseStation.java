package com.lf.controler.tools;

/**
 * 基站信息
 * 
 * @author LinChen
 *
 */
public class BaseStation {
	/**
	 * 移动国家代码（中国的为460）
	 */
	private int mcc;
	/**
	 * 移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
	 */
	private int mnc;
	/**
	 * 位置区域码
	 */
	private int lac;
	/**
	 * 基站编号
	 */
	private int cid;
	/**
	 * 
	 */
	private int psc;

	public int getMcc() {
		return mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public int getLac() {
		return lac;
	}

	public int getCid() {
		return cid;
	}

	public int getPsc() {
		return psc;
	}

	protected void setMcc(int mcc) {
		this.mcc = mcc;
	}

	protected void setMnc(int mnc) {
		this.mnc = mnc;
	}

	protected void setLac(int lac) {
		this.lac = lac;
	}

	protected void setCid(int cid) {
		this.cid = cid;
	}

	protected void setPsc(int psc) {
		this.psc = psc;
	}

}
