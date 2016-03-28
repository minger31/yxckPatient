package com.patient.db.dao;

import java.util.List;

import com.patient.ui.patientUi.entity.baseTable.ProvCityBean;

public interface PchDao {

	// 查询省市通用类
	public List<ProvCityBean> getProCityData(String condition);
}
