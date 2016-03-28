package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class ConcernBean implements Serializable {

	/**
	 * 关注表
	 */
	private static final long serialVersionUID = -5877904926428967640L;
	public String concernId;// ID
	public String beConcernId;// 被关注用户Id
	public String partyId;// 关注用户Id
}
