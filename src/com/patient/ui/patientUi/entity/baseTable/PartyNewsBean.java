package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class PartyNewsBean implements Serializable {

	/**
	 * 给用户的消息
	 */
	private static final long serialVersionUID = -8521442303510739200L;
	public String partyNewsId;// ID
	public String serviceId;// 业务ID
	public String manage;// 信息
	public String type;// 类型
	public String isReadEnum;// 是否阅读
}
