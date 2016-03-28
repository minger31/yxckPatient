package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class SponsorshipBean implements Serializable {

	/**
	 * 赞助记录表
	 */
	private static final long serialVersionUID = -2167392081297448324L;
	public String sponsorshipId;// ID
	public String partyId;// 用户Id
	public String beSponsorshipId;// 被赞助用户Id
	public String sponsorshipMoney;// 赞助金额
	public String sponsorshipTime;// 赞助时间
}
