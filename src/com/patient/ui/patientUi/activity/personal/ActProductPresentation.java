package com.patient.ui.patientUi.activity.personal;

import android.os.Bundle;

import com.patient.commonent.TitleBar;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.yxck.patient.R;

/**
 * 患者端产品介绍
 * <dl>
 * <dt>ActProductPresentation.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-11-18 下午1:47:38</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActProductPresentation extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_product_presentation);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("产品介绍", 1);
		 
		init();
	}

	private void init() {
		
	}
}
