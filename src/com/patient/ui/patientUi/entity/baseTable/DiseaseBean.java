package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class DiseaseBean implements Serializable {

	/**
	 * 疾病表
	 */
	private static final long serialVersionUID = -7621884753903756866L;
	public String diseaseId;// ID
	public String partyId;// 医生Id
	public String indexName;// 疾病名称
	public String description;// 疾病备注
	public String indexSpell;// 疾病名称的拼音
}
