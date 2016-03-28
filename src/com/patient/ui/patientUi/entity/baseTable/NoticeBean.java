package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class NoticeBean implements Serializable{
	
	/**
	 * 公告
	 */
	public static final long serialVersionUID = 1279929726146586621L;
	
	public String noticeId;//ID
	public String title;//标题 
	public String content;//内容 
	public String issueTime;//发布时间 
	public String appTypeEnum;//app类型 
	
}
