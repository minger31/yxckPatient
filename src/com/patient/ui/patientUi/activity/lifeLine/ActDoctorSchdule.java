package com.patient.ui.patientUi.activity.lifeLine;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.entity.extendsTable.DoctorSchduleBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ActDoctorSchdule.java</dt>
 * <dd>Description:医生的日程设置</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-12-12 下午3:52:41</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ActDoctorSchdule extends BaseActivity {

	private static final String TAG = ActDoctorSchdule.class.getName();

	private ListView schdule = null;
	private List<DoctorSchduleBean> schdData;
	private SchduleAdapter adapter;
	
	private  String lookDays = "";
	private String doctorId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_doctor_schdule);

		LogUtil.d(TAG, "onCreate");

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle(getResources().getString(R.string.schdule_setting), R.color.white);
		
		lookDays = getIntent().getStringExtra(CommonConstant.KEY_RESLUT);
		doctorId = getIntent().getStringExtra(CommonConstant.KEY_ID);
		
		schdule = (ListView) findViewById(R.id.schdule_list);

		adapter = new SchduleAdapter();
		schdule.setAdapter(adapter);

		getSchduleData();
	}

	private class SchduleAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return schdData == null ? 0 : schdData.size();
		}

		@Override
		public Object getItem(int position) {
			return schdData == null ? 0 : schdData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			ViewHolder holder;
			if (view == null) {
				
				view = LayoutInflater.from(ActDoctorSchdule.this).inflate(R.layout.row_schdule, null);
				holder = new ViewHolder();
				view.setTag(holder);
				holder.date = (TextView) view.findViewById(R.id.date);
				holder.enName = (TextView) view.findViewById(R.id.date_en_name);
				holder.week = (TextView) view.findViewById(R.id.week);
				holder.day = (TextView) view.findViewById(R.id.day);
				holder.morning = (TextView) view.findViewById(R.id.morning);
				holder.afternoon = (TextView) view.findViewById(R.id.afternoon);
				holder.allDay = (TextView) view.findViewById(R.id.allday);
				holder.rg = (LinearLayout) view.findViewById(R.id.rg);
				holder.lt = (LinearLayout) view.findViewById(R.id.lt);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final DoctorSchduleBean bean = schdData.get(position);
			holder.lt.setVisibility(View.GONE);
			if (isShowMonth(position)) {
				holder.lt.setVisibility(View.VISIBLE);
				holder.date.setText(DateUtil.getNewFormatDateString(
						bean.dateTime, DateUtil.FORMAT_YYYY_MM_DD,DateUtil.FORMAT_YYYY_MM));
				holder.enName.setText("In " + bean.dateTime.substring(0, 4)
						+ " " + getMonth(bean.dateTime));
			}

			// 修改接口
			if (!TextUtils.isEmpty(bean.selected)) {
				if ("dayType_1".equals(bean.selected)) {
					holder.morning.setSelected(true);
					holder.afternoon.setSelected(false);
					holder.allDay.setSelected(false);
					
				} else if ("dayType_2".equals(bean.selected)) {
					holder.morning.setSelected(false);
					holder.afternoon.setSelected(true);
					holder.allDay.setSelected(false);
					 
				} else if ("dayType_3".equals(bean.selected)) {
					holder.morning.setSelected(false);
					holder.afternoon.setSelected(false);
					holder.allDay.setSelected(true);
				}
			} else {
				holder.morning.setSelected(false);
				holder.afternoon.setSelected(false);
				holder.allDay.setSelected(false);
			}
			holder.week.setText(bean.week);
			holder.day.setText(bean.dateTime.substring(8, 10));
			return view;
		}

		private boolean isShowMonth(int position) {

			if (position == 0) {
				return true;
			} else if (position > 0 && position < getCount()) {
				if (!schdData.get(position).dateTime.substring(0, 7).equals(
						schdData.get(position - 1).dateTime.substring(0, 7))) {
					return true;
				}
			}
			return false;
		}

		private String getMonth(String time) {
			int month = Integer.parseInt(time.substring(5, 7));
			String value = null;
			switch (month) {
			case 12:
				value = "December";
				break;
			case 1:
				value = "January";
				break;
			case 2:
				value = "February";
				break;
			case 3:
				value = "March";
				break;
			case 4:
				value = "Apri";
				break;
			case 5:
				value = "May";
				break;
			case 6:
				value = "June";
				break;
			case 7:
				value = "July";
				break;
			case 8:
				value = "August";
				break;
			case 9:
				value = "September";
				break;
			case 10:
				value = "October";
				break;
			case 11:
				value = "November ";
				break;
			default:
				break;
			}
			return value;
		}

		private class ViewHolder {

			LinearLayout lt;
			TextView date;
			TextView enName;
			TextView week;
			TextView day;
			TextView morning;
			TextView afternoon;
			TextView allDay;
			LinearLayout rg;
		}
	}

	private void getSchduleData() {

		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		params.addQueryStringParameter("index", lookDays);
		params.addQueryStringParameter("doctorId", doctorId);
		http.send(HttpMethod.POST, UrlConstant.getOpenScheduleData, params,
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
								int code = json.optInt("status");
								if (code == CommonConstant.SUCCESS) {
									schdData = JSONConverter.convertToArray(
											json.optString("outputList"),
											new TypeToken<List<DoctorSchduleBean>>() {
											});
									adapter.notifyDataSetChanged();
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
