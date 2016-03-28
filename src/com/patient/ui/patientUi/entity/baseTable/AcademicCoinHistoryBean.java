package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class AcademicCoinHistoryBean implements Serializable {

	/**
	 * 学术币历史
	 */
	private static final long serialVersionUID = -4780623702387701905L;
	public String acaCoinHisId;// ID
	public String partyId;// 用户ID
	public String changeTime;// 消费时间
	public String money;// 金额   人民币
	public String coin;// 金币  系统金币
	public String ip;// IP
	public String serviceId;// 业务ID
	public String remarks;// 备注
	public String serviceTypeName;// 话费 消费
	public String createdStamp;
	public String serviceType;
}
