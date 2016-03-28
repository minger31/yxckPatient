package com.patient.ui.patientUi.activity.common;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.patient.commonent.TitleBar;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
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
public class ActStore extends BaseActivity {

	private WebView webView = null;
	private String url = "http://192.168.1.163/mobileStore/control/main";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_store);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("商城", 1);

		init();
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void init() {
		webView = (WebView) findViewById(R.id.webView1);
		webView.setFitsSystemWindows(true);
		WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webView.setWebViewClient(new WebViewClient(){
	         @Override
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	 
	          view.loadUrl(url);   //在当前的webview中跳转到新的url
	 
	          return true;
	         }
	        });
		webView.setWebChromeClient(new MyWebChromeClient());

		webView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
		webView.loadUrl(url);
	}

	// js 调 java 方法去执行相应
	@SuppressWarnings("unused")
	public void skipApplyScores(int scores) {
		// Intent i = new Intent(this, ActFillInfo.class);
		// startActivity(i);
		// finish();
	}

	private Handler mHandler = new Handler();

	/**
	 * This is not called on the UI thread. Post a runnable to invoke loadUrl on
	 * the UI thread.
	 */
	@JavascriptInterface
	private void returnMonth(final String doctorId, final String paperId) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {

			}
		});
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

	private final class DemoJavaScriptInterface {

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */
		@JavascriptInterface
		public void clickOnAndroid(final String currentPosition,
				final String totalPicUrl) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					int position = 0;
					ArrayList<String> reslut = new ArrayList<String>();
					if (totalPicUrl != null) {
						String[] pics = totalPicUrl.split(",");
						for (int i = 0; i < pics.length; i++) {
							String string = pics[i];
							if (string.equals(currentPosition)) {
								position = i;
							}
							reslut.add(string);
						}
					} else {
						return;
					}
					Bundle bundle = new Bundle();
					bundle.putStringArrayList(ActPreViewIcon.KEY_ALL_ICON,
							reslut);
					bundle.putInt(ActPreViewIcon.KEY_CURRENT_ICON, position);
					Intent intent = new Intent(ActStore.this,
							ActPreViewIcon.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
					// overridePendingTransition(android.R.anim.fade_in,
					// android.R.anim.fade_out);
					// Bundle bundle = new Bundle();
					// bundle.putStringArrayList(Intent.EXTRA_STREAM, reslut);
					// bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM,
					// true);
					// bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE,ActSendComment.SHARE_TYPE_PIC);
					// bundle.putString(PreviewActivity.ACTIVITY_FLAG,PreviewActivity.AVITVITY_START_FOR_RESULT);
					// bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX,
					// position);
					// Intent intent = new Intent(ActArticalDetail.this,
					// PreviewActivity.class);
					// intent.putExtras(bundle);
					// startActivity(intent);
				}
			});
		}
	}

}
