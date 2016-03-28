package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class DoctorReviewBean implements Serializable {

	/**
	 * 项目评论
	 */
	private static final long serialVersionUID = 4418793234890899911L;

	public String doctorReviewId;// ID
	public String projectId;// 项目ID
	public String commentsId;// 评论人ID
	public String commentsName;// 评论人姓名
	public String commentsTime;// 评论时间
	public String commentsTypeEnum;// 评论类型
	public String content;// 内容
	public String reviewSource;// 评论来源（1、编辑评论2、编委评论）
	public String reviewerId;// 审核者ID
	public String reviewerName;// 审核者姓名
	public String refuseReason;// 拒绝原因
	public String artStatusEnum;// 状态
	public String creditsNum;// 学分数
}
