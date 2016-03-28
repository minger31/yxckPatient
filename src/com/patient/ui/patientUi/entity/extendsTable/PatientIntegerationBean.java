package com.patient.ui.patientUi.entity.extendsTable;

import java.util.ArrayList;
import java.util.List;

import com.patient.ui.patientUi.entity.baseTable.InterrogationBean;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;

public class PatientIntegerationBean extends InterrogationBean {

	/**
	 * 患者端问诊数据列表
	 */
	private static final long serialVersionUID = 2558718706408749586L;
	
	public List<PartyBean> openScheduleList;// 开放日程的医生列表  

	public List<PartyBean> getOpenScheduleList() {
		if (openScheduleList == null) {
			openScheduleList = new ArrayList<PartyBean>();
		}
		return openScheduleList;
	}
 
}
