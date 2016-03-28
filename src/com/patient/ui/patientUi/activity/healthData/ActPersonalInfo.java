package com.patient.ui.patientUi.activity.healthData;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.EnumConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.healthData.ShareTopicDialog.TopicListener;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/** 健康数据 - 个人信息 */
public class ActPersonalInfo extends BaseActivity implements OnClickListener {

	private TextView tvName = null;
	private TextView tvAge = null;
	private TextView tvSex = null;
	private TextView tvBloodType = null;

	private PartyBean bean = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_personal_infos);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("个人信息", R.color.white);

		init();
	}

	private void init() {
		bean = LoginPreference.getUserInfo();
		tvName = (TextView) findViewById(R.id.tv_name);
		tvAge = (TextView) findViewById(R.id.tv_age);
		tvSex = (TextView) findViewById(R.id.tv_sex);
		tvBloodType = (TextView) findViewById(R.id.tv_blood_type);

		findViewById(R.id.rl_name).setOnClickListener(null);// 姓名
		findViewById(R.id.rl_sex).setOnClickListener(null);// 性别
		findViewById(R.id.rl_age).setOnClickListener(null);// 年龄
		findViewById(R.id.rl_blood_type).setOnClickListener(this);// 血型
		findViewById(R.id.rl_stature).setOnClickListener(this);// 身高
		findViewById(R.id.rl_weight).setOnClickListener(this);// 体重
		findViewById(R.id.rl_body_mass_index).setOnClickListener(this);// 体重指数
	}

	@Override
	protected void onResume() {
		super.onResume();
		setValue();
	}

	private void setValue() {
		tvName.setText(bean.partyName);
		tvAge.setText(TextUtils.isEmpty(bean.age) ? "" : bean.age + "岁");
		tvSex.setText(EnumConstant.enumMap.get(bean.sexEnum));
		tvBloodType.setText(bean.bloodType);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_blood_type:// 血型
			ShareTopicDialog dg = new ShareTopicDialog(ActPersonalInfo.this,tvBloodType.getText().toString());
			dg.setPopDepartListener(new TopicListener() {

				@Override
				public void doRefresh(String value) {
					updateOutsideExperts(tvBloodType, value);
				}
			});
			dg.showDg();
			break;
		case R.id.rl_stature:// 身高
			startActivity(new Intent(ActPersonalInfo.this, ActStature.class));
			break;
		case R.id.rl_weight:// 体重
			startActivity(new Intent(ActPersonalInfo.this, ActWeight.class));
			break;
		case R.id.rl_body_mass_index:// 体重指数
			startActivity(new Intent(ActPersonalInfo.this, ActBodyMassIndex.class));
			break;
		default:
			break;
		}
	}

	/** 修改数据 */
	public void updateOutsideExperts(final TextView tv, final String value) {

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId",
				LoginPreference.getKeyValue(LoginType.PARTY_ID, "000000"));// 参数
		params.addQueryStringParameter("userLoginId",
				LoginPreference.getKeyValue(LoginType.PARTY_ID, "000000"));// 参数

		params.addQueryStringParameter("bloodType", value);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.UPDATE_OUTSIDE_EXPERTS, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {

						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");

								if (CommonConstant.STATUS_SUCCESS.equals(code)) {

									CommonUtil.showToast("修改成功");
									tv.setText(value);
									bean.bloodType = value;
								} else {
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}
}
