package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class FollowUpValueBean implements Serializable {

	/**
	 * 指标值表
	 */
	private static final long serialVersionUID = 4672928269077317785L;
	public String valueId;// ID
	public String detailId;// 随访详情ID
	public String targetValue;// 值
	public String targetUnit;// 单位
	public String testTime;// 检测时间
}
