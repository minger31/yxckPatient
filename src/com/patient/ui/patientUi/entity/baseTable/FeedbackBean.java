package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class FeedbackBean implements Serializable {

	/**
	 * 反馈意见
	 */
	private static final long serialVersionUID = 7634738487358965608L;
	public String feedbackId;// ID
	public String partyId;// 用户ID
	public String opinion;// 意见
	public String feedbackTime;// 反馈时间
	public String phone;// 联系电话
	public String appTypeEnum;// app类型
}
