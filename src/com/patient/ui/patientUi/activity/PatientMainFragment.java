package com.patient.ui.patientUi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.db.dao.PushMessageDaoImpl;
import com.patient.preference.LoginPreference;
import com.patient.service.PushIntentService;
import com.patient.ui.patientUi.activity.healthData.FragHealthData;
import com.patient.ui.patientUi.activity.lifeLine.FragLifeLine;
import com.patient.ui.patientUi.activity.patientsCircle.FragPatientsCircle;
import com.patient.ui.patientUi.activity.personal.FragPersonal;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.yxck.patient.R;

/**
 * 患者端主界面
 */
public class PatientMainFragment extends FragmentActivity implements
		OnClickListener {

	private TextView lifeLine = null;// 生命线
	private TextView healthData = null;// 健康数据
	private TextView patientsCircle = null;// 病友圈
	private TextView personal = null;// 我

	private FragmentManager fragmentManager = null;
	public static String TAB_INDICATOR_INDEX = "PatientMainFragment.indicator.index";

	public static String TAG_LIFELINE = FragLifeLine.class.getName();//生命线
	public static String TAG_HEALTHDATA = FragHealthData.class.getName();//健康数据
	public static String TAG_PATIENTSCIRCLE = FragPatientsCircle.class.getName();//病友圈
	public static String TAG_PERSONAL = FragPersonal.class.getName();// 我
 
	private static PatientMainFragment instance;
	
	private ImageView lifetime;
	private ImageView patientCircle;

	private PushAgent mPushAgent;
	private PushMessageDaoImpl dao = null;

	public static PatientMainFragment getInstance() {
		return instance;
	}

	private String currentTag = "";

	public String getCurrentTag() {
		return currentTag;
	}

	public boolean isCurrentTag(String currentPage) {
		if (currentPage.equals(currentTag)) {
			return true;
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;
		dao = new PushMessageDaoImpl(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonConstant.Action_PUSH_MSG);
		this.registerReceiver(new MyBroadCast(), filter);

		setContentView(R.layout.patient_mainfragment);
		if (savedInstanceState != null) {
			// 由于此activity被系统回收，保存了一些必要数据，再次回到此activity时，用于界面恢复
			currentTag = savedInstanceState.getString("CURRENTTAG");
		}

		// 初始化消息推送
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable(mRegisterCallback);
		mPushAgent.setPushIntentServiceClass(PushIntentService.class);
		if (mPushAgent.isRegistered()) {
			LogUtil.d(TAG, "已经注册成功");
			new AddAliasTask(LoginPreference.getUserInfo().partyId,
					ALIAS_TYPE.SINA_WEIBO).execute();
		}

		fragmentManager = this.getSupportFragmentManager();

		lifeLine = (TextView) this.findViewById(R.id.tvLifeLine);
		healthData = (TextView) this.findViewById(R.id.tvHealthData);
		patientsCircle = (TextView) this.findViewById(R.id.tvPatientsCircle);
		personal = (TextView) this.findViewById(R.id.tvPersonal);
		
		lifetime = (ImageView)findViewById(R.id.lifetime);
		patientCircle = (ImageView)findViewById(R.id.patient);

		findViewById(R.id.ll_LifeLine).setOnClickListener(this);
		findViewById(R.id.ll_HealthData).setOnClickListener(this);
		findViewById(R.id.ll_PatientsCircle).setOnClickListener(this);
		findViewById(R.id.lt_personal).setOnClickListener(this);

		initTag();

	}

	private void initTag() {

		if (!TextUtils.isEmpty(currentTag)) {

			if (currentTag.equals(TAG_LIFELINE)) {
				currentTag = "";
				changeTopIndicator(TAG_LIFELINE);
				CustomFragmentManager(FragLifeLine.class, null);
			} else if (currentTag.equals(TAG_HEALTHDATA)) {
				currentTag = "";
				changeTopIndicator(TAG_HEALTHDATA);
				CustomFragmentManager(FragHealthData.class, null);
			} else if (currentTag.equals(TAG_PATIENTSCIRCLE)) {
				currentTag = "";
				changeTopIndicator(TAG_PATIENTSCIRCLE);
				CustomFragmentManager(FragPatientsCircle.class, null);
			} else if (currentTag.equals(TAG_PERSONAL)) {
				currentTag = "";
				changeTopIndicator(TAG_PERSONAL);
				CustomFragmentManager(FragPersonal.class, null);
			}

		} else {
			int index = getIntent().getIntExtra(TAB_INDICATOR_INDEX, 0);
			switch (index) {

			case 1:
				changeTopIndicator(TAG_LIFELINE);
				CustomFragmentManager(FragLifeLine.class, null);
				break;
			case 2:
				changeTopIndicator(TAG_HEALTHDATA);
				CustomFragmentManager(FragHealthData.class, null);
				break;
			case 3:
				changeTopIndicator(TAG_PATIENTSCIRCLE);
				CustomFragmentManager(FragPatientsCircle.class, null);
				break;
			case 4:
				changeTopIndicator(TAG_PERSONAL);
				CustomFragmentManager(FragPersonal.class, null);
				break;
			default:
				changeTopIndicator(TAG_LIFELINE);
				CustomFragmentManager(FragLifeLine.class, null);
				break;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("CURRENTTAG", currentTag);
		super.onSaveInstanceState(outState);
	}

	private void changeTopIndicator(String tag) {

		lifeLine.setSelected(false);
		healthData.setSelected(false);
		patientsCircle.setSelected(false);
		personal.setSelected(false);

		if (TAG_LIFELINE.equals(tag)) {
			lifeLine.setSelected(true);
			currentTag = TAG_LIFELINE;
		} else if (TAG_HEALTHDATA.equals(tag)) {
			healthData.setSelected(true);
			currentTag = TAG_HEALTHDATA;
		} else if (TAG_PATIENTSCIRCLE.equals(tag)) {
			patientsCircle.setSelected(true);
			currentTag = TAG_PATIENTSCIRCLE;
		} else if (TAG_PERSONAL.equals(tag)) {
			personal.setSelected(true);
			currentTag = TAG_PERSONAL;
		}
	}

	public void CustomFragmentManager(Class<?> fragmemtClass, Bundle args) {

		String fragmentName = fragmemtClass.getName();
 
 
		Fragment lifeLine = fragmentManager.findFragmentByTag(FragLifeLine.class.getName());
		Fragment healthData = fragmentManager.findFragmentByTag(FragHealthData.class.getName());
		Fragment patientsCircle = fragmentManager.findFragmentByTag(FragPatientsCircle.class.getName());
		Fragment personal = fragmentManager.findFragmentByTag(FragPersonal.class.getName());

		FragmentTransaction ft = fragmentManager.beginTransaction();
		// 要显示的fragment
		Fragment currentFragment = fragmentManager
				.findFragmentByTag(fragmentName);

		if (fragmentName.equals(FragLifeLine.class.getName())) {
			if (healthData != null) {
				ft.hide(healthData);
			}
			if (patientsCircle != null) {
				ft.hide(patientsCircle);
			}
			if (personal != null) {
				ft.hide(personal);
			}

		} else if (fragmentName.equals(FragHealthData.class.getName())) {
			if (lifeLine != null) {
				ft.hide(lifeLine);
			}
			if (patientsCircle != null) {
				ft.hide(patientsCircle);
			}
			if (personal != null) {
				ft.hide(personal);
			}
		} else if (fragmentName.equals(FragPatientsCircle.class.getName())) {
			if (lifeLine != null) {
				ft.hide(lifeLine);
			}
			if (healthData != null) {
				ft.hide(healthData);
			}
			if (personal != null) {
				ft.hide(personal);
			}
		} else if (fragmentName.equals(FragPersonal.class.getName())) {
			if (lifeLine != null) {
				ft.hide(lifeLine);
			}
			if (healthData != null) {
				ft.hide(healthData);
			}
			if (patientsCircle != null) {
				ft.hide(patientsCircle);
			}
		}
		// 不能通过隐藏上一个标签来实现，必须显示一个，隐藏另外三个的方式。
		// 因为手机操作场景是非常复杂的，只隐藏lastTag的话，会导致切换时界面隐藏和显示的混乱
		// 比如关闭飞行模式时频繁切换
		if (currentFragment == null) {
			currentFragment = Fragment.instantiate(this, fragmentName, args);
			// ft.add(currentFragment, fragmentName);
			ft.add(R.id.fragment_container, currentFragment);
		} else {
			ft.show(currentFragment);
		}
		ft.commitAllowingStateLoss();
		fragmentManager.executePendingTransactions();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ll_LifeLine:
			if (currentTag.equals(TAG_LIFELINE)) {
				return;
			}
			changeTopIndicator(TAG_LIFELINE);
			CustomFragmentManager(FragLifeLine.class, null);
			break;
		case R.id.ll_HealthData:
			if (currentTag.equals(TAG_HEALTHDATA)) {
				return;
			}
			changeTopIndicator(TAG_HEALTHDATA);
			CustomFragmentManager(FragHealthData.class, null);
			break;
		case R.id.ll_PatientsCircle:
			if (currentTag.equals(TAG_PATIENTSCIRCLE)) {
				return;
			}
			
			dao.deleteMesgByType(PushIntentService.patient_circle);
			patientCircle.setVisibility(View.GONE);
			 
			changeTopIndicator(TAG_PATIENTSCIRCLE);
			CustomFragmentManager(FragPatientsCircle.class, null);
			break;
		case R.id.lt_personal:
			if (currentTag.equals(TAG_PERSONAL)) {
				return;
			}
			changeTopIndicator(TAG_PERSONAL);
			CustomFragmentManager(FragPersonal.class, null);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		super.onActivityResult(requestCode, arg1, data);
		// 重写
		if (PatientMainFragment.TAG_PATIENTSCIRCLE.equals(PatientMainFragment.getInstance().getCurrentTag())) {
			FragPatientsCircle.refresh(requestCode,  arg1,  data);
		}else if (PatientMainFragment.TAG_LIFELINE.equals(PatientMainFragment.getInstance().getCurrentTag())) {
			FragLifeLine.refresh(requestCode,  arg1,  data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean isPatientEdu = dao
				.getMessageByType(PushIntentService.new_inteoration)
				|| dao.getMessageByType(PushIntentService.new_treatment)
				|| dao.getMessageByType(PushIntentService.new_followup);
		if (isPatientEdu) {
			lifetime.setVisibility(View.VISIBLE);
		}else{
			lifetime.setVisibility(View.GONE);
		}
		
		if (dao.getMessageByType(PushIntentService.patient_circle)) {
			patientCircle.setVisibility(View.VISIBLE);
		}else{
			patientCircle.setVisibility(View.GONE);
		}
	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent i) {
			String action = i.getAction();
			if (CommonConstant.Action_PUSH_MSG.equals(action)) {
				showRedCircle();
			}
		}
	}



	private void showRedCircle() {

		if (CommonUtil.isTopActivity(PatientMainFragment.this,PatientMainFragment.class.getName())) {
			boolean isPatientEdu = dao
					.getMessageByType(PushIntentService.new_inteoration)
					|| dao.getMessageByType(PushIntentService.new_treatment)
					|| dao.getMessageByType(PushIntentService.new_followup);
			if (currentTag.equals(TAG_LIFELINE)) {
				// 看有没有 问诊 随访 ，有的话
				if (isPatientEdu) {
					lifetime.setVisibility(View.VISIBLE);
				}else{
					lifetime.setVisibility(View.GONE);
				}
			
			} else if (currentTag.equals(TAG_PATIENTSCIRCLE)) {
				if (dao.getMessageByType(PushIntentService.patient_circle)) {
					patientCircle.setVisibility(View.VISIBLE);
				}else{
					patientCircle.setVisibility(View.GONE);
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// 注册uemng 服务接收回调接口
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};
	public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

		@Override
		public void onRegistered(String registrationId) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					LogUtil.d(TAG, "注册消息推送成功");
					CommonUtil.showToast("注册消息推送成功");
					new AddAliasTask(LoginPreference.getUserInfo().partyId,
							ALIAS_TYPE.SINA_WEIBO).execute();
				}
			});
		}
	};

	private static final String TAG = PatientMainFragment.class.getName();

	class AddAliasTask extends AsyncTask<Void, Void, Boolean> {

		String alias;
		String aliasType;

		public AddAliasTask(String aliasString, String aliasTypeString) {

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

			CommonUtil.showToast("addalise " + result);
			Log.d(TAG, "alias was set successfully." + result);

		}

	}

	// 关闭uemng 消息通知
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
}
