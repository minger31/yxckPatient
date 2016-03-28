package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class InitiateFollowupBean implements Serializable {

	/**
	 * 随访表
	 */
	private static final long serialVersionUID = 6317147667268048157L;
	public String initFollowupId;// ID
	public String patientId;// 患者ID
	public String doctorId;// 医生ID
	public String treatmentId;// 就诊ID
	public String followUpDes;// 随访描述
	public String callbackDes;// 回访描述
	public String followupStatus;// 状态（1、医生添加随访2、患者填写随访）
	public String diseaseId;// 疾病id
	public String modelId;// 模板ID
	public String followupType;// 随访类型(普通随访、微随访)
	public String followUpContent;// 微随访内容
	public String initiator;// 发起人（1、医生2、患者）
	public String createdStamp;// 时间
	public String followUpImage;//图片
	
	public PartyBean patientGv;
	public PartyBean doctorGv;
}
