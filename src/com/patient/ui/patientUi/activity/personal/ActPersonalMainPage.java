package com.patient.ui.patientUi.activity.personal;

 
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.EnumConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.adapter.DiseaseNumAdapter;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 患者端的医生个人主页
 * <dl>
 * <dt>ActPersonalMainPage.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考报</dd>
 * <dd>CreateDate: 2014-12-30 下午3:58:15</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActPersonalMainPage extends BaseActivity {
	
	private static final String TAG = ActPersonalMainPage.class.getName();
	
	private ImageView editorHead;
	private TextView editorName;
	private TextView editorDuty;// 职称 主治医生
	private TextView editorUnit;// 单位
	private TextView editorPosition;// 行政职务
	private TextView editorDepart;// 科室
	private TextView editorDepartPosition; // 科室类别
	
	private TextView patientsCount;
	private TextView visitorCount;
	private TextView patientsEducCount;
	private TextView tvFlower;
	private TextView tvBanner;
	
    private TitleBar bar;
	private TreatmentBean treatmentBean;//传过来的医生bean
	private DiseaseNumAdapter adapter;
	
	private ListView listview;
	private PartyBean partyBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_personal_main_page);
		partyBean = LoginPreference.getUserInfo();
		bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("个人主页", R.color.white);
		 
		treatmentBean = (TreatmentBean) getIntent().getSerializableExtra(CommonConstant.KEY_RESLUT);
		
		initUI();
	}	
	
	private void initUI() {
		tvFlower = (TextView) findViewById(R.id.tv_flower);// 鲜花
		tvBanner = (TextView) findViewById(R.id.tv_banner);// 锦旗
		editorHead = (ImageView) findViewById(R.id.partyheadUrl);
		editorName = (TextView) findViewById(R.id.tv_party_name);
		editorDuty = (TextView) findViewById(R.id.tv_duty);// 职称 主治医生
		editorUnit = (TextView) findViewById(R.id.tv_worker_unit);// 单位
		editorPosition = (TextView) findViewById(R.id.tv_hospital_title);// 行政职务
		editorDepart = (TextView) findViewById(R.id.tv_department);// 科室
		editorDepartPosition = (TextView) findViewById(R.id.tv_depart_position); // 科室类别

		patientsCount = (TextView) findViewById(R.id.patientCount);// 患者
		visitorCount = (TextView) findViewById(R.id.visitorCount);// 随访
		patientsEducCount = (TextView) findViewById(R.id.patientEducation);// 患教

		listview = (ListView) findViewById(R.id.listview);
		adapter = new DiseaseNumAdapter(ActPersonalMainPage.this);
		listview.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setDoctorInfo();
		getDoctorHome();
	}
	
	private void setDoctorInfo() {
		if (null == treatmentBean) {
			return;
		}

		LoaderImage.getInstance(R.drawable.ic_heads_doc).ImageLoaders(treatmentBean.doctorGv.partyheadUrl, editorHead);
		
		editorName.setText(treatmentBean.doctorGv.partyName);
		editorDuty.setText(treatmentBean.doctorGv.hospitalTitle);// 职称 主治医生
		editorUnit.setText(treatmentBean.doctorGv.hospitalName);// 单位 医院
		editorPosition.setText(EnumConstant.enumMap.get(treatmentBean.doctorGv.administrationTitle));// 行政职务
		editorDepart.setText(treatmentBean.doctorGv.departmentName);// 科室
		editorDepartPosition.setText(treatmentBean.doctorGv.departTitle);// 科室职务

	}

	/** 获取医生下各病人数  */
	private void getDiseaseNum() {

		final CommonWaitDialog wdg = new CommonWaitDialog(ActPersonalMainPage.this, "加载数据中");

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("doctorId",treatmentBean.doctorGv.partyId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_DISEASE_NUM, params,new RequestCallBack<Object>() {

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
									// 解析返回的数据类型
									List<TreatmentBean> list = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<TreatmentBean>>() {});
									adapter.setList(list);
								} else {
									adapter.setList(null);
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
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});

	}
	
	/** 获取医生个人主页的患者数随访和患教数 */
	private void getDoctorHome() {
//		final CommonWaitDialog wdg = new CommonWaitDialog(this, "加载中");
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId", treatmentBean.doctorGv.partyId);//医生id
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_DOCTORHOME, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {
//						wdg.clearAnimation();
						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								PartyBean bean = null;
								if (code == CommonConstant.SUCCESS) {
									bean = new PartyBean(); 
									JSONObject object = json.optJSONObject("outputList");
//									JSONObject asd =object.getJSONObject("partyGv");
									bean.patientNum = object.getString("patientCount");
									bean.followCount = object.getString("initiateFollowupCount");
									bean.patientEduCount = object.getString("PatientEduCount");
									flowersNum = object.getString("flowersCount");
									bannerNum = object.getString("bannerCount");
									notifyDataChanged(bean);
								}
							} catch (JSONException e) {
//								wdg.clearAnimation();
								e.printStackTrace();
							}
						}
					}
				
					@Override
					public void onFailure(Throwable error, String msg) {
						CommonUtil.showError(error, msg);
//						wdg.clearAnimation();
						LogUtil.d2File(msg);
					}
				});
	}

	private String flowersNum = "0";//医生的鲜花数
	private String bannerNum = "0";//医生的锦旗数
	private void notifyDataChanged(PartyBean bean) {
		if (TextUtils.isEmpty(flowersNum)) {
			flowersNum = "0";
		}
		if (TextUtils.isEmpty(bannerNum)) {
			bannerNum = "0";
		}
		tvFlower.setText("(" + flowersNum + ")束");
		tvBanner.setText("(" + bannerNum + ")面");
		patientsCount.setText("患者(" + bean.patientNum + ")");//患者数
		visitorCount.setText("随访(" + bean.followCount + ")");//随访
		patientsEducCount.setText("患教(" + bean.patientEduCount + ")");//患教数
		getDiseaseNum();
	}
}