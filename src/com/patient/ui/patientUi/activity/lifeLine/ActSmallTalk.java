package com.patient.ui.patientUi.activity.lifeLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.PullToRefreshListView;
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
import com.patient.ui.patientUi.activity.common.ActBuyFlowersBanner;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.common.DownImageUtil;
import com.patient.ui.patientUi.adapter.SmallTalkAdapter;
import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
import com.patient.ui.patientUi.entity.baseTable.InitiateFollowupBean;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.TinyFollowUpMessageBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

public class ActSmallTalk extends BaseActivity implements OnClickListener {
	private PullToRefreshListView pullToRefreshListView;
	private InitiateFollowupBean bean;
	private SmallTalkAdapter adapter = null;
	private List<TinyFollowUpMessageBean> list = null;
	private EditText etContext;
	private PartyBean partyBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_small_talk);
		bean = (InitiateFollowupBean) getIntent().getSerializableExtra(CommonConstant.KEY_RESLUT);

		String name = TextUtils.isEmpty(bean.doctorGv.partyName) ? (TextUtils.isEmpty(bean.doctorGv.nickName) ? bean.doctorGv.partyId : bean.doctorGv.nickName) : bean.doctorGv.partyName;
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle(name, R.color.white);
		mListPhotoPath = new ArrayList<String>();
		mInputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		init();
	}

	private void init() {
		partyBean = LoginPreference.getUserInfo();
		findViewById(R.id.iv_add).setOnClickListener(this);
		findViewById(R.id.ll_photo).setOnClickListener(this);
		findViewById(R.id.ll_imagelib).setOnClickListener(this);
		findViewById(R.id.ll_giveing).setOnClickListener(this);

		etContext = (EditText) findViewById(R.id.et_context);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		list = new ArrayList<TinyFollowUpMessageBean>();
		adapter = new SmallTalkAdapter(this);
		adapter.setBean(bean);
		pullToRefreshListView.setAdapter(adapter);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				if (!NetWorkUtil.isNetworkConnected(ActSmallTalk.this)) {
					CommonUtil.showToast("请检查网络连接");
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				getInitiateFollowup(false, false, true);// 聊天 倒过来
			}
		});
		// 不要上拉刷新

		findViewById(R.id.tv_send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(etContext.getText().toString())) {
					saveFollowupRecord();
				} else {
					CommonUtil.showToast("发送内容不能为空");
				}
			}
		});
		getInitiateFollowup(true, true, false);
	}

	private CommonWaitDialog wdg;
	private int currentDoctorPage = 1;
	private int totalPage = 1;

	/**
	 * 聊天列表 isTop 下拉刷新的时候不自动定位到底部
	 * */
	private void getInitiateFollowup(final boolean isWait, final boolean isRefresh, final boolean isTop) {
		if (isRefresh) {
			currentDoctorPage = 1;
			totalPage = 1;
		} else {
			currentDoctorPage++;
		}
		if (isWait) {
			wdg = new CommonWaitDialog(ActSmallTalk.this, "加载数据中");
		}
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("page",String.valueOf(currentDoctorPage));
		params.addQueryStringParameter("initFollowupId", bean.initFollowupId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_TINY_FOLLOW_UP_MESSAGE, params,
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
									List<TinyFollowUpMessageBean> temp = JSONConverter.convertToArray(json.optString("outputList"),new TypeToken<List<TinyFollowUpMessageBean>>() {});
									notifitionData(temp, isRefresh, isTop);
								}
							} catch (JSONException e) {
								if (!isRefresh) {
									currentDoctorPage--;
								}
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						if (!isRefresh) {
							currentDoctorPage--;
						}
						pullToRefreshListView.onRefreshComplete();
						if (isWait) {
							wdg.clearAnimation();
						}
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});

	}

	private void notifitionData(List<TinyFollowUpMessageBean> temp, boolean isRefresh, boolean isTop) {
		if (isTop) {// 下拉刷新的时候不自动定位到底部
			pullToRefreshListView.setStackFromBottom(false);
			pullToRefreshListView.setTranscriptMode(PullToRefreshListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}
		if (list == null) {
			list = new ArrayList<TinyFollowUpMessageBean>();
		}
		if (isRefresh) {
			list.clear();
		}
		if (temp != null) {
			list.addAll(temp);
		}
		adapter.setList(list);
	}

	/** 发送聊天 */
	private void saveFollowupRecord() {
		wdg = new CommonWaitDialog(ActSmallTalk.this, "加载数据中");
		RequestParams params = new RequestParams();

		params.addQueryStringParameter("treatmentId", bean.treatmentId);//就诊ID
		params.addQueryStringParameter("receiveId", bean.doctorGv.partyId);// 接收者ID
		params.addQueryStringParameter("launchId",LoginPreference.getUserInfo().partyId);//发送者id.
		if (!TextUtils.isEmpty(etContext.getText().toString())) {
			params.addQueryStringParameter("content", etContext.getText().toString());//消息内容
		}
		if (!TextUtils.isEmpty(urls)) {
			params.addQueryStringParameter("tinyMsgImage", urls);//图片返回的路劲
		}

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.ADD_TINY_FOLLOW_UP_MESSAGE, params,
				new RequestCallBack<Object>() {

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
									CommonUtil.showToast("发送成功");
									finish();
								} else {
									CommonUtil.showToast("发送失败，请稍后重试");
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

	private InputMethodManager mInputMethodManager;

	// 控制键盘显示 true为隐藏，false为显示
	private void hideInputMethod(boolean isHiden) {
		if (isHiden) {
			if (mInputMethodManager.isActive()) {
				final View v = getWindow().peekDecorView();
				if (v != null && v.getWindowToken() != null) {
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		} else {
			mInputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private boolean flag = true;// true为隐藏状态，false为显示状态

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add:
			if (flag) {// true为隐藏状态
				findViewById(R.id.ll_bottom1).setVisibility(View.VISIBLE);
				hideInputMethod(true);// 控制键盘显示 true为隐藏，false为显示
				flag = false;
			} else {// false为显示状态
				findViewById(R.id.ll_bottom1).setVisibility(View.GONE);
				flag = true;
				// hideInputMethod(!flag);//控制键盘显示 true为隐藏，false为显示
			}
			break;
		case R.id.ll_photo: // 拍照
			shareFromCamera();
			break;
		case R.id.ll_imagelib:// 从手机选图
			Bundle bundle = new Bundle();
			bundle.putString(MultiBucketChooserActivity.KEY_ACTIVITY_TYPE,MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT);
			bundle.putInt(KEY_SELECTED_CNT, mListPhotoPath.size());
			bundle.putInt(CommonConstant.KEY_RESLUT, 4);
			bundle.putBoolean(MultiBucketChooserActivity.KEY_IS_MULTI, true);
			Intent intent = new Intent(ActSmallTalk.this,MultiBucketChooserActivity.class);
			if (shareType == SHARE_TYPE_PIC) {
				intent.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE,MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
			} else {
				// TODO 分享其它的东西
			}
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_ADD);
			break;
		case R.id.ll_giveing:
			if ("0".equals(partyBean.bannerNum) || null == partyBean.bannerNum) {
				CommonDialog dialog = new CommonDialog(ActSmallTalk.this);
				dialog.setTitle("提示");
				dialog.setMessage("锦旗数不足，是否购买");
				dialog.setCancleButton(null, "取消");
				dialog.setPositiveButton(new BtnClickedListener() {

					@Override
					public void onBtnClicked() {
						Intent intent = new Intent(ActSmallTalk.this,ActBuyFlowersBanner.class);
						intent.putExtra(CommonConstant.KEY_RESLUT, false);
						startActivity(intent);
					}
				}, "确定");
				dialog.showDialog();
			} else {
				sendBanner();
			}
			break;
		}
	}

	// 赠送锦旗
	public void sendBanner() {
		final CommonWaitDialog wdg = new CommonWaitDialog(ActSmallTalk.this,"赠送中");
		RequestParams params = new RequestParams();

		params.addQueryStringParameter("sponsorPartyId", partyBean.partyId);// 赠送人
		params.addQueryStringParameter("goalPartyId", bean.doctorGv.partyId);// 送给谁
		params.addQueryStringParameter("count", "1");// 赠送数量
		params.addQueryStringParameter("donateTypeEnum", "donateTypeEnum_2");// 参数
		params.addQueryStringParameter("flag", "hjxh");// 参数
		params.addQueryStringParameter("patientEduId", "hjxh");// 参数

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.SEND_FLOWER_BANNER, params,
				new RequestCallBack<Object>() {
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
								String msg = json.optString("errorMessage");
								if (CommonConstant.STATUS_SUCCESS.equals(code)) {
									CommonUtil.showToast("赠送成功");
									partyBean.bannerNum = String.valueOf(Integer.valueOf(partyBean.bannerNum) - 1);
								} else {
									CommonUtil.showToast(msg);
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

	// 添加照片
	public static final int REQUEST_CODE_ADD = 112;
	public static final int SHARE_TYPE_PIC = 0;
	// 分享类型（图片或视频）,默认分享图片
	private int shareType = SHARE_TYPE_PIC;
	// 分享文件本地路径
	private ArrayList<String> mListPhotoPath = null;
	public static final String KEY_SELECTED_CNT = "key_selected_cnt";
	private static final int ACTION_SHARE_FROM_CAMERA = 1000;
	private String cameraFilePath = "";
	private File mCurPhotoFile = null;// File For Photo Which Camera Taked

	private void shareFromCamera() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhotoNative();
		} else {
			Toast.makeText(this, "SD卡未发现", Toast.LENGTH_SHORT).show();
		}
	}

	private String urls;// 图片上传后返回的路劲

	protected void doTakePhotoNative() {
		try {// Launch camera to take photo for selected contact

			mCurPhotoFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());// attach
			if (mCurPhotoFile != null) {

				final Intent intent = getTakePickIntent(mCurPhotoFile);
				cameraFilePath = mCurPhotoFile.getPath();
				startActivityForResult(intent, ACTION_SHARE_FROM_CAMERA);
			} else {
				Toast.makeText(this, "未发现SD卡", Toast.LENGTH_SHORT).show();
			}
		} catch (ActivityNotFoundException e) {
		}
	}

	public static Intent getTakePickIntent(File f) {
		// 部分三星手机，在启动照相机后，onActivityResult返回的intent为空，
		// 不能将照相后的图片传递到本页面,故此处用指定路径的形式做透传
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	private String getPhotoFileName() {
		String timeStr = "IMG_" + System.currentTimeMillis() + ".jpg";
		return timeStr;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CODE_ADD && data != null
					&& data.getExtras() != null) {
				// 增加照片
				ArrayList<String> addedPathList = data.getExtras().getStringArrayList(Intent.EXTRA_STREAM);
				if (addedPathList != null && addedPathList.size() > 0) {
					if (mListPhotoPath == null) {
						mListPhotoPath = new ArrayList<String>();
					}
					int repeatCnt = 0;
					for (int i = 0; i < addedPathList.size(); i++) {
						String addedPath = addedPathList.get(i);
						if (!mListPhotoPath.contains(addedPath)) {
							mListPhotoPath.add(addedPath);
						} else {
							repeatCnt++;
						}
						if (mListPhotoPath.size() >= MultiBucketChooserActivity.MAX_IMAGE_COUNT) {
							break;
						}
					}

					if (repeatCnt > 0) {
						CommonUtil.showToast("您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片");
					}
					wdgs = new CommonWaitDialog(ActSmallTalk.this, "正在发送中");
					asyTaskImage();
				}
			} else if (requestCode == ACTION_SHARE_FROM_CAMERA) {
				if (!TextUtils.isEmpty(cameraFilePath)) {
					CommonUtil.scanFileAsync(this, cameraFilePath);

					if (mListPhotoPath == null) {
						mListPhotoPath = new ArrayList<String>();
					}
					if (!mListPhotoPath.contains(cameraFilePath)) {
						mListPhotoPath.add(cameraFilePath);
					}
					wdgs = new CommonWaitDialog(ActSmallTalk.this, "正在发送中");
					asyTaskImage();
				} else {
					CommonUtil.showToast("解析数据出错", this);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void asyTaskImage() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mListPhotoPath = DownImageUtil.arraySave(mListPhotoPath);
				handler.sendEmptyMessage(1);
			}
		}).start();
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			importPicUrl();
		}
	};
	
	private CommonWaitDialog wdgs;
	// 上传图片
	private void importPicUrl() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("flag", "dtsc");
		for (int i = 0; i < mListPhotoPath.size(); i++) {
			params.addBodyParameter("image" + i,new File(mListPhotoPath.get(i)));
		}
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.UPLOAD_ICON, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {
						wdgs.clearAnimation();
						if (result != null) {

							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								if (CommonConstant.SUCCESS == code) {
									urls = json.optString("outputList");
									saveFollowupRecord();
								} else {
									CommonUtil.showToast("发送失败");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						wdgs.clearAnimation();
						LogUtil.d2File(msg);
						CommonUtil.showToast("发送失败");
						finish();
					}
				});
	}
}