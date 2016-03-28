package com.patient.ui.patientUi.activity.healthData;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActAddDate extends BaseActivity {
	
	private static final String TAG = ActAddDate.class.getName();
	
	private EditText edKilogram = null;//公斤
	private TextView tvDate = null;//日期
	private TextView tvTime = null;//时间
	private int type;
	private String fitnessIndexId;//type所对应的id，接口传值
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_date);
		
		type = getIntent().getIntExtra(CommonConstant.KEY_RESLUT,CommonConstant.STATURE);
		
		Log.d(TAG, "=========onCreate");
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("添加数据", R.color.white);
		bar.enableRightBtn("添加", -1, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(edKilogram.getText().toString())) {
					edKilogram.setError("数值不能为空");
				}else {
					createFitnessValue(fitnessIndexId);
				}
			}
		}); 
		init();		
	}

	private void init() {
		
		switch (type) {
		case CommonConstant.STATURE://身高
			((TextView)findViewById(R.id.tv_unit)).setText("厘米");
			fitnessIndexId = CommonConstant.TARGETID_STATURE;
			break;
		case CommonConstant.WEIGHT://体重
			((TextView)findViewById(R.id.tv_unit)).setText("公斤");
			fitnessIndexId = CommonConstant.TARGETID_WEIGHT;
			break;
		case CommonConstant.BODY_MASS_INDEX://体重指数
			((TextView)findViewById(R.id.tv_unit)).setText("BMI");
			fitnessIndexId = CommonConstant.TARGETID_BODY_MASS_INDEX;
			break;
		case CommonConstant.ANIMAL_HEAT://体温
			((TextView)findViewById(R.id.tv_unit)).setText("°C");
			fitnessIndexId = CommonConstant.TARGETID_ANIMAL_HEAT;
			break;
		case CommonConstant.BREATHE://呼吸
			((TextView)findViewById(R.id.tv_unit)).setText("次/分钟");
			fitnessIndexId = CommonConstant.TARGETID_BREATHE;
			break;
		case CommonConstant.HEART_RATE://心率
			((TextView)findViewById(R.id.tv_unit)).setText("次/分");
			fitnessIndexId = CommonConstant.TARGETID_HEART_RATE;
			break;
		case CommonConstant.OXYGEN_CONTENT://含氧量
			((TextView)findViewById(R.id.tv_unit)).setText("巴");
			fitnessIndexId = CommonConstant.TARGETID_OXYGEN_CONTENT;
			break;
		case CommonConstant.BLOOD_PRESSURE://血压
			((TextView)findViewById(R.id.tv_unit)).setText("千帕");
			fitnessIndexId = CommonConstant.TARGETID_BLOOD_PRESSURE;
			break;
		case CommonConstant.BLOOD_GLUCOSE://血糖
			((TextView)findViewById(R.id.tv_unit)).setText("mmol/L");
			fitnessIndexId = CommonConstant.TARGETID_BLOOD_GLUCOSE;
			break;
		default:
			((TextView)findViewById(R.id.tv_unit)).setText("厘米");
			fitnessIndexId = CommonConstant.TARGETID_STATURE;
			break;
		}
		
		edKilogram = (EditText)findViewById(R.id.ed_kilogram);
		tvDate = (TextView)findViewById(R.id.tv_date);
		tvTime = (TextView)findViewById(R.id.tv_time);
		
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
//		int second = c.get(Calendar.SECOND); 
		tvDate.setText(year + "年" + update(month) +"月" + update(date) + "日");
		tvTime.setText(update(hour) + ":" + update(minute));
	}
	
	/** 小于10的数前面加0 */
	private String update(int value){
		if (value < 10) {
			return "0" + value;
		}
		return "" + value;
	}
	
	/** 添加数据 */
	private void createFitnessValue(String fitnessIndex) {
		final CommonWaitDialog wdg = new CommonWaitDialog(this, "添加数据中");

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId", LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("targetValue", edKilogram.getText().toString());
		 params.addQueryStringParameter("fitnessIndexId", fitnessIndex);
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_FITNESS_VALUE, params, new RequestCallBack<Object>() {

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
								String code = json.optString("responseMessage");

								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
									CommonUtil.showToast("添加成功");
									Intent intent = getIntent();
									intent.putExtra(CommonConstant.KEY_RESLUT, "asd");
									setResult(Activity.RESULT_OK, intent);
									finish();
								}
							} catch (JSONException e) {
								e.printStackTrace();
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
