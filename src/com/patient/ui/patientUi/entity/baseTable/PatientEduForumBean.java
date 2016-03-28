package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class PatientEduForumBean implements Serializable {

	/**
	 * 患教评论
	 */
	private static final long serialVersionUID = 6742507784788389947L;
	public String forumId;// ID
	public String commentsId;// 评论人ID
	public String commentsName;// 评论人姓名
	public String patientEduId;// 患教文章ID
	public String commentsTime;// 评论时间
	public String content;// 内容
	
	public PartyBean partyGv;
}
