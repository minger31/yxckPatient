package com.patient.ui.patientUi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.patient.service.PushIntentService;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.yxck.patient.R;

public class TestPush extends BaseActivity {
	
	private static final String TAG = TestPush.class.getName();
	private PushAgent mPushAgent;
	TextView tv;
	
	
	private void updateStatus() {
		String pkgName = getApplicationContext().getPackageName();
		String info = String.format("enabled:%s  isRegistered:%s  DeviceToken:%s",
				mPushAgent.isEnabled(), mPushAgent.isRegistered(),
				mPushAgent.getRegistrationId());
		tv.setText("应用包名："+pkgName+"\n"+info);
		
		Log.i(TAG, "updateStatus:" + String.format("enabled:%s  isRegistered:%s",
				mPushAgent.isEnabled(), mPushAgent.isRegistered()));
		Log.i(TAG, "=============================");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push);
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable(mRegisterCallback);
		mPushAgent.setPushIntentServiceClass(PushIntentService.class);
		
		if (mPushAgent.isRegistered()) {
			LogUtil.d(TAG, "注册消息推送成功");
		}
		
		  tv = (TextView)findViewById(R.id.tv);
	}
	// 注册uemng 服务接收回调接口
		public Handler handler = new Handler();
		public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
			
			@Override
			public void onRegistered(String registrationId) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						
						updateStatus();
						LogUtil.d(TAG, "注册消息推送成功");
						CommonUtil.showToast("注册消息推送成功");
						new AddAliasTask("15855131332", ALIAS_TYPE.SINA_WEIBO).execute();
					}
				});
			}
		};
		
		//关闭uemng 消息通知
		public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {
			
			@Override
			public void onUnregistered(String registrationId) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
					}
				});
			}
		};
		
		
		
		class AddAliasTask extends AsyncTask<Void, Void, Boolean>{
				
				String alias;
				String aliasType;
				
				public AddAliasTask(String aliasString,String aliasTypeString) {
				 
					this.alias = aliasString;
					this.aliasType = aliasTypeString;
				}
				
				protected Boolean doInBackground(Void... params) {
					try {
						return mPushAgent.addAlias(alias, aliasType);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					 
					CommonUtil.showToast("addalise "+result);
					Log.d(TAG, "alias was set successfully." + result);
						
				    }

			}
			
}
