package com.patient.db.dao;

import com.patient.ui.patientUi.entity.extendsTable.RedDotBean;

public interface PushMessageDao {

	boolean getMessageByType(String pushType);
	boolean insertMessage(RedDotBean bean);
	boolean deleteMesgByType(String pushType);
	
}
