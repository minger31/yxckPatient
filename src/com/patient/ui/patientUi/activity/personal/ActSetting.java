package com.patient.ui.patientUi.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.patient.commonent.TitleBar;
import com.patient.ui.patientUi.activity.common.ActChangePassword;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.yxck.patient.R;

/**
 * 患者端设置界面
 * <dl>
 * <dt>ActSetting.java</dt>
 * <dd>Description:TODO</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 医学参考</dd>
 * <dd>CreateDate: 2014-11-18 上午9:36:05</dd>
 * </dl>
 * 
 * @author dell
 */
public class ActSetting extends BaseActivity implements OnClickListener{
	
	private LinearLayout llChangePassword = null;//修改密码
	private LinearLayout llOpinionsSuggestions = null;//意见建议
	private LinearLayout llProductPresentation = null;//产品介绍
	private LinearLayout llSoftwareUpdate = null;//软件更新

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_setting);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("设置", R.color.white);
		
		init();
	}

	private void init() {
		llChangePassword = (LinearLayout)findViewById(R.id.ll_change_password);//修改密码
		llOpinionsSuggestions =  (LinearLayout)findViewById(R.id.ll_opinions_suggestions);//意见建议
		llProductPresentation =  (LinearLayout)findViewById(R.id.ll_product_presentation);//产品介绍
		llSoftwareUpdate =  (LinearLayout)findViewById(R.id.ll_software_update);//软件更新
		llChangePassword.setOnClickListener(this);
		llOpinionsSuggestions.setOnClickListener(this);
		llProductPresentation.setOnClickListener(this);
		llSoftwareUpdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_change_password://修改密码
//			CommonUtil.showToast("修改密码");
			intent.setClass(ActSetting.this, ActChangePassword.class);
			startActivity(intent);
			break;
		case R.id.ll_opinions_suggestions://意见建议
//			CommonUtil.showToast("意见建议");
			intent.setClass(ActSetting.this, ActOpinionsSuggestions.class);
			startActivity(intent);
			break;
		case R.id.ll_product_presentation://产品介绍
//			CommonUtil.showToast("产品介绍");
			intent.setClass(ActSetting.this, ActProductPresentation.class);
			startActivity(intent);
			break;
		case R.id.ll_software_update://软件更新
			CommonUtil.showToast("软件更新");
			// 调用接口获取版本信息进行比较升级
//			if (ApkDownloadManager.isDownLoading) {
//				// 如果正在下载就提示正在下载中
//				Toast.makeText(ActSetting.this, "软件正在下载升级中...",
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
//			getVersion();
			break;
		}
	}
	
	
/**
 * 版本升级暂无接口	
 * @author: dell
 * @Title: getVersion 
 * @Description: TODO 
 * @date: 2014-11-18 下午1:44:33
 */
//	获取版本信息、软件升级
//	public void getVersion() {
//		RequestParams params = new RequestParams();
//		final CommonWaitDialog wdg = new CommonWaitDialog(ActSetting.this,
//				"请稍等");
//		HttpUtils http = new HttpUtils();
//		http.configTimeout(15 * 1000);
//		http.send(HttpMethod.POST, UrlConstant.GET_VERSION, params,
//				new RequestCallBack<Object>() {
//
//					@Override
//					public void onStart() {
//
//					}
//
//					@Override
//					public void onSuccess(Object result) {
//
//						wdg.clearAnimation();
//						// 根据接口返回的字段类型，判断当前是哪一个角色登陆
//						LogUtil.d2File(result.toString());
//						LogUtil.d(result.toString());
//
//						if (result != null) {
//							JSONObject json;
//							try {
//								json = new JSONObject(result.toString());
//								// 解析返回的数据类型
//								JSONArray array = json
//										.optJSONArray("versionRows");
//								if (array != null && array.length() > 0) {
//									json = array.getJSONObject(0);
//									apkUrl = json.optString("url");
//									serverVersion = json.optString("version");
//									String currentVersion = CommonUtil.getPackageInfo().versionName;
//									if (CommonUtil.isDownLoadPackage(
//											currentVersion, serverVersion)) {
//
//										CommonDialog dialog = new CommonDialog(
//												ActSetting.this);
//										dialog.setTitle("提示");
//										dialog.setMessage("有新版本,是否下载");
//										dialog.setCancleButton(null, "取消");
//										dialog.setPositiveButton(
//												new BtnClickedListener() {
//
//													@Override
//													public void onBtnClicked() {
//
//														NewVersionAlertDialog();
//
//													}
//												}, "确定");
//										dialog.showDialog();
//									} else {
//										CommonUtil.showToast("亲，您当前使用的是最新版本");
//									}
//								}
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable error, String msg) {
//
//						LogUtil.d2File(msg);
//						wdg.clearAnimation();
//						CommonUtil.showError(error, msg);
//					}
//				});
//	}
//	private String serverVersion;
//	private String apkUrl; 
//		
//	private void NewVersionAlertDialog() {
//
//		ApkDownloadManager apkmanager = new ApkDownloadManager(this);
//		apkmanager.setDownLoadUrl(apkUrl);
//		apkmanager.setFilename(serverVersion);
//		// 可以下载升级 false; true 表示正在升级
//		apkmanager.isInstallOrLoadApk(false);
//	}
}	
