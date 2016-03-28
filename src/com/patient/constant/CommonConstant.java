package com.patient.constant;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

// 通用常量参数值
public class CommonConstant {

	public static final Uri IMAGE_BASEURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final Uri VIDEO_BASEURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	/** 字符检索条 */
	public static final String[] LETTERS = { "-99", "A", "C", "E", "G", "I",
			"K", "M", "O", "Q", "S", "U", "W", "Y", "#" };
	public static final String letter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// 视频文件最大限制：10M
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
	// 图片文件最大限制：10M
	public static final long MAX_IMAGEFILE_SIZE = (long) 1.5 * 1024 * 1024;

	// 录音文件最大限制：6M
	public static final long RECORD_FILE_SIZE = 6 * 1024 * 1024;

	public static final int auto_focus = 1;
	public static final int decode = 2;
	public static final int decode_failed = 3;
	public static final int decode_succeeded = 4;
	public static final int encode_failed = 5;
	public static final int encode_succeeded = 6;
	public static final int launch_product_query = 7;
	public static final int quit = 8;
	public static final int restart_preview = 9;
	public static final int return_scan_result = 10;
	public static final int search_book_contents_failed = 11;
	public static final int search_book_contents_succeeded = 12;
	
	// 短信模板编号
	/** "5006" 医生主动注册 **/
	public static final String DOCTOR_SELF_REGISTER = "5006";
	/** 添加学术联络员 **/
	public static final String LEADER_ADD_ACADMIC = "5007";
	/** 学术联络员添加医生 **/
	public static final String ACADMIC_ADD_DOCTOR = "5008";
	/** 忘记密码 **/
	public static final String FORGET_PASSWORD = "5009";
	/** 兑换商品 **/
	public static final String EXCHANGE_PRODUCTOR = "6672";

	public static final String TIMEOUT = "网络异常，请稍后重试";
	public static final String UNKNOWHOST = "未知主机";
	public static final String INTERNET_INTERRUPT = "网络连接中断";

	/** 接口返回码 为 1 时 是成功的 ： 否则是失败的情况 **/
	public static final int SUCCESS = 1;
	public static final int NODATA = 2;
	public static final int AGAIN = 3;

	public static String CASHE_VIDEO = "/yxck_patient/VideoCache/";
	/** 缓存图片的路径 **/
	public static String IMAGE_CACHE_DIR = "yxck_patient/imageCache";
	public final static String downLoadUrl = Environment.getExternalStorageDirectory().getPath() + "/yxck_patient_apk";
	public static String SOFT_NAME = "yxck_patient";

	public static final int GET_AREA = 1;// 区域
	public static final int GET_DEPART = 2;
	public static final int GET_PROVINCE = 3;// 省份
	public static final int GET_CITY_PROVIENCE = 4;// 城市
	public static final int GET_HOSPITAL = 5;// 医院
	public static final int GET_STREAM = 6;// 科室
	public static final int GET_JOB = 7;// 职称
	public static final int GET_ENTERPRISE = 8;// 企业
	public static final int GET_CITY = 9;// 城市
	public static final int GET_GENDE = 10;// 性别
	public static final int GET_STREAM_TYPE = 11;// 科室类别
	public static final int GET_STREAM_TYPE_STREAM = 12;// 科室类别和科室

	public static final String  Action_PUSH_MSG = "action_push_msg";
	// 单点登录实现的挂广播机制
	public static final String ACTION_SIGNAL_LOGIN = "action_signal_login";
	// 注册时区分手机端调用的接口
	public static final String JSON = "Y";
	// 注册成功
	public static final String STATUS_SUCCESS = "success";
	// 注册失败
	public static final String STATUS_ERROR = "error";

	public static final String KEY_RESLUT = "key_reslut";
	public static final String KEY_ID = "key_id";
	public static final String SUCCESS_RESPONSEMESSAGE = "responseMessage";
	
