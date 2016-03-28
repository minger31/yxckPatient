package com.patient.ui.patientUi.activity.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.patient.commonent.TitleBar;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.yxck.patient.R;

/**
 * 我  消息
 * <dl>
 * <dt>ActMessages.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-12-21 下午5:35:55</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActMessages extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_message);
		TitleBar bar = getTitleBar();
		bar.setBack("返回", null, -1);
		bar.setTitle("我的消息", R.color.white);
		
		findViewById(R.id.details).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonUtil.showToast("查看详情");
			}
		});
	}
}
