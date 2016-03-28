package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class FitnessIndexBean implements Serializable {

	/**
	 * 健康数据指标表
	 */
	private static final long serialVersionUID = -9166205830888822855L;
	public String fitnessIndexId;// ID.
	public String fitnessDataId;// 健康数据表ID
	public String targetId;// 指标ID.
	public String targetName;// 指标名称.
	public String parentId;//父ID
}