	public static final String VALUE = "0";
	public static final String VALUE_TRUE = "true";
	
	
	/**
	 * 支付宝支付公用的参数
	 * 支付宝账号：admin@medref.cn
	 * 支付宝登录密码：ofbiz11.
	 * 支付宝支付密码  ofbiz11..
	 */
	//admin@medref.cn ofbiz11.
	// 2088711775736200 合作者身份id
	// pkcs8 加密私钥
//	MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMbue49uUDIjmE/36SchCgLbcQtcmgImxU1HiH9d/mxAByZuM9W3cxenShU98gXwKQDkdjwGJDd/oQuhncsvNaddNhPP3acHURPwOx/1/Ejklu1+bQjhz6/u0iybfv1UiK8fh0KXdk6g29lgRkKQd/ho5nGu5W8GSMYc9ayMOtPXAgMBAAECgYAP1GRSqoWJJ9nz6xdLfM6OIaYiMFE1AJ7MoneTIwie1o17GmPXisyGwlIb1J0QqeapYoSiDB8Pp4FKKqcixvPu5HJbX7EzTjN0Dyi5SWAD27uh+t+AWsktrHwuqv8GCn5YRAf8hgUnpiwyMLAGKDYBAKD/4yDFwHkIQNGtWQhOaQJBAPKwoPp/v3YHZifwY//s6idbGkWEhCL89FgUrDDiULGflyrET40em9hFv4gAacPZnsv6mnOzikP0/vZfWQMPVdMCQQDR13xBlIp5il2W98z9gbyDCfbvM6myCKjBTr+7kz9Wp/bNbcCgISChBy9A3w/RE+h7ACNpTmuYUUmvpcf9VPNtAkB95d881DSwuBnosi5OP8sg64PveFVMCUmAwAiru1v9KTs0dm6GWNCkG1rG5N/5N9XHH5QsXYFmy5vQl5oHmlb9AkA6RWOM9onJnIHGxYNYNmm53c+Dt039UWhDXfHiTc596uCfRUYDcY+VB+C4W/kRbzzUw0ISISPBxXj33Mp/b9K1AkBxgVJ4WslDdHvocjgyJa3caq/0zrfWsCxRfaPyNuaO8wbfTqzSdXH3ybNflsYm9x2pf5AY9Inn6OrqnTgOaZty
//  支付宝公钥
//	MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDG7nuPblAyI5hP9+knIQoC23ELXJoCJsVNR4h/Xf5sQAcmbjPVt3MXp0oVPfIF8CkA5HY8BiQ3f6ELoZ3LLzWnXTYTz92nB1ET8Dsf9fxI5Jbtfm0I4c+v7tIsm379VIivH4dCl3ZOoNvZYEZCkHf4aOZxruVvBkjGHPWsjDrT1wIDAQAB
	public static final String PARTNER = "2088711775736200";
	public static final String SELLER = "admin@medref.cn";
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMbue49uUDIjmE/36SchCgLbcQtcmgImxU1HiH9d/mxAByZuM9W3cxenShU98gXwKQDkdjwGJDd/oQuhncsvNaddNhPP3acHURPwOx/1/Ejklu1+bQjhz6/u0iybfv1UiK8fh0KXdk6g29lgRkKQd/ho5nGu5W8GSMYc9ayMOtPXAgMBAAECgYAP1GRSqoWJJ9nz6xdLfM6OIaYiMFE1AJ7MoneTIwie1o17GmPXisyGwlIb1J0QqeapYoSiDB8Pp4FKKqcixvPu5HJbX7EzTjN0Dyi5SWAD27uh+t+AWsktrHwuqv8GCn5YRAf8hgUnpiwyMLAGKDYBAKD/4yDFwHkIQNGtWQhOaQJBAPKwoPp/v3YHZifwY//s6idbGkWEhCL89FgUrDDiULGflyrET40em9hFv4gAacPZnsv6mnOzikP0/vZfWQMPVdMCQQDR13xBlIp5il2W98z9gbyDCfbvM6myCKjBTr+7kz9Wp/bNbcCgISChBy9A3w/RE+h7ACNpTmuYUUmvpcf9VPNtAkB95d881DSwuBnosi5OP8sg64PveFVMCUmAwAiru1v9KTs0dm6GWNCkG1rG5N/5N9XHH5QsXYFmy5vQl5oHmlb9AkA6RWOM9onJnIHGxYNYNmm53c+Dt039UWhDXfHiTc596uCfRUYDcY+VB+C4W/kRbzzUw0ISISPBxXj33Mp/b9K1AkBxgVJ4WslDdHvocjgyJa3caq/0zrfWsCxRfaPyNuaO8wbfTqzSdXH3ybNflsYm9x2pf5AY9Inn6OrqnTgOaZty";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDG7nuPblAyI5hP9+knIQoC23ELXJoCJsVNR4h/Xf5sQAcmbjPVt3MXp0oVPfIF8CkA5HY8BiQ3f6ELoZ3LLzWnXTYTz92nB1ET8Dsf9fxI5Jbtfm0I4c+v7tIsm379VIivH4dCl3ZOoNvZYEZCkHf4aOZxruVvBkjGHPWsjDrT1wIDAQAB";

	/** 健康数据 */
	public static final String TARGETID_INFO = "fi_10000";// targetName="个人信息"
	public static final String TARGETID_BLOOD_TYPE = "fi_10001";// targetName="血型"
	public static final String TARGETID_STATURE = "fi_10002";// targetName="身高"
	public static final String TARGETID_WEIGHT = "fi_10003";// targetName="体重"
	public static final String TARGETID_BODY_MASS_INDEX = "fi_10004";// targetName="体重指数"
	public static final String TARGETID_ANIMAL_HEAT = "fi_10005";// targetName="体温"
	public static final String TARGETID_BREATHE = "fi_10006";// targetName="呼吸"
	public static final String TARGETID_HEART_RATE = "fi_10007"; // targetName="心率"
	public static final String TARGETID_OXYGEN_CONTENT = "fi_10008";// targetName="含氧量"
	public static final String TARGETID_BLOOD_PRESSURE = "fi_10009"; // targetName="血压"
	public static final String TARGETID_BLOOD_GLUCOSE = "fi_10010"; // targetName="血糖"

	public static final int STATURE = 2;//身高
	public static final int WEIGHT = 3;//体重
	public static final int BODY_MASS_INDEX = 4;//体重指数
	public static final int ANIMAL_HEAT = 5;//体温
	public static final int BREATHE = 6;//呼吸
	public static final int HEART_RATE = 7;//心率
	public static final int OXYGEN_CONTENT = 8;//含氧量
	public static final int BLOOD_PRESSURE = 9;//血压
	public static final int BLOOD_GLUCOSE = 10;//血糖

}