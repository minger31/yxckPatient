package com.patient.ui.patientUi.activity.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.activity.PatientMainFragment;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActLogin extends BaseActivity implements OnClickListener{
	
		private static final String TAG = ActLogin.class.getName();

		private EditText loginPhone = null;
		private EditText loginPassword = null;
		Spinner spinner;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Log.d(TAG, "onCreate");
			setContentView(R.layout.act_login);

			initUI();
		}

		private void initUI() {
			spinner = (Spinner)findViewById(R.id.spinner1);
			final  String[] mItems = {"182.92.185.73","192.168.1.153","192.168.1.154","123.57.134.108"};
			ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,R.layout.aaa, mItems);
			spinner.setAdapter(_Adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			        String str= mItems[position];
			        UrlConstant.COMMON_URL = "http://" + str + ":80/sysCommon/control/restful/";
			        UrlConstant.asd();
			    }
			    @Override
			    public void onNothingSelected(AdapterView<?> parent) {
			        // TODO Auto-generated method stub
			    }
			});
			
			findViewById(R.id.btn_login).setOnClickListener(this);
			findViewById(R.id.btn_new_user).setOnClickListener(this);
			findViewById(R.id.btn_find_password).setOnClickListener(this);

			loginPhone = (EditText) findViewById(R.id.et_login_phone);
			loginPassword = (EditText) findViewById(R.id.et_login_password);

		}

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.btn_login:
				login();
				break;
			case R.id.btn_new_user:
				Intent intent = new Intent();
				intent.setClass(this, ActRegister.class);
				startActivity(intent);
				break;
			case R.id.btn_find_password:
				Intent intent2 = new Intent();
				intent2.setClass(this, ActFindPassword.class);
				startActivity(intent2);
				CommonUtil.showToast("找回密码", this);
				break;
			}
		}

	 
		private void login() {

			String loginPho = loginPhone.getText().toString();
			String password = loginPassword.getText().toString();

			final CommonWaitDialog wdg = new CommonWaitDialog(this, "正在登陆中");
			// 调登陆接口进行登录
			RequestParams params = new RequestParams();

			params.addHeader("USERNAME", loginPho);
			params.addHeader("PASSWORD", password);
			params.addQueryStringParameter("partyFlag", "N");
			 
			HttpUtils http = new HttpUtils();
			http.configTimeout(15 * 1000);
			http.send(HttpMethod.POST, UrlConstant.LOGIN_SERVICE, params,
					new RequestCallBack<Object>() {

						@Override
						public void onStart() {
						}

						@Override
						public void onSuccess(Object result) {

							wdg.clearAnimation();

							// 根据接口返回的字段类型，判断当前是哪一个角色登陆
//							CommonUtil.showToast(result.toString(), ActLogin.this);
							LogUtil.d2File(result.toString());

							if (result != null) {
								JSONObject json;
								try {
									json = new JSONObject(result.toString());
									int code = json.optInt("status");
									if (code == CommonConstant.SUCCESS) {
										
										LogUtil.d(TAG, "登陆成功   "+"  jessionid="+json.optString("jsessionid"));
										
										// 解析返回的数据类型
//										JSONArray array = json.optJSONArray("outputList");
										List<PartyBean> list = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<PartyBean>>(){});
										if (list != null && list.size() > 0) {

//											json = array.getJSONObject(0);
											LogUtil.d2File("partyId="+ json.optString("partyId"));
											LoginPreference.setUserInfo(list.get(0));
											LoginPreference.getUserInfo().password = loginPassword.getText().toString();
											LoginPreference.getInstance().setString(LoginType.PARTY_ID,LoginPreference.getUserInfo().partyId);
											LoginPreference.getInstance().setString(LoginType.LEVEL,LoginPreference.getUserInfo().levelEnum);
											LoginPreference.getInstance().setString(LoginType.HEAD_URL,LoginPreference.getUserInfo().partyheadUrl);
											
											LogUtil.d(LoginPreference.getKeyValue( LoginType.PARTY_ID, "000000"));

											loginPhone.setText("");
											loginPassword.setText("");
										 
											LoginPreference.getInstance().setString(LoginType.PARENT_PARTY_ID,json.optString("parentPartyId"));
											ActLogin.this.startActivity(new Intent(ActLogin.this,PatientMainFragment.class));
											
//											ActLogin.this.startActivity(new Intent(ActLogin.this,TestPush.class));
										}
									} else if (code == 2) {
										// 密码错误
										loginPassword.setText("");
										CommonUtil.showToast(json.optString("msg"),ActLogin.this);
									} else {
										LogUtil.d(TAG, "登陆失败");
										loginPassword.setText("");
									}
								} catch (JSONException e) {
									e.printStackTrace();
									wdg.clearAnimation();
									CommonUtil.showToast("网络异常，请稍后", ActLogin.this);
								}
							}
						}

						@Override
						public void onFailure(Throwable error, String msg) {
							LogUtil.d2File(msg);
							wdg.clearAnimation();
							CommonUtil.showToast("网络异常，请稍后", ActLogin.this);
						}
					});
		}
		 
}
