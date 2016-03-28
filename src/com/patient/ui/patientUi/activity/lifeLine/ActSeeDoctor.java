package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.adapter.VisitorDoctorAdapter;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 患者端 就诊
 * <dl>
 * <dt>ActFollowUp.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报社</dd>
 * <dd>CreateDate: 2014-12-24 下午5:42:37</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActSeeDoctor extends BaseActivity {

	private PullToRefreshListView pullToRefreshListView;
	private VisitorDoctorAdapter adapter;
	private List<TreatmentBean> list;
	private String patientId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_see_doctor);

		patientId = getIntent().getStringExtra(CommonConstant.KEY_RESLUT);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("就诊", R.color.white);
		bar.enableRightBtn("添加就诊", 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ActSeeDoctor.this,ActAddVisit.class);
				startActivityForResult(i, 1);
			}
		});

		findViewById(R.id.ll_search).setVisibility(View.GONE);
		init();
		getTreatment(true, true);

	}
	

	private void init() {
		list = new ArrayList<TreatmentBean>();

		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		// 显示历史详情
		adapter = new VisitorDoctorAdapter(ActSeeDoctor.this);
		pullToRefreshListView.setAdapter(adapter);
		// 下拉加载更多数据
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActSeeDoctor.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getTreatment(false, true);
			}
		});
		// 上拉加载更多数据
		pullToRefreshListView.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {

				if (!NetWorkUtil.isNetworkConnected(ActSeeDoctor.this)) {
					CommonUtil.showToast("请检查网络连接");
					// 显示footer
					pullToRefreshListView.onLoadComplete();
					return;
				}
				getTreatment(false, false);
			}
		});

	}

	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	/** 就诊列表 */
	private void getTreatment(final boolean isWait, final boolean isRefresh) {
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		if (isWait) {
			wdg = new CommonWaitDialog(ActSeeDoctor.this, "加载数据中");
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("patientId", patientId);
		params.addQueryStringParameter("page",
				String.valueOf(currentDoctorPage));

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_TREATMENT, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(Object result) {

						if (isWait) {
							wdg.clearAnimation();
						}
						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						if (result != null) {

							LogUtil.d2File(result.toString());

							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");

								currentDoctorPage = json.optInt("page");
								totalPage = json.optInt("total");
								if (currentDoctorPage >= totalPage) {
									pullToRefreshListView.setLoadFull(true);
								} else {
									pullToRefreshListView.setLoadFull(false);
								}
								if (code == CommonConstant.SUCCESS) {
									// 解析返回的数据类型
									List<TreatmentBean> temp = JSONConverter.convertToArray(
											json.optString("outputList"),
											new TypeToken<List<TreatmentBean>>() {
											});
									notifitionData(temp, isRefresh);
								} else {
									adapter.setList(null);
								}

							} catch (JSONException e) {

								pullToRefreshListView.onRefreshComplete();
								if (!isRefresh) {
									currentDoctorPage--;
								}
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {

						pullToRefreshListView.onRefreshComplete();
						pullToRefreshListView.onLoadComplete();
						if (isWait) {
							wdg.clearAnimation();
						}
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});

	}

	private void notifitionData(List<TreatmentBean> temp, boolean isRefresh) {

		if (isRefresh) {
			list.clear();
		}
		list.addAll(temp);
		adapter.setList(list);
	}

}
