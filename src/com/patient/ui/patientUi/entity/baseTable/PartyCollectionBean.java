package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class PartyCollectionBean implements Serializable {

	/**
	 * 用户收藏
	 */
	private static final long serialVersionUID = 1015904273498309167L;
	public String partyCollId;// ID
	public String partyId;// 用户ID
	public String collectType;// 业务类型(期刊、文章、项目)
	public String collectionId;// 被收藏ID
	public String collectionTime;// 收藏时间
}
