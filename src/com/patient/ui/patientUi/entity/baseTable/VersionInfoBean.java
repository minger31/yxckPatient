package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class VersionInfoBean implements Serializable {

	/**
	 * 版本信息
	 */
	private static final long serialVersionUID = -492993295326629596L;
	public String versionInfoId;// ID
	public String versionNum;// 版本号
	public String url;// 文件路径
	public String remarks;// 备注
	public String appTypeYH;// 医生-患者端
	public String appTypeAI;// 安卓-ios端
}
