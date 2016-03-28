package com.patient.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


/**
 * <dl>
 * <dt>NetWorkUtil.java</dt>
 * <dd>Description:网络操作工具类</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>CreateDate: 2013-8-2 下午1:52:07</dd>
 * </dl>
 * 
 * @author lihs
 */
public class NetWorkUtil {

    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMWAP = "cmwap";
    public static final String CMNET = "cmnet";
    public static final String NET_3G = "3gnet";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static final String UNINET = "uninet";
    public static final int TYPE_DX_3G = 5;// 电信3G网络
    public static final int TYPE_YD_3G = 6;// 移动3G网络
    public static final int TYPE_LT_3G = 7;// 联通3G网络
    public static final int TYPE_DX_2G = 8;// 电信2G网络
    public static final int TYPE_YD_2G = 9;// 移动2G网络
    public static final int TYPE_LT_2G = 10;// 联通2G网络
    public static final int TYPE_OTHER = 11;// 其他
    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");// 获取APN所需地址
    public static final int TYPE_NET_WORK_DISABLED = 0;
    public static final int TYPE_WIFI = 4;
    public static boolean isMyOpenWiFi = false;

    /**
     * 客户端提示网络类型
     */
    public static void checkNet(Context context) {
        int checkNetworkType = checkNetworkType(context);
//        switch (checkNetworkType) {
//        case TYPE_WIFI:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_WIFI, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_NET_WORK_DISABLED:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_NULL, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_DX_3G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_DX_3G, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_DX_2G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_DX, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_YD_3G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_YD_3G, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_YD_2G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_YD, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_LT_3G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_LT_3G, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_LT_2G:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_LT, Toast.LENGTH_LONG).show();
//            break;
//        case TYPE_OTHER:
//            Toast.makeText(CmccApplication.getContext(),
//                    R.string.network_of_OTHER, Toast.LENGTH_LONG).show();
//            break;
//        default:
//            break;
//        }
    }

    public static int checkNetworkType(Context mContext) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return TYPE_NET_WORK_DISABLED;
            } else {
                // NetworkInfo不为null开始判断是网络类型
                int netType = mobNetInfoActivity.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    // wifi net处理
                    return TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    // 注意二：
                    // 判断是否电信wap:
                    // 不要通过getExtraInfo获取接入点名称来判断类型，
                    // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
                    // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
                    // 所以可以通过这个进行判断！
                    boolean is3G = isFastMobileNetwork(mContext);
                    final Cursor c = mContext.getContentResolver().query(
                            PREFERRED_APN_URI, null, null, null, null);
                    if (c != null) {
                        c.moveToFirst();
                        final String user = c.getString(c
                                .getColumnIndex("user"));
                        LogUtil.d("networkutil:user网络类型：" + user);
                        if (!TextUtils.isEmpty(user)) {
                            if (user.startsWith(CTWAP)
                                    || user.startsWith(CTNET)) {
                                if (is3G) {
                                    return TYPE_DX_3G;
                                } else
                                    return TYPE_DX_2G;
                            }
                        }
                    }
                    c.close();
                    // 注意三：
                    // 判断是移动联通wap:
                    // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
                    // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
                    // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
                    // 所以采用getExtraInfo获取接入点名字进行判断
                    String netMode = mobNetInfoActivity.getExtraInfo();
                    Log.i("", "==================netmode:" + netMode);
                    if (netMode != null) {
                        // 通过apn名称判断是否是联通和移动wap
                        netMode = netMode.toLowerCase();
                        // 判断移动的网络类型
                        if (netMode.equals(CMWAP) || netMode.equals(CMNET)) {
                            if (is3G) {
                                return TYPE_YD_3G;
                            } else
                                return TYPE_YD_2G;
                            // 判断联通的网络类型
                        } else if ((netMode.equals(NET_3G) || netMode
                                .equals(UNINET))
                                || (netMode.equals(WAP_3G) || netMode
                                        .equals(UNIWAP))) {
                            if (is3G) {
                                return TYPE_LT_3G;
                            } else
                                return TYPE_LT_2G;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }
        return TYPE_OTHER;
    }

    private static boolean isFastMobileNetwork(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA:
            return false; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE:
            return false; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
            return true; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
            return true; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS:
            return false; // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            return true; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA:
            return true; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA:
            return true; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS:
            return true; // ~ 400-7000 kbps
        case TelephonyManager.NETWORK_TYPE_EHRPD:
            return true; // ~ 1-2 Mbps
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            return true; // ~ 5 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPAP:
            return true; // ~ 10-20 Mbps
        case TelephonyManager.NETWORK_TYPE_IDEN:
            return false; // ~25 kbps
        case TelephonyManager.NETWORK_TYPE_LTE:
            return true; // ~ 10+ Mbps
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            return false;
        default:
            return false;
        }
    }

    /**
     * @author: lihs
     * @Title: isNetworkConnected
     * @Description: 是否有网络连接
     * @param context
     * @return true 有网络连接；false 没有网络连接
     * @date: 2013-8-2 下午1:52:32
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * @author: lihs
     * @Title: isWifiConnected
     * @Description: 当前是wifi网络
     * @param context
     * @return true wifi可用；false 不可用
     * @date: 2013-8-2 下午1:53:21
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @author: lihs
     * @Title: isWifiConnecting
     * @Description: wifi 是否连接上
     * @param context
     * @return
     * @date: 2013-8-22 下午2:20:25
     */
    public static boolean isWifiConnecting(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                if (NetworkInfo.State.CONNECTED == mWiFiNetworkInfo.getState()) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * @author: lihs
     * @Title: isMobileConnected
     * @Description:
     * @param context
     * @return
     * @date: 2013-8-2 下午2:00:03
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @author: lihs
     * @Title: getConnectedType
     * @Description: 获取当前网络类型
     * @param context
     * @return
     * @date: 2013-8-2 下午2:00:19
     */
    public static int getConnectedType(Context context) {
        if (context != null) {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

     
    public static final boolean isCanDownLoadData() {

//        String downLoadWay = CmccApplication.getPreference().getKeyValue(
//                PrefType.USER_DOWNLOAD_METHEDS, "0");
//        LogUtil.d("networkutil 下载方式：" + downLoadWay);
//        if (TextUtils.isEmpty(downLoadWay) || "0".equals(downLoadWay)) {
//            // 从未设置下载方式
//            return isWifiConnected(CmccApplication.getContext());
//        } else if ("1".equals(downLoadWay)) {
//            // 所有网络下载
//            return isNetworkConnected(CmccApplication.getContext());
//        } else if ("2".equals(downLoadWay)) {
//            // 禁用所有网络下载
//            return false;
//        }
        return false;
    }

    /**
     * @author: lihs
     * @Title: scanAroundInterNet
     * @Description: 扫描周围网络，扫描成功后系统会异步发送广播
     *               WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
     * @param context
     * @date: 2013-8-20 下午12:39:28
     */
    public static void scanAroundInterNet(Context context) {
        if (isNetworkConnected(context)) {
            if (!isWifiConnecting(context)) {
                // 有网络连接但不是Wifi连接 打开Wifi开始扫描网络
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                isMyOpenWiFi = true;
                wifiManager.startScan();
                LogUtil.d("开始扫描");
            }
        }
    } 
}
