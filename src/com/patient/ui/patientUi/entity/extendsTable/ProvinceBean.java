package com.patient.ui.patientUi.entity.extendsTable;

import java.io.Serializable;

public class ProvinceBean implements Serializable {
	
	
//	 "geoId": "CN-21",
//     "wellKnownText": "Liaoning",
//     "geoIdFrom": "CHN",
//     "geoAssocTypeId": "REGIONS",
//     "geoName": "辽宁",
//     "abbreviation": "辽",
//     "geoCode": "21",
//     "geoTypeId": "PROVINCE"

	/**
	 * 省份
	 */
	private static final long serialVersionUID = -6176408100059466279L;
	public String geoId;// ID
	public String geoName;// 内容
	public String wellKnownText;//字母定位功能
	
	public String hospitalName;
	public String hospitalId;
	
	// 添加
	public String sortKey[];
	
	
}
 