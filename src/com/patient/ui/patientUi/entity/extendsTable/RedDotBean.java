package com.patient.ui.patientUi.entity.extendsTable;

import java.io.Serializable;

import android.content.ContentValues;

import com.patient.db.table.PushMsgTable;

public class RedDotBean implements Serializable {

	/**
	 *  附加的额外消息，包括消息类型 和消息内容实体解析完成后，跳转到指定的界面
	 */
	private static final long serialVersionUID = 5719421190289510658L;
	
	public String messageType;
	public String messageId;
	public String readStatus;// 1未读取，否
	public String academicCoin;
	public String id;
	
	public static ContentValues parseToValue(RedDotBean bean){
		
		ContentValues values = new ContentValues();
		values.put(PushMsgTable.MESSAGEID, bean.messageId);
		values.put(PushMsgTable.MESSAGETYPE, bean.messageType);
		values.put(PushMsgTable.READSTATUS, bean.readStatus);
		values.put(PushMsgTable.id, bean.id);
		return values;
	}

}
