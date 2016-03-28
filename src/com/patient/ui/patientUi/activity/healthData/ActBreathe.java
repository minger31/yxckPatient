package com.patient.ui.patientUi.activity.healthData;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.common.LineView;
import com.patient.ui.patientUi.entity.baseTable.FitnessValueBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 健康数据  呼吸
 * @author dell
 *
 */
public class ActBreathe extends BaseActivity implements OnClickListener {
	
	private static final String TAG = ActBreathe.class.getName(); 
	private TextView day = null;//日
	private TextView week = null;//周
	private TextView month = null;//月
	private TextView year = null;//年
	private TextView tvDayAverage = null;//日平均值
	private int tag = R.id.tv_day;
	private List<FitnessValueBean> list;
	private LineView lineView;
	private ArrayList<String> strList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_weight);
		
		Log.d(TAG, "ActWeight===============onCreate");
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("呼吸速率", R.color.white);
		init();		
	}

	private void init() {
		lineView = (LineView) findViewById(R.id.line_view);
		strList = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			strList.add((i+1)+"日");
		}
		lineView.setBottomTextList(strList);
		
		day = (TextView)findViewById(R.id.tv_day);//日
		week = (TextView)findViewById(R.id.tv_week);//周
		month = (TextView)findViewById(R.id.tv_month);//月
		year = (TextView)findViewById(R.id.tv_year);//年
		
		tvDayAverage = (TextView)findViewById(R.id.tv_day_average);//日平均值
		findViewById(R.id.rl_add_date).setOnClickListener(this);//添加数据点
		findViewById(R.id.rl_share_date).setOnClickListener(this);//分享数据
		
		day.setOnClickListener(this);//日
		week.setOnClickListener(this);//周
		month.setOnClickListener(this);//月
		year.setOnClickListener(this);//年
		day.setSelected(true);
		
		/** 接口数据 */
		getFitnessValue();
	}
	public int getRandom(int min, int max) {
		return (int) Math.round(Math.random() * (max - min) + min);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_add_date://添加数据点
			Intent intent = new Intent(ActBreathe.this, ActAddDate.class);
			intent.putExtra(CommonConstant.KEY_RESLUT, CommonConstant.BREATHE); 
			startActivity(intent);
			break;
		case R.id.rl_share_date://分享数据
			CommonUtil.showToast("分享数据");
			break;
		case R.id.tv_day://日
			changeTag(R.id.tv_day);
			break;
		case R.id.tv_week://周
			changeTag(R.id.tv_week);
			break;
		case R.id.tv_month://月
			changeTag(R.id.tv_month);
			break;
		case R.id.tv_year://年
			changeTag(R.id.tv_year);
			break;
		default:
			break;
		}
	}
	
	private void changeTag(int id) {
		if (tag == id) {
			return;
		}
		day.setSelected(false);
		week.setSelected(false);
		month.setSelected(false);
		year.setSelected(false);
		tag = id;
		if (R.id.tv_day == id) {
			tvDayAverage.setText("日平均值：- -");
			day.setSelected(true);
		} else if (R.id.tv_week == id) {
			tvDayAverage.setText("周平均值：- -");
			week.setSelected(true);
		} else if (R.id.tv_month == id) {
			tvDayAverage.setText("月平均值：- -");
			month.setSelected(true);
		} else if (R.id.tv_year == id) {
			tvDayAverage.setText("年平均值：- -");
			year.setSelected(true);
		}
		//调接口
		getFitnessValue();
	}
	
	private void getFitnessValue() {
		final CommonWaitDialog wdg = new CommonWaitDialog(this, "加载数据中");

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId",LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("fitnessIndexId", "fi_10006");
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_FITNESS_VALUE, params, new RequestCallBack<Object>() {

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

								if (code == CommonConstant.SUCCESS) {
									list = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<FitnessValueBean>>(){});
									zouni(list);
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
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lineView.setBottomTextList(strList);
			lineView.setDataList(dataList);
		}
		
	};
	ArrayList<Integer> dataList;
	@SuppressLint("ResourceAsColor")
	public void zouni(List<FitnessValueBean> list) {
		dataList = new ArrayList<Integer>();
		strList.clear();
		for (int i = 0; i < list.size(); i++) {
			String a = list.get(i).createdStamp.substring(8, 11);
			strList.add(a);
			if (!TextUtils.isEmpty(list.get(i).targetValue)) {
				dataList.add((int) (Double.valueOf((list.get(i).targetValue)) + 0.5));
			}
		}
		handler.sendEmptyMessageDelayed(1, 1000);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CommonConstant.SUCCESS && resultCode == Activity.RESULT_OK && data != null) {
			if (!TextUtils.isEmpty(data.getExtras().getString(CommonConstant.KEY_RESLUT))) {
				getFitnessValue();
			}else {
				return;
			}
		}
	}
}
