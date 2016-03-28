package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class ProjectSponsorBean implements Serializable {

	/**
	 * 项目联合发起人
	 */
	private static final long serialVersionUID = 1775472747963565293L;
	public String projectSponId;// ID
	public String projectId;// 项目ID
	public String partyId;// 用户ID
	public String partyName;// 用户姓名
	public String isAgreeEnum;// 是否同意
}
