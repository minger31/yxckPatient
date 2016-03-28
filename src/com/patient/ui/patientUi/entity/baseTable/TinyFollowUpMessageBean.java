package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class TinyFollowUpMessageBean implements Serializable {

	/**
	 * 微随访消息表
	 */
	private static final long serialVersionUID = -6176408100059466279L;

	public String tinyMsgId;// ID
	public String initFollowupId;// 随访ID
	public String content;// 消息内容
	public String tinyMsgImage;// 图片
	public String launchId;// 发送者ID
	public String receiveId;// 接收者ID
	public String createdStamp;// 发送时间
}
