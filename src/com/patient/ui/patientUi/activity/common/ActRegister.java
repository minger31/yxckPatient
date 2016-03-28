package com.patient.ui.patientUi.activity.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ActRegister.java</dt>
 * <dd>Description:患者端注册</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-11-17 上午10:05:33</dd>
 * </dl>
 */
public class ActRegister extends BaseActivity {

	private static final String TAG = ActRegister.class.getName();

	private EditText regPhone = null;//手机
	private EditText smsCode = null;//验证码

	private String regPhoneStr = null;
	private String smsCodeStr = null;
	
	// 距离下次注册倒计时
	private TextView countTimeTv;
	private int MSG_WHAT_GETNUBE = 0;
	private long TWO_MINITES = 2 * 60;// 2分钟

	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_WHAT_GETNUBE) {
				long internalTime = (Long) msg.obj;
				refreshCountTime(internalTime);
				if (myHandler != null) {
					Message msg1 = myHandler.obtainMessage();
					msg1.what = MSG_WHAT_GETNUBE;
					msg1.obj = internalTime - 1;
					myHandler.sendMessageDelayed(msg1, 1000);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		LogUtil.d(TAG, TAG + "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_register);

		TitleBar title = getTitleBar();
		title.setBack("返回", null, -1);

		initUI();
		// 注册新用户
		title.setTitle(getResources().getString(R.string.register),
					R.color.white);
	}

	private void initUI() {

		regPhone = (EditText) findViewById(R.id.et_register_phone);
		smsCode = (EditText) findViewById(R.id.et_code);
		countTimeTv = (TextView) findViewById(R.id.tv_get_code);

		countTimeTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 获取验证码
				getSmsVerfication();
			}
		});

		findViewById(R.id.btn_submit_register).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						// 提交注册
						submitRegister();
					}
				});
	}
	String vCode;
	private void getSmsVerfication() {

		if (myHandler != null) {
			myHandler.removeMessages(MSG_WHAT_GETNUBE);
		}

		String registerPho = regPhone.getText().toString();

		if (TextUtils.isEmpty(registerPho)) {
			CommonUtil.showToast("请输入手机号",ActRegister.this);
		} else if (!registerPho.matches("^[1][0-9]{10}$")) {
			CommonUtil.showToast("请输入合法的手机号",this);
		} else { 
			final CommonWaitDialog wdg = new CommonWaitDialog(this, "发送中...");
			params = new RequestParams();
			// 掉获取短信验证码接口获取短信验证码
			params.addQueryStringParameter("userLoginId", registerPho);
			params.addQueryStringParameter("type",CommonConstant.DOCTOR_SELF_REGISTER);
			HttpUtils http = new HttpUtils();
			http.configTimeout(15 * 1000);
			http.send(HttpMethod.POST, UrlConstant.GET_VERIFICATION_CODE,
					params, new RequestCallBack<Object>() {

						@Override
						public void onStart() {

						}

						@Override
						public void onSuccess(Object result) {
							wdg.clearAnimation();
							countTimeTv.setEnabled(false);

							if (result != null) {
								JSONObject json;
								try {
									json = new JSONObject(result.toString());
									int status = json.optInt("status");
									String msgs = json.optString("msg");
									vCode = json.optString("outputList");
									if (status == 1) {
										CommonUtil.hideSoftInputFromWindow(ActRegister.this);
										CommonUtil.showToast("验证码已发送到您的手机，请注意查收",ActRegister.this);
										// 解析返回的数据类型
										refreshCountTime(TWO_MINITES);
										if (myHandler != null) {
											Message msg = myHandler.obtainMessage();
											msg.what = MSG_WHAT_GETNUBE;
											msg.obj = TWO_MINITES - 1;
											myHandler.sendMessageDelayed(msg, 1000);
										}
									}else {
										countTimeTv.setEnabled(true);
										CommonUtil.showToast(msgs,ActRegister.this);
									}
								} catch (JSONException e) {
									wdg.clearAnimation();
									e.printStackTrace();
								}

							}
						}

						@Override
						public void onFailure(Throwable error, String msg) {
							wdg.clearAnimation();
							CommonUtil.showError(error, msg);
						}
					});
		}

	}

	RequestParams params = null;
	private void submitRegister() {

		regPhoneStr = regPhone.getText().toString();
		smsCodeStr = smsCode.getText().toString();

		if (TextUtils.isEmpty(regPhoneStr)) {
			CommonUtil.showToast("请输入手机号");
		} else if (!regPhoneStr.matches("^[1][0-9]{10}$")) {
			CommonUtil.showToast("请输入合法的手机号");
		} else if (TextUtils.isEmpty(smsCodeStr)) {
			CommonUtil.showToast("请输入短信验证码");
		} else if (!smsCodeStr.matches("^[0-9a-zA-Z]{6}")) {
			CommonUtil.showToast("短信验证码长度为6位");
		} else {

			if (smsCodeStr.equals(vCode)) {
				// 调用注册接口进行注册
				register();
			} else {
				CommonUtil.showToast("验证码不正确");
			}
		}
	}

	// 调用注册接口进行注册
	private void register() {
		params = new RequestParams();
		params.addQueryStringParameter("phone1", regPhoneStr);
		params.addQueryStringParameter("currentPassword", smsCodeStr);
		params.addQueryStringParameter("partyFlag", "N");

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_OUTSIDE_EXPERTS, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(Object result) {
						if (result != null) {
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");
								String msg = json.optString("errorMessage");
								if (code.equals(CommonConstant.STATUS_SUCCESS)) {
									// 解析返回的数据类型
									CommonUtil.showToast("注册成功");
									finish();
								}else if (code.equals(CommonConstant.STATUS_ERROR)) {
									CommonUtil.showToast(msg);
								}else {
									CommonUtil.showToast("网络繁忙，请稍后再试");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {

						CommonUtil.showError(error, msg);
					}
				});
	}

	private void refreshCountTime(long time) {

		String format = "<font color = '#ff0000'>" + time + "</font>"
				+ "秒后,重新获取";

		if (time >= 0) {

			countTimeTv.setText(Html.fromHtml(format));
			countTimeTv.setEnabled(false);
		} else {

			countTimeTv.setEnabled(true);
			countTimeTv.setText("重新获取验证码");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
