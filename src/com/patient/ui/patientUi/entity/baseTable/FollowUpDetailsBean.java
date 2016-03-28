package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class FollowUpDetailsBean implements Serializable {

	/**
	 * 随访详情表
	 */
	private static final long serialVersionUID = 6844089362810561427L;
	public String detailId;// ID
	public String initFollowupId;// 随访ID
	public String targetId;// 指标ID
	public String targetName;// 指标名称
	public String targetCount;// 次数
	public String targetUnit;// 单位
}
