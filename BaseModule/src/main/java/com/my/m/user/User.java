package com.my.m.user;

import android.text.TextUtils;

public class User {

	public static final String GENDER_M = "M";//男
	public static final String GENDER_F = "F";//女
//	private int id;
	private String user_id;//身份id 标识
	private String name;//用户名 、昵称
	private String icon_url;//头像
	private String phone;//手机号
	private String alim_pwd;//即时通信密码 用户ID：user_id就是即时通信ID (阿里百川)
	private String app_key ;//软件识别码
	private String app_version;//app版本
	private String create_time;//创建时间
	private String update_time;// 修改时间
	private String askSrc;// 哪个平台
	private String login_time;// 每次登陆时间
	private String qq;//用户的qq id
	private String wechat;//用户的微信id
	private String gender;//男：M 女：F
	private String birthday;//生日
	private String province;//省
	private String city;



	public String getId() {
		return user_id;
	}

//	public void setId(int id) {
//		this.id = id;
////	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		if(!TextUtils.isEmpty(name))
			return name;
		if(!TextUtils.isEmpty(user_id) && user_id.length() > 6)
			return user_id.substring(user_id.length() - 6, user_id.length());
		return name;
	}

	public String getRealName()
	{
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlim_pwd() {
		return alim_pwd;
	}

	public void setAlim_pwd(String alim_pwd) {
		this.alim_pwd = alim_pwd;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getAskSrc() {
		return askSrc;
	}

	public void setAskSrc(String askSrc) {
		this.askSrc = askSrc;
	}

	public String getLogin_time() {
		return login_time;
	}

	public void setLogin_time(String login_time) {
		this.login_time = login_time;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}



	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
