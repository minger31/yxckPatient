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
import android.widget.Button;
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

public class ActFindPassword extends BaseActivity {

		private static final String TAG = ActFindPassword.class.getName();

		private EditText findPhone = null;//填写的手机号
		private TextView tvGetCode = null;//点击获取验证码
		private EditText etFindCode = null;//填写验证码
		private EditText etFindPassword = null;//重设密码
		private Button BtnSubmitFind = null;//提交

		private String findPhoStr,etFindCodeStr,etFindPasswordStr;
		// 距离下次注册倒计时
		private TextView countTimeTv;
		private int MSG_WHAT_GETNUBE = 0;
		private long TWO_MINITES = 2 * 60;// 2分钟

		private String resultCode;//收到的验证码
		

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
			setContentView(R.layout.act_find_password);

			TitleBar title = getTitleBar();
			title.setBack("返回", null, -1);

			initUI();

		}

		private void initUI() {
			
			findPhone = (EditText) findViewById(R.id.et_find_phone);//填写的手机号
			countTimeTv = (TextView) findViewById(R.id.tv_get_code);//点击获取验证码
			etFindCode = (EditText) findViewById(R.id.et_find_code);//填写验证码
			etFindPassword = (EditText) findViewById(R.id.et_find_password);//重设密码
			BtnSubmitFind = (Button) findViewById(R.id.btn_submit_find);//提交

			countTimeTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 获取验证码
					getSmsVerfication();
				}
			});

			findViewById(R.id.btn_submit_find).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							// 提交进行找回密码
							submitRegister();
						}
					});
		}

		private void getSmsVerfication() {

			refreshCountTime(TWO_MINITES);
	
			if (myHandler != null) {
				Message msg = myHandler.obtainMessage();
				msg.what = MSG_WHAT_GETNUBE;
				msg.obj = TWO_MINITES - 1;
				myHandler.sendMessageDelayed(msg, 1000);
			}
			
			if (myHandler != null) {
				myHandler.removeMessages(MSG_WHAT_GETNUBE);
			}
			findPhoStr = findPhone.getText().toString();

			if (TextUtils.isEmpty(findPhoStr)) {
				CommonUtil.showToast("请输入手机号");
			} else if (!findPhoStr.matches("^[1][0-9]{10}$")) {
				CommonUtil.showToast("请输入合法的手机号");
			} else { 

				// 掉获取短信验证码接口获取短信验证码
				RequestParams params = new RequestParams();
				params.addQueryStringParameter("userLoginId", findPhoStr);
				params.addQueryStringParameter("type",CommonConstant.FORGET_PASSWORD);
				HttpUtils http = new HttpUtils();
				http.configTimeout(15 * 1000);
				http.send(HttpMethod.POST, UrlConstant.GET_VERIFICATION_CODE,
						params, new RequestCallBack<Object>() {

							@Override
							public void onStart() {

							}

							@Override
							public void onSuccess(Object result) {
								countTimeTv.setEnabled(false);

								if (result != null) {
									JSONObject json;
									try {
										json = new JSONObject(result.toString());
										int code = json.optInt("status");
										resultCode = json.optString("rows");
										if (code == 1) {
											CommonUtil.showToast("验证码已发送到您的手机，请注意查收");
											// 解析返回的数据类型
											refreshCountTime(TWO_MINITES);
											if (myHandler != null) {
												Message msg = myHandler
														.obtainMessage();
												msg.what = MSG_WHAT_GETNUBE;
												msg.obj = TWO_MINITES - 1;
												myHandler.sendMessageDelayed(msg,
														1000);
											}
										}else {
											CommonUtil.showToast(json.optString("msg"));
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

		}
		CommonWaitDialog wdg;
		private void submitRegister() {
			
			findPhoStr = findPhone.getText().toString();
			etFindCodeStr = etFindCode.getText().toString();
			etFindPasswordStr = etFindPassword.getText().toString();

			if (TextUtils.isEmpty(findPhoStr)) {
				CommonUtil.showToast("请输入手机号");
			} else if (!findPhoStr.matches("^[1][0-9]{10}$")) {
				CommonUtil.showToast("请输入合法的手机号");
			} else if (TextUtils.isEmpty(etFindCodeStr)) {
				CommonUtil.showToast("请输入短信验证码");
			} else if (!etFindCodeStr.matches("^[0-9a-zA-Z]{6}")) {
				CommonUtil.showToast("短信验证码长度为6位");
			} else if (TextUtils.isEmpty(etFindPasswordStr)) {
				CommonUtil.showToast("请输入新密码");
			} else{
				params = new RequestParams();
					// 调用忘记密码接口进行找回
					findPassword();
				}
			}

		RequestParams params;

		// TODO 调用忘记密码接口进行找回
		private void findPassword() {
//			wdg = new CommonWaitDialog(this, "提交中...");
//			
//			params.addQueryStringParameter("userLoginId", findPho);
//			params.addQueryStringParameter("newPassword", findCode);
//
//			HttpUtils http = new HttpUtils();
//			http.configTimeout(15 * 1000);
//			http.send(HttpMethod.POST, UrlConstant.GET_FIND_PASSWORD, params,
//					new RequestCallBack<Object>() {
//
//						@Override
//						public void onStart() {
//
//						}
//
//						@Override
//						public void onSuccess(Object result) {
//							wdg.clearAnimation();
//							if (result != null) {
//								JSONObject json;
//								try {
//									json = new JSONObject(result.toString());
//									if(!findCode.equals(resultCode)){
//										CommonUtil.showToast("验证码不正确");
//										return;
//									}
//									int code = json.optInt("status");
//									String msg = json.optString("msg");
//									if (code == 1) {
//										// 解析返回的数据类型
//										CommonUtil.showToast("修改成功");
//										finish();
//									}/*else if (code == 3) {
//										CommonUtil.showToast("该手机号已经被注册");
//									}*/else {
//										CommonUtil.showToast(msg);
//									}
//								} catch (JSONException e) {
//									wdg.clearAnimation();
//									e.printStackTrace();
//								}
//
//							}
//						}
//
//						@Override
//						public void onFailure(Throwable error, String msg) {
//							wdg.clearAnimation();
//							CommonUtil.showError(error, msg);
//						}
//					});
		}

		private void refreshCountTime(long time) {

			String format = "<font color = '#ff0000'>" + time + "</font>"
					+ "后,重新获取";

			if (time >= 0) {

				countTimeTv.setText(Html.fromHtml(format));
				countTimeTv.setEnabled(false);
			} else {

				countTimeTv.setEnabled(true);
				countTimeTv.setText("重新获取验证码");
			}
		}
}
