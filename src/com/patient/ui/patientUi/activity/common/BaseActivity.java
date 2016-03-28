package com.patient.ui.patientUi.activity.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;

public class BaseActivity extends Activity  {

	private MyBroadCast instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonConstant.ACTION_SIGNAL_LOGIN);
		this.registerReceiver(instance, filter);
	}

	private TitleBar titleBar = null;

	public TitleBar getTitleBar() {
		if (titleBar == null) {
			titleBar = new TitleBar(this,
					((ViewGroup) findViewById(android.R.id.content))
							.getChildAt(0));
		}
		return titleBar;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		 if (instance != null) {
	            this.unregisterReceiver(instance);
	        }
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CommonConstant.ACTION_SIGNAL_LOGIN.equals(action)) {
				tipLogin("您的账号已在异地登陆，您已经被迫下线");
			}
		}
	}

	/**
	 * @author: lihs
	 * @Title: tipLogin
	 * @Description: 推出登录
	 * @date: 2013-9-11 下午6:25:31
	 */

	private CommonDialog dialog;

	private void tipLogin(String text_context) {

		dialog = new CommonDialog(BaseActivity.this);
		// 屏蔽返回键
		dialog.setCancelable(false);
		dialog.setTitle("提醒");
		dialog.setMessage(text_context);
		dialog.setPositiveButton(new CommonDialog.BtnClickedListener() {
			@Override
			public void onBtnClicked() {
				// 发广播强制登陆广播
				dialog.dismiss();
			}
		}, "确定");

		if (dialog != null && dialog.isShowing()) {

		} else {
			if (!isFinishing()) {
				dialog.showDialog();
			}
		}
	}
}
