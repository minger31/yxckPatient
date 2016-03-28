package com.patient.ui.patientUi.entity.extendsTable;

import java.io.Serializable;

public class DoctorSchduleBean implements Serializable {

	/**
	 * 医生的开放日程 时间表
	 */
	private static final long serialVersionUID = 5937107295578455245L;
	
	public String dateTime;
	public String am;
	public String pm;
	public String day;
	public String selected;//,"上午":"dayType_1","下午":"dayType_2","全天":"dayType_3"
	public String week;

}
