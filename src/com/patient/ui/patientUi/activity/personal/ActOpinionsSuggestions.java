package com.patient.ui.patientUi.activity.personal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.patient.commonent.TitleBar;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.yxck.patient.R;

/**
 * 患者端意见建议
 * <dl>
 * <dt>ActOpinionsSuggestions.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-11-18 下午1:47:19</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActOpinionsSuggestions extends BaseActivity {

	private EditText phone = null;// 手机号
	private Button submitBtn = null;// 提交按钮
	
	//登录者的手机号
	private String party_id = LoginPreference.getKeyValue( LoginType.PARTY_ID, "");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_opinions_suggestions);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("意见建议", 1);
		 
		init();
	}

	private void init() {
		phone = (EditText)findViewById(R.id.phone);
		//未登录用户显示此项目
		if (TextUtils.isEmpty(party_id)) {
			phone.setVisibility(View.VISIBLE);
		}
		submitBtn = (Button)findViewById(R.id.submitBtn);
		submitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonUtil.showToast("点击了提交按钮");
			}
		});
	}
}
