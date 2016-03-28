package com.patient.db.dao;

import java.util.List;

import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;

public interface DiseaseDao {

	// 保存所有的疾病
	public boolean saveDisease(List<DiseaseBean> data);
	// 根据条件获取病的名字
	public List<DiseaseBean> getAllDisease(String condition);
}
