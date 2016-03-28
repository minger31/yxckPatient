package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class OpenScheduleBean implements Serializable {

	/**
	 * 查看日程
	 */
	private static final long serialVersionUID = -5653006074025045326L;
	public String openScheduleId;// ID
	public String doctorId;// 医生ID
	public String interrogationId;// 问诊ID
	public String patientId;// 患者Id
	public String openDate;// 开放日期
	public String openWeekEnum;// 日类型
}
