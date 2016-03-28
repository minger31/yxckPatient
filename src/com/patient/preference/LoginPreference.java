package com.patient.preference;

import android.content.Context;

import com.patient.PatientApplication;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;

/**
 * <dl>
 * <dt>LoginPreference.java</dt>
 * <dd>Description:云电话登陆相关参数的保存：主要是保存 登陆的手机号，和登陆的密码，以及tokenid</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:  </dd>
 * <dd>CreateDate: 2013-7-31 下午1:29:31</dd>
 * </dl>
 * 
 * @author lihs
 */
public class LoginPreference extends BasePreference {

	private static LoginPreference loginPre;

	public static LoginPreference getInstance() {
		if (loginPre == null) {
			loginPre = new LoginPreference(PatientApplication.getContext());
		}
		return loginPre;
	}

	// 当前用户登录的信息，已经完善过个人信息的用户在登录后会返回给我，否则
	private static PartyBean userInfo = null;

	public static PartyBean getUserInfo() {
		if (userInfo == null) {
			userInfo = new PartyBean();
		}
		return userInfo;
	}

	public static void setUserInfo(PartyBean userInfo) {
		LoginPreference.userInfo = userInfo;
	}
		
	private static final String LOGIN_PREFERENCE_NAME = "cmcc_login_prefer";

	public enum LoginType {

		LOGIN_PHONE, // 登陆手机�?		LOGIN_PASSWORD, // 登陆密码
		PARTY_ROLE_ID, // 角色ID @CommonConstant.ROLE_LEADER
		USER_NAME, // 登陆的用户名
		PARTY_ID, // 会员Id
		HEAD_URL, // 头像URL
		PIC_NAME, // 头像名称
		LEVEL, // 级别
		PARENT_PARTY_ID, // 医生独有的字�?		CUS_REGION_ID, // 学术联络员区域ID
		DEPT_ID, // 学术联络员部门ID
		GROUP_ID, // 项目负责�?		APPID,// 应用ID
		APPNAME,// 应用title 名字
		FIRST_LOGIN,//首次登陆
		WEATHER_BIND_ACADMIC,// 是否绑定学术联络�?		JSESSION_ID,// 有效期id
		CCME_APK_ID,// 下载apk的id
		KEY_DOWN_LOAD_APK_FILE_PATH// 下载后的本地apk地址
	}

	public LoginPreference(Context context) {
		super(context, LOGIN_PREFERENCE_NAME);
	}

	public void setString(LoginType keyType, String value) {
		super.setString(keyType.name(), value);
	}

	public String getString(LoginType keyType, String defaultValue) {
		String secrecyString = super.getString(keyType.name(), defaultValue);
		if (secrecyString == null)
			return defaultValue;
		return secrecyString;
	}

	public static String getKeyValue(LoginType secrecyKeyType,
			String defaultValue) {
		return getInstance().getString(secrecyKeyType, defaultValue);
	}

	/**
	 * @author: lihs
	 * @Title: clearLoginPreference
	 * @Description: �?��登录时�?清空缓存的信�?	 * @date: 2013-8-9 下午1:55:02
	 */
	public void clearLoginPreference() {
		
	}
}
