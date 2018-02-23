package com.lf.controler.tools.weather;

/**
 * 天气
 * 
 * @author ww
 * 
 */
public class Weather {
	// 城市
	private String mCity = "";
	// 城市编码
	private String mCityCode = "";
	// 最高温
	private String mTempHigh = "";
	// 最低温
	private String mTempLow = "";
	// 当前温
	private String mTempNow = "N";
	// 湿度
	private String humidity = "";
	// 风向
	private String windDirection = "";
	// 风速
	private String windSpeed = "";
	// 天气描述
	private String mWeatherStr = "";
	// 白天天气图片
	private String mDayWeatherImage = "";
	// 夜晚天气图片
	private String mNightWeatherImage = "";
	// 发布时间
	private String mPublishTime = "";
	// 有效时间
	private String mValidateTime = "";
	// 当前时间
	private String mCurrentTime = "";
	//空气质量
	private String mQuality = "";

	/**
	 * ��֤����Ƿ�����
	 * 
	 * @return
	 */
	public boolean isComplete() {
		// if (mCity == null || mCityCode == null || mTempHigh == null || mTempLow == null ||
		// mTempNow == null
		// || humidity == null || windDirection == null || windSpeed == null || mWeatherStr == null
		// || mDayWeatherImage == null || mNightWeatherImage == null || mPublishTime == null
		// || mValidateTime == null || mCurrentTime == null)
		// {
		// return false;
		// }
		return true;
	}

	/**
	 * ��ȡ������
	 * 
	 * @return
	 */
	public String getCity() {
		return mCity;
	}

	/**
	 * ���ó�����
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		mCity = city;
	}

	/**
	 * ��ȡ������
	 * 
	 * @return
	 */
	public String getCityCode() {
		return mCityCode;
	}

	/**
	 * ���ó�����
	 * 
	 * @param cityCode
	 */
	public void setCityCode(String cityCode) {
		mCityCode = cityCode;
	}

	/**
	 * ��ȡ���µ����ֵ
	 * 
	 * @return
	 */
	public String getTempHigh() {
		return mTempHigh;
	}

	/**
	 * �������µ����ֵ
	 * 
	 * @param tempHigh
	 */
	public void setTempHigh(String tempHigh) {
		mTempHigh = tempHigh;
	}

	/**
	 * ��ȡ���µ����ֵ
	 * 
	 * @return
	 */
	public String getTempLow() {
		return mTempLow;
	}

	/**
	 * �������µ����ֵ
	 * 
	 * @param tempLow
	 */
	public void setTempLow(String tempLow) {
		mTempLow = tempLow;
	}

	/**
	 * ��ȡ��ǰ�¶�
	 * 
	 * @return
	 */
	public String getTempNow() {
		return mTempNow;
	}

	/**
	 * ���õ�ǰ�¶�
	 * 
	 * @param tempLow
	 */
	public void setTempNow(String tempNow) {
		mTempNow = tempNow;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getWeatherStr() {
		return mWeatherStr;
	}

	public String getShortWeatherStr() {
		if (mWeatherStr.length() <= 5) {
			return mWeatherStr;
		} else {
			if (mWeatherStr.indexOf("��") != -1) {
				return "��";
			} else if (mWeatherStr.indexOf("ǿɳ����") != -1 || mWeatherStr.indexOf("��ɳ") != -1
					|| mWeatherStr.indexOf("����") != -1 || mWeatherStr.indexOf("ɳ����") != -1) {
				return "ɳ��";
			} else if (mWeatherStr.indexOf("ѩ") != -1) {
				return "ѩ";
			} else if (mWeatherStr.indexOf("��") != -1) {
				return "��";
			} else if (mWeatherStr.indexOf("��") != -1) {
				return "��";
			} else if (mWeatherStr.indexOf("��") != -1) {
				return "��";
			} else if (mWeatherStr.indexOf("��") != -1) {
				return "��";
			} else if (mWeatherStr.indexOf("����") != -1) {
				return "����";
			} else {
				return "��";
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param weatherStr
	 */
	public void setWeatherStr(String weatherStr) {
		mWeatherStr = weatherStr;
	}

	/**
	 * ��ȡ��������ͼ��
	 * 
	 * @return
	 */
	public String getDayWeatherImage() {
		return mDayWeatherImage;
	}

	/**
	 * ���ð�������ͼ��
	 * 
	 * @param dayWeatherImage
	 */
	public void setDayWeatherImage(String dayWeatherImage) {
		mDayWeatherImage = dayWeatherImage;
	}

	/**
	 * ��ȡҹ������ͼ��
	 * 
	 * @return
	 */
	public String getNightWeatherImage() {
		return mNightWeatherImage;
	}

	/**
	 * ����ҹ������ͼ��
	 * 
	 * @param nightWeatherImage
	 */
	public void setNightWeatherImage(String nightWeatherImage) {
		mNightWeatherImage = nightWeatherImage;
	}

	/**
	 * ��ȡ����ʱ��
	 * 
	 * @return
	 */
	public String getPublishTime() {
		return mPublishTime;
	}

	/**
	 * ���÷���ʱ��
	 */
	public void setPublishTime(String publishTime) {
		mPublishTime = publishTime;
	}

	/**
	 * ��ȡ��֤ʱ��
	 * 
	 * @return
	 */
	public String getValidateTime() {
		return mValidateTime;
	}

	/**
	 * ������֤ʱ��
	 * 
	 * @param validate
	 */
	public void setValidateTime(String validate) {
		mValidateTime = validate;
	}

	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		return mCurrentTime;
	}

	/**
	 * ���õ�ǰʱ��
	 * 
	 * @param current
	 */
	public void setCurrentTime(String current) {
		mCurrentTime = current;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getQuality() {
		return mQuality;
	}

	public void setQuality(String quality) {
		this.mQuality = quality;
	}

}
