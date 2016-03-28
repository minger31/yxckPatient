package com.patient.common.iflytek;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;
import com.yxck.patient.R;

public class SpeechApp extends Application {

	@Override
	public void onCreate() {
		SpeechUtility.createUtility(SpeechApp.this, "appid=" + getString(R.string.app_id));
		super.onCreate();
	}
}
