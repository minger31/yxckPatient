package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class InterrogationBean implements Serializable {

	/**
	 * 问诊
	 */
	private static final long serialVersionUID = 4795900718743448286L;
	public String interrogationId;// ID
	public String description;// 描述
	public String createTime;// 创建时间
	
	public String doctorId;// 医生ID
	public String patientId;// 患者Id
	public String susDiagnoEnum;// 疑似诊断
	public String provinceId;// 省ID
	public String cityId;// 市ID
	public String hospitalId;// 医院ID
	public String departmentId;// 科室ID
	
	public String dataStatus;// 状态（1、开启2、关闭）
	
	public String basicImage;// 
	
}
