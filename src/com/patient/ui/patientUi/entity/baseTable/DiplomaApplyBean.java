package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class DiplomaApplyBean implements Serializable {

	/**
	 * 赞助证书申请
	 */
	private static final long serialVersionUID = 5781955907264042725L;
	public String diplomaApplyiId;// ID
	public String applicantsId;// 申请者ID
	public String applicantsName;// 申请者姓名
	public String applicantsTime;// 申请时间
	public String applicantsUrl;// 申请者邮寄地址
	public String applicantsZipCode;// 申请者邮编
	public String appTypeEnum;// 申请类型
	public String appStatusEnum;// 申请状态
	public String assistId;// 赞助者ID
	public String assistName;// 赞助者姓名
	public String assistTime;// 赞助时间
	public String assistPhone;// 赞助者电话
	public String assistUrl;// 赞助者邮寄地址
	public String zipCode;// 赞助者邮编
	public String academicCoin;// 学术币
	public String deliveryTime;// 发货时间
}
