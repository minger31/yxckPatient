package com.patient.preference;

import android.content.Context;

import com.patient.PatientApplication;

public class UserInfoPreference extends BasePreference {

	private static UserInfoPreference infoPre;

	public static UserInfoPreference getInstance() {
		if (infoPre == null) {
			infoPre = new UserInfoPreference(PatientApplication.getContext());
		}
		return infoPre;
	}

	private static final String INFO_PREFERENCE_NAME = "cmcc_info_prefer";

	public enum InfoType {
		HEAD,//头像
		ENTERPRISE,// 企业
		NAME,// 姓名
		PHONE,// 手机�?		AREA,// 区域
		STREAM,// 科室
		ADDRESS,// 地址
		SEX,// 性别
		PROVINCE,// 省份
		CITY,// 城市
		HOSPITAL,// 医院
		STREAMTYPE,// 科室类别
		JOB,// 职称
		EMAIL,// 邮箱
		VALUE,// 缓存用的
		SURVEY;// 缓存用的调研
	}

	public UserInfoPreference(Context context) {
		super(context, INFO_PREFERENCE_NAME);
	}

	public void setString(InfoType keyType, String value) {
		super.setString(keyType.name(), value);
	}

	public String getString(InfoType keyType, String defaultValue) {
		String secrecyString = super.getString(keyType.name(), defaultValue);
		if (secrecyString == null)
			return defaultValue;
		return secrecyString;
	}

	public static String getKeyValue(InfoType secrecyKeyType,
			String defaultValue) {
		return getInstance().getString(secrecyKeyType, defaultValue);
	}

	/**
	 * 
	 * @author: dell
	 * @Title: clearLoginPreference 
	 * @Description: TODO 
	 * @date: 2014-9-22 下午10:44:14
	 */
	public void clearLoginPreference() {
		UserInfoPreference.getInstance().setString(InfoType.ENTERPRISE,"");
		UserInfoPreference.getInstance().setString(InfoType.JOB, "");
		UserInfoPreference.getInstance().setString(InfoType.PHONE, "");
		UserInfoPreference.getInstance().setString(InfoType.NAME, "");
		UserInfoPreference.getInstance().setString(InfoType.HEAD, "");
		UserInfoPreference.getInstance().setString(InfoType.EMAIL, "");
		UserInfoPreference.getInstance().setString(InfoType.STREAMTYPE, "");
		UserInfoPreference.getInstance().setString(InfoType.HOSPITAL, "");
		UserInfoPreference.getInstance().setString(InfoType.VALUE, "");
		UserInfoPreference.getInstance().setString(InfoType.SURVEY, "");
	}
}
