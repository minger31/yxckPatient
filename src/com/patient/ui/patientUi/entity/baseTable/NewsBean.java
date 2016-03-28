package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class NewsBean implements Serializable {

	/**
	 * 消息1
	 */
	private static final long serialVersionUID = -4170712309109168642L;
	public String newsId;// ID
	public String fromUserId;// 消息来自ID
	public String toUserId;// 消息去向ID
	public String content;// 内容
	public String type;// 类型
	public String isReadEnum;// 是否阅读
	public String appType;// app类型
}
