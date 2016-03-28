package com.patient.ui.patientUi.activity.lifeLine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.ui.patientUi.activity.common.ActStore;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActFillFollowup extends BaseActivity {
	private Handler mHandler = new Handler();
	private WebView webView;
	private String initFollowupId;
	private CommonWaitDialog dg = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_fill_followup);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("填写回访",R.color.white);
		bar.enableRightBtn("商城", -1, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActFillFollowup.this, ActStore.class));
			}
		});
		
		webView = (WebView) findViewById(R.id.webview);
		  
		init();
		
	}
	@SuppressLint({ "JavascriptInterface", "NewApi" })
	private void init() {
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
 
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			initFollowupId = bundle.getString(CommonConstant.KEY_RESLUT);

			webView.setWebChromeClient(new MyWebChromeClient());

			webView.addJavascriptInterface(new DemoJavaScriptInterface(),"demo");

			String webUrl = UrlConstant.FILL_IN_RETURN_VISIT +"?initFollowupId=" + initFollowupId;
//			String webUrl ="http://192.168.1.154:8088/yxck/control/FillInReturnVisit?initFollowupId=" + initFollowupId;
			webView.loadUrl(webUrl);
			LogUtil.d("webUrl = "+webUrl);

			webView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					
					super.onPageStarted(view, url, favicon);
					dg = new CommonWaitDialog(ActFillFollowup.this, "加载中");

				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					
					dg.clearAnimation();
					// String doctorId =
					// LoginPreference.getKeyValue(LoginType.PARTY_ID,
					// "0000000");
					// String testPaper = course.getTestPaperId();
					//
//					webView.loadUrl("javascript:getDocAndTestPaper("+ doctorId + "," + testPaper + ")");
					// webView.loadUrl("javascript:getDocAndTestPaper('10000','yiyuzheng')");
				}
			});
		}
	}
 
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (webView.canGoBack()) {
				webView.goBack(); // goBack()表示返回WebView的上一页面
			} else {
				finish();
			}
			return true;
		}
		return false;
	}
		
	// js 调 java 方法去执行相应
	@SuppressWarnings("unused")
	public void skipApplyScores(int scores) {

//		Intent i = new Intent(this, ActFillFollowup.class);
//		startActivity(i);
//		finish();
	}

	/**
	 * This is not called on the UI thread. Post a runnable to invoke loadUrl on
	 * the UI thread.
	 */
	@JavascriptInterface
	private void returnMonth(final String doctorId, final String paperId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				CommonUtil.showToast("jstojava=" + doctorId + paperId);
			}
		});
	}

	private final class DemoJavaScriptInterface {

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */
		@JavascriptInterface
		public void clickOnAndroid() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {//返回时执行
					CommonUtil.showToast("保存成功");
					finish();
				}
			});
		}
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			result.confirm();
			return true;
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("is_load", true);
	}


}
