package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class TreatmentBean implements Serializable {

	/**
	 * 就诊
	 */
	private static final long serialVersionUID = 7548706561260962025L;
	public String treatmentId;// ID
	public String patientId;// 患者ID
	public String groupId;// 组ID
	public String doctorId;// 医生ID
	public String treatmentTime;// 就诊时间
	public String patientDisease;// 所患疾病
	public String diseaseId;// 疾病id
	public String report;// 主诉
	public String medication;// 用药
	public String comment;// 备注
	public String status;// 状态（1、患者填2、医生改3、TreatmStatusEnum_2时，显示完善资料按钮）
	
	public String treatmentCardId;// 就诊卡号
	public String soundRecorder;// 就诊录音文件地址
	public String cureImages;// 治疗图片 逗号拆分
	public String examineImages;// 检擦图片 逗号拆分
	public String basicImage;// 基本 逗号拆分

	public PartyBean doctorGv; 
	public PartyBean patientGv;
}
