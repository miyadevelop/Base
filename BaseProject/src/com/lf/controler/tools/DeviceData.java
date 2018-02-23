package com.lf.controler.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * 手机设备信息
 *
 * @author LinChen
 */
public class DeviceData {

    private static String imei;
    private static String mac;
    private static String imsi;

    /**
     * 获取imei
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        if (null != imei) {
            return imei;
        }

        imei = null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try {
                imei = telephonyManager.getDeviceId();//需要READ_PHONE_STATE权限
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(imei)) {
            //无需权限，但有时会返回null，在刷机、恢复出厂设置之后，会发生变化
            imei = Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        if (TextUtils.isEmpty(imei)) {
            //无需权限，但有的手机会返回相同的值，例如02:00:00:00:00:00
            imei = getMac(context);
        }
        if (TextUtils.isEmpty(imei)) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取MAC地址，获取不到返回空字符串
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        if (null != mac) {
            return mac;
        }
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mac = wifiInfo.getMacAddress();
        } catch (Exception e) {

        }
        if (mac == null) {
            mac = "";
        }
        return mac;
    }

    /**
     * 获取手机的Imsi信息，没有获取到返回空字符串
     *
     * @param context
     * @return
     */
    public static String getImsi(Context context) {
        if (null != imsi) {
            return imsi;
        }
        try {
            TelephonyManager telManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = telManager.getSubscriberId();
        } catch (Exception e) {
        }
        if (imsi == null) {
            imsi = "";
        }
        return imsi;
    }


    /**
     * 更为完善的获取imsi方法，但尚未测试过
     *
     * @param context
     * @return
     */
    public static String getImsi1(Context context) {
        String imsi = "";
        try {   //普通方法获取imsi
            TelephonyManager tm = (TelephonyManager) context.
                    getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (imsi == null || "".equals(imsi)) imsi = tm.getSimOperator();
            Class<?>[] resources = new Class<?>[]{int.class};
            Integer resourcesId = 1;
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    MTK手机
                    Method addMethod = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);
                    addMethod.setAccessible(true);
                    imsi = (String) addMethod.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    展讯手机
                    Class<?> c = Class
                            .forName("com.android.internal.telephony.PhoneFactory");
                    Method m = c.getMethod("getServiceName", String.class, int.class);
                    String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
                    TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
                    imsi = tm1.getSubscriberId();
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                try {   //利用反射获取    高通手机
                    Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", resources);
                    addMethod2.setAccessible(true);
                    imsi = (String) addMethod2.invoke(tm, resourcesId);
                } catch (Exception e) {
                    imsi = null;
                }
            }
            if (imsi == null || "".equals(imsi)) {
                imsi = "000000";
            }
            return imsi;
        } catch (Exception e) {
            return "000000";
        }
    }


    /**
     * 获取基站信息
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static BaseStation getBaseStation(Context context) {
        try {
            BaseStation baseStation = new BaseStation();
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String operator = manager.getNetworkOperator();
            int mcc = Integer.parseInt(operator.substring(0, 3));
            int mnc = Integer.parseInt(operator.substring(3));
            int lac = 0;
            int cellid = 0;
            int psc = -1;
            if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                CdmaCellLocation location = (CdmaCellLocation) manager.getCellLocation();
                if (location != null) {
                    cellid = location.getBaseStationId();
                    lac = location.getNetworkId();
                }
            } else {
                GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
                if (location != null) {
                    cellid = location.getCid();
                    lac = location.getLac();
                    if (Build.VERSION.SDK_INT >= 9) {
                        psc = location.getPsc();
                    }
                }
            }
            baseStation.setCid(cellid);
            baseStation.setLac(lac);
            baseStation.setMcc(mcc);
            baseStation.setMnc(mnc);
            baseStation.setPsc(psc);
            return baseStation;
        } catch (Exception e) {
            BaseStation baseStation = new BaseStation();
            baseStation.setCid(0);
            baseStation.setLac(0);
            baseStation.setMcc(0);
            baseStation.setMnc(0);
            baseStation.setPsc(-1);
            return baseStation;
        }
    }

    /**
     * 获取当前系统的版本号
     *
     * @return
     */
    public static int getOSVer() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取外部版本号，例如5.0
     *
     * @return
     */
    public static String getOSUserVer() {
        return android.os.Build.VERSION.RELEASE;
    }
}
