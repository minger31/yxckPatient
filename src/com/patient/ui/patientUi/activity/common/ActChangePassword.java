package com.patient.ui.patientUi.activity.common;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.entity.baseTable.InitiateFollowupBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 患者端修改密码
 * <dl>
 * <dt>ActChangePassword.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-11-18 下午1:47:00</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActChangePassword extends BaseActivity {

	private EditText phone = null;//手机号
	private EditText etFindCode;
	private EditText etFindPassword;//重复密码
	//登录者的手机号
	private String party_id = LoginPreference.getKeyValue( LoginType.PARTY_ID, "000000");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_change_password);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("修改密码", 1);
		bar.enableRightBtn("保存", R.color.white, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (etFindCode.getText().toString().equals(etFindPassword.getText().toString())) {
					changePassword();
				}else {
					CommonUtil.showToast("两次输入的密码不一致");
				}
			}

		});
		init();
	}

	private void init() {
		phone = (EditText)findViewById(R.id.phone);
		phone.setText(party_id);//手机号赋值
		etFindCode =(EditText)findViewById(R.id.et_find_code);
		etFindPassword = (EditText)findViewById(R.id.et_find_password);
	}
	
	private void changePassword() {
		final CommonWaitDialog wdg = new CommonWaitDialog(this, "修改中");

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userLoginId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("oldPassword", LoginPreference.getUserInfo().password);
		params.addQueryStringParameter("newPassword", etFindPassword.getText().toString());
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.AJAX_UPDATE_PASSWORD,
				params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {

							wdg.clearAnimation();

						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								String a = json.optString("responseMessage");
								if (code == CommonConstant.SUCCESS) {
									if ("success".equals(a)) {
										CommonUtil.showToast("修改成功");
										finish();
									}
								}
							} catch (JSONException e) {
								wdg.clearAnimation();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						wdg.clearAnimation();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}
}
 