package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class TargetBean implements Serializable {

	/**
	 * 指标表
	 */
	private static final long serialVersionUID = 1855810281123165088L;
	public String targetId;// ID
	public String targetName;// 指标名称
	public String targetUnit;// 单位
	public String targetDes;// 指标解释
	public String deviceId;// 设备Id
	public String targetType;// 指标类型（1、疾病指标 2、健康数据指标）
}
