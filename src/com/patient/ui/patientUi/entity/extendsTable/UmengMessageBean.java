package com.patient.ui.patientUi.entity.extendsTable;

import java.io.Serializable;

public class UmengMessageBean implements Serializable {

	/**
	 * umeng 消息推送
	 */
	private static final long serialVersionUID = 3111328894393488316L;
	
	public String alias;// 这个应该是 绑定的登陆账号进行筛选发送
	public MessageBody body;// 消息体，主要用来通知栏显示用的
	public ExtraMessage extra;// 附加消息

}
