package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class FitnessValueBean implements Serializable {

	/**
	 * 健康数据值表
	 */
	private static final long serialVersionUID = -6176408100059466279L;

	public String valueId;// ID
	public String fitnessIndexId;// 健康数据ID
	public String targetValue;// 值
	public String partyId;// 用户ID
	public String createdStamp;// 时间
}
