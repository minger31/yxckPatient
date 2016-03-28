package com.patient.ui.patientUi.activity.personal;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.PullToRefreshListView;
import com.patient.commonent.PullToRefreshListView.OnLoadListener;
import com.patient.commonent.PullToRefreshListView.OnRefreshListener;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActAlipay;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.adapter.UserSalesAdapter;
import com.patient.ui.patientUi.entity.baseTable.AcademicCoinHistoryBean;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ActAcademicCurrency.java</dt>
 * <dd>Description:学术币</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-12-7 下午4:18:13</dd>
 * </dl>
 * 
 * @author lihs
 */

public class ActAcademicCurrency extends BaseActivity {

	// 学术币 和 学术宝
	private PullToRefreshListView salesRecord;

	private UserSalesAdapter userAdapter;
	private TextView coins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_academic_currency);
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("钱包",R.color.white);
		bar.enableRightBtn(getResources().getString(R.string.recharge), -1,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ActAcademicCurrency.this, ActAlipay.class);
						intent.putExtra(CommonConstant.JSON, "asdasd");
						startActivity(intent);
					}
				});

		init();

		getsalesRecord(true, true);
	}

	private void init() {

		userAdapter = new UserSalesAdapter(this);
		salesRecord = (PullToRefreshListView) findViewById(R.id.salesList);
		salesRecord.setAdapter(userAdapter);
		salesRecord.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActAcademicCurrency.this)) {
					CommonUtil.showToast("请检查网络连接");
					salesRecord.onRefreshComplete();
					return;
				}
				getsalesRecord(false, true);
			}
		});

		// 下拉加载更多数据
		salesRecord.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(ActAcademicCurrency.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					salesRecord.onLoadComplete();
					return;
				}
				getsalesRecord(false, false);
			}
		});

		coins = (TextView) findViewById(R.id.coins);
	}

	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	// 获取项目的评审
	private void getsalesRecord(final boolean isWait, final boolean isRefresh) {

		if (isWait) {
			wdg = new CommonWaitDialog(this, "加载数据中");
		}

		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId",LoginPreference.getUserInfo().partyId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GETACADEMICCOINHISTORY, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {

						if (isWait) {
							wdg.clearAnimation();
						}
						salesRecord.onRefreshComplete();
						salesRecord.onLoadComplete();

						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								currentDoctorPage = json.optInt("page");
								totalPage = json.optInt("total");
								if (currentDoctorPage >= totalPage) {
									salesRecord.setLoadFull(true);
								} else {
									salesRecord.setLoadFull(false);
								}
								if (code == CommonConstant.SUCCESS) {
									List<AcademicCoinHistoryBean> saleRecords = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<AcademicCoinHistoryBean>>() { });
									userAdapter.setData(saleRecords);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								if (isWait) {
									wdg.clearAnimation();
								}
								if (!isRefresh) {
									currentDoctorPage--;
								}
								salesRecord.onRefreshComplete();
								salesRecord.onLoadComplete();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						if (isWait) {
							wdg.clearAnimation();
						}
						if (!isRefresh) {
							currentDoctorPage--;
						}
						salesRecord.onRefreshComplete();
						salesRecord.onLoadComplete();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		PartyBean user = LoginPreference.getUserInfo();
		int myScores = TextUtils.isEmpty(user.academicCoin) ? 0 : Integer
				.parseInt(user.academicCoin);
		coins.setText(myScores + "");
	}
}
