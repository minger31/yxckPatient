package com.patient.ui.patientUi.activity.lifeLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.MyGridView;
import com.patient.commonent.TitleBar;
import com.patient.commonent.UpdateImageMenu;
import com.patient.commonent.UpdateImageMenu.DoPhtot;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.common.DownImageUtil;
import com.patient.ui.patientUi.activity.common.PreviewActivity;
import com.patient.ui.patientUi.defineView.PopDoctorArea;
import com.patient.ui.patientUi.defineView.PopDoctorArea.PopDepartListeners;
import com.patient.ui.patientUi.defineView.PopSelectDepart;
import com.patient.ui.patientUi.defineView.PopSelectDepart.PopDepartListener;
import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
import com.patient.ui.patientUi.entity.extendsTable.PatientIntegerationBean;
import com.patient.ui.patientUi.entity.extendsTable.ProvinceBean;
import com.patient.ui.patientUi.entity.extendsTable.YxckDeptBean;
import com.patient.util.BitmapManager;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>ActPatientInterrogation.java</dt>
 * <dd>Description:添加问诊</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-12-26 下午3:27:19</dd>
 * </dl>
 * 
 * @author lihs
 */
public class ActAddInterrgeration extends BaseActivity implements
		OnClickListener {

	private static final String TAG = ActAddInterrgeration.class.getName();

	private EditText patientName;// 疾病名称

	// 科室
	private RelativeLayout rlDepart;
	private TextView tvDepart;
	private ImageView departIcon;

	// 地区
	private RelativeLayout rlArea;
	private TextView tvArea;
	private ImageView areaIcon;
	// 描述问题
	private EditText description;

	private List<YxckDeptBean> yxckDeptTypeList;// "typeName":"内科"
	private List<ProvinceBean> provinceList;// "geoName":"福建"
	private ProvinceBean provience;
	private ProvinceBean city;
	private YxckDeptBean deptType1;
	private YxckDeptBean departs1;
	// 照片展示grid
	private MyGridView gridView = null;
	private ImageAdapter imgAdapter = null;
	private int imgSize = 0;
	// 拍照分享照片路径
	private String mCameraPath = null;
	// 分享文件本地路径
	private ArrayList<String> mListPhotoPath = null;
	// 一行显示图片列数
	private static final int IMG_COLUMN_COUNT = 4;
	// 图片文件最大限制：2M
	public static final long MAX_IMAGEFILE_SIZE = 2 * 1024 * 1024;
	private int maxCount = 4;
	// 添加照片
	public static final int REQUEST_CODE_ADD = 112;
	// 预览照片
	public static final int REQUEST_CODE_PREVIEW = 111;
	// 分享类型（图片或视频）,默认分享图片
	private int shareType = SHARE_TYPE_PIC;
	// 分享 图片音频 视频
	public static final int SHARE_TYPE_PIC = 0;
	public static final int SHARE_TYPE_VIDEO = 1;
	public static final int SHARE_TYPE_AUDIO = 2;
	public static final String KEY_SELECTED_CNT = "key_selected_cnt";
	private File mCurPhotoFile = null;// File For Photo Which Camera Taked
	private String cameraFilePath = "";// 拍照后的地址
	private String url = "";// 图片上传成功返回的地址
	private static final int ACTION_SHARE_FROM_CAMERA = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		LogUtil.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_interration);
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("添加问诊", R.color.white);
		bar.enableRightBtn("完成", 0, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 调用接口进行处理
				// 下面的图片为不必须上传，如果为空，直接调接口，不为空，先上传图片成功后返回图片地址，再调接口提交问诊
				if (verify()) {//这里是非空验证，true为验证成功
					if (null != mListPhotoPath && mListPhotoPath.size() > 0) {
						dg = new CommonWaitDialog(ActAddInterrgeration.this, "正在上传图片");
						asyTaskImage();
					} else {
						addInterration("");
					}
				} 
			}
		});

		patientName = (EditText) findViewById(R.id.tvPatientName);
		description = (EditText) findViewById(R.id.description);

		// 科室
		rlDepart = (RelativeLayout) findViewById(R.id.rl_depart);
		tvDepart = (TextView) findViewById(R.id.tv_depart);
		departIcon = (ImageView) findViewById(R.id.iv_depart);

		// 地区
		rlArea = (RelativeLayout) findViewById(R.id.rl_area);
		tvArea = (TextView) findViewById(R.id.tv_area);
		areaIcon = (ImageView) findViewById(R.id.iv_area);

		rlDepart.setOnClickListener(this);
		rlArea.setOnClickListener(this);
		findViewById(R.id.ll_update_image).setOnClickListener(this);
		getCerDoctorData(null);

		initData();
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
			uploadHead();
		}
	};

	String patientNames;
	String pro;
	String depart;
	String desp;
	public boolean verify(){
		patientNames = patientName.getText().toString();
		pro = tvArea.getText().toString();
		depart = tvDepart.getText().toString();
		desp = description.getText().toString();
		if (TextUtils.isEmpty(patientNames)) {
			CommonUtil.showToast("请输入疑似疾病");
		}else if (TextUtils.isEmpty(depart)) {
			CommonUtil.showToast("请选择科室");
		}else if (TextUtils.isEmpty(pro)) {
			CommonUtil.showToast("请选择地区");
		}else if (TextUtils.isEmpty(desp)) {
			CommonUtil.showToast("请对该疾病简单的描述");
		}else {
			return true;
		}
		return false;
	}

	private void initData() {
		gridView = (MyGridView) findViewById(R.id.img_grid);

		int mImageSpacing = getResources().getDimensionPixelSize(R.dimen.share_img_margin);
		int mImageMargin = getResources().getDimensionPixelSize(R.dimen.share_img_margin);
		int mImagePadding = getResources().getDimensionPixelSize(R.dimen.share_img_padding);

		// 屏幕宽度
		int screenWidth = CommonUtil.getDeviceSize(this).x;
		// 计算得出图片大小
		imgSize = (screenWidth - mImageMargin * 2 - mImagePadding * 2 - (IMG_COLUMN_COUNT - 1) * mImageSpacing) / IMG_COLUMN_COUNT;
		imgAdapter = new ImageAdapter(ActAddInterrgeration.this);
		gridView.setAdapter(imgAdapter);
		if (null == mListPhotoPath) {
			mListPhotoPath = new ArrayList<String>();
		}
	}

	// //// ImageAdapter start
	// ///////////////////////////////////////////////////////////////
	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater = null;

		private ImgViewHolder viewHolder = null;

		public ImageAdapter(Context context) {
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (mListPhotoPath == null) {
				return 0;
			} else {
				if (mListPhotoPath.size() < maxCount) {
					// 未达到最大个数时，还可以继续添加
					return mListPhotoPath.size();
				} else {
					return mListPhotoPath.size();
				}
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View curView = convertView;

			if (convertView == null) {
				curView = (View) layoutInflater.inflate(R.layout.share_img_item, parent, false);
				viewHolder = new ImgViewHolder();
				viewHolder.imageTarget = (ImageView) curView.findViewById(R.id.image_target);
				viewHolder.llDelete = (LinearLayout) curView.findViewById(R.id.ll_delete);
				curView.setTag(viewHolder);
			} else {
				viewHolder = (ImgViewHolder) curView.getTag();
			}

			viewHolder.llDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListPhotoPath.remove(position);
					imgAdapter.notifyDataSetChanged();
				}
			});
			
			// 显示图片
			FrameLayout.LayoutParams imgLp = (FrameLayout.LayoutParams) viewHolder.imageTarget
					.getLayoutParams();
			imgLp.width = imgSize;
			imgLp.height = imgSize;
			viewHolder.imageTarget.setLayoutParams(imgLp);

			viewHolder.imageTarget.setTag(position);
 			Bitmap bit = BitmapManager.decodeSampledBitmapFromFile(
					mListPhotoPath.get(position), imgSize, imgSize);
			// 已选择个数达最大时，不显示添加按钮，最后一个也是正常图片
			viewHolder.imageTarget.setImageBitmap(bit);

			viewHolder.imageTarget
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (CommonUtil.isFastDoubleClick()) {
								return;
							}

							int position = Integer.parseInt(String.valueOf(v
									.getTag()));

							// 点击图片或视频，进入图片/视频预览界面（可移除图片，即不选择）。
							Bundle bundle = new Bundle();
							bundle.putStringArrayList(Intent.EXTRA_STREAM, mListPhotoPath);
							bundle.putBoolean(ActSendComment.KEY_ACTION_CUSTOM, true);
							bundle.putInt(ActSendComment.KEY_FOR_SHARE_TYPE, shareType);
							bundle.putString(PreviewActivity.ACTIVITY_FLAG, PreviewActivity.AVITVITY_START_FOR_RESULT);
							bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, position);
							Intent intent = new Intent(ActAddInterrgeration.this, PreviewActivity.class);
							intent.putExtras(bundle);
							startActivityForResult(intent, REQUEST_CODE_PREVIEW);
						}
					});
			return curView;
		}
	}

	private static class ImgViewHolder {
		ImageView imageTarget;
		LinearLayout llDelete;
	}

	// //// ImageAdapter end
	// ///////////////////////////////////////////////////////////////
	private void sharePhotoMenu() {

		UpdateImageMenu buttonMenu = new UpdateImageMenu(this,"您上传的照片只有接诊您的医生可见");
		buttonMenu.setListener(new DoPhtot() {

			@Override
			public void takePhoto() {
				// 拍照
				if (mListPhotoPath != null
						&& mListPhotoPath.size() >= ActAddInterrgeration.IMG_COLUMN_COUNT) {
					CommonUtil.showToast("本次分享的照片最多为4张",
							ActAddInterrgeration.this);
					return;
				}
				shareFromCamera();
			}

			@Override
			public void selecAblum() {
				// 从手机选图
				Bundle bundle = new Bundle();
				bundle.putString(MultiBucketChooserActivity.KEY_ACTIVITY_TYPE,MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT);
				bundle.putInt(KEY_SELECTED_CNT, mListPhotoPath.size());
				bundle.putInt(CommonConstant.KEY_RESLUT,4);
				bundle.putBoolean(MultiBucketChooserActivity.KEY_IS_MULTI, true);
				Intent intent = new Intent(ActAddInterrgeration.this,MultiBucketChooserActivity.class);
				if (shareType == SHARE_TYPE_PIC) {
					intent.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE,
							MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
				} else {
					// TODO 分享其它的东西
				}
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_ADD);
			}
		});
		buttonMenu.show();
	}

	private void shareFromCamera() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhotoNative();
		} else {
			Toast.makeText(this, "SD卡未发现", Toast.LENGTH_SHORT).show();
		}
	}

	protected void doTakePhotoNative() {
		try {// Launch camera to take photo for selected contact

			mCurPhotoFile = new File(Environment.getExternalStorageDirectory(),
					getPhotoFileName());// attach
			if (mCurPhotoFile != null) {

				final Intent intent = getTakePickIntent(mCurPhotoFile);
				cameraFilePath = mCurPhotoFile.getPath();
				startActivityForResult(intent, ACTION_SHARE_FROM_CAMERA);
			} else {
				Toast.makeText(this, "未发现SD卡", Toast.LENGTH_SHORT).show();
			}
		} catch (ActivityNotFoundException e) {
			LogUtil.d(TAG, "ActivityNotFoundException");
		}
	}

	private String getPhotoFileName() {
		String timeStr = "IMG_" + System.currentTimeMillis() + ".jpg";
		return timeStr;
	}

	public static Intent getTakePickIntent(File f) {
		// 部分三星手机，在启动照相机后，onActivityResult返回的intent为空，
		// 不能将照相后的图片传递到本页面,故此处用指定路径的形式做透传
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CODE_PREVIEW && data != null
					&& data.getExtras() != null) {
				// 预览返回
				mListPhotoPath = data.getExtras().getStringArrayList(
						Intent.EXTRA_STREAM);
				 imgAdapter.notifyDataSetChanged();

			} else if (requestCode == REQUEST_CODE_ADD && data != null
					&& data.getExtras() != null) {
				// 增加照片
				ArrayList<String> addedPathList = data.getExtras()
						.getStringArrayList(Intent.EXTRA_STREAM);

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
						Toast.makeText(ActAddInterrgeration.this,
								"您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片",
								Toast.LENGTH_SHORT).show();
					}

					 imgAdapter.notifyDataSetChanged();
				}
			} else if (requestCode == ACTION_SHARE_FROM_CAMERA) {
				if (!TextUtils.isEmpty(cameraFilePath)) {
					CommonUtil.scanFileAsync(this, cameraFilePath);
					LogUtil.d(TAG, "拍照返回的图片路径：" + cameraFilePath);

					if (mListPhotoPath == null) {
						mListPhotoPath = new ArrayList<String>();
					}
					if (!mListPhotoPath.contains(cameraFilePath)) {
						mListPhotoPath.add(cameraFilePath);
					}
					 imgAdapter.notifyDataSetChanged();

				} else {
					CommonUtil.showToast("解析数据出错", this);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_area:
			if (provinceList == null || provinceList.size() == 0) {
				getCerDoctorData(v);
			} else {
				popArea(v);
			}
			break;
		case R.id.rl_depart:
			if (yxckDeptTypeList == null || yxckDeptTypeList.size() == 0) {
				getCerDoctorData(v);
			} else {
				popDepart(v);
			}
			break;
		case R.id.ll_update_image:
			if (mListPhotoPath.size() < maxCount) {
				sharePhotoMenu();
			}else {
				CommonUtil.showToast("只可以上传4张图片");
			}
			break;

		default:
			break;
		}
	}

	private void popArea(View view) {

		areaIcon.setBackgroundResource(R.drawable.ic_top_angnals1);
		PopDoctorArea areaPop = new PopDoctorArea(this, view, tvArea.getText()
				.toString(), provinceList);
		areaPop.getPopupWindow().setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				areaIcon.setBackgroundResource(0);
				areaIcon.setBackgroundResource(R.drawable.ic_top_angnals);
			}
		});
		areaPop.setPopDepartListeners(new PopDepartListeners() {

			@Override
			public void doRefresh(ProvinceBean provience1, ProvinceBean city1) {

				provience = provience1;
				city = city1;
				areaIcon.setBackgroundResource(R.drawable.ic_top_angnals);
				tvArea.setText(provience1.geoName + " " + city1.geoName);

			}
		});
	}

	private void popDepart(final View view) {

		departIcon.setBackgroundResource(R.drawable.ic_top_angnals1);
		PopSelectDepart departPop = new PopSelectDepart(this, view, tvDepart
				.getText().toString(), yxckDeptTypeList, false);
		departPop.getPopupWindow().setOnDismissListener(
				new OnDismissListener() {

					@Override
					public void onDismiss() {
						departIcon.setBackgroundResource(0);
						departIcon
								.setBackgroundResource(R.drawable.ic_top_angnals);
					}
				});

		departPop.setPopDepartListener(new PopDepartListener() {

			@Override
			public void doRefresh(YxckDeptBean deptType, YxckDeptBean departs) {

				departIcon.setBackgroundResource(R.drawable.ic_top_angnals);
				// 掉接口，处理逻辑
				deptType1 = deptType;
				departs1 = departs;
				tvDepart.setText(deptType1.typeName + " " + departs1.deptName);
			}
		});
	}

	// 获取信息
	private void getCerDoctorData(final View v) {

		RequestParams params = new RequestParams();

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_CER_DOCTOR_DATA, params,
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
								yxckDeptTypeList = JSONConverter.convertToArray(
										json.optString("yxckDeptTypeList"),
										new TypeToken<List<YxckDeptBean>>() {
										});
								provinceList = JSONConverter.convertToArray(
										json.optString("provinceList"),
										new TypeToken<List<ProvinceBean>>() {
										});
								if (v != null) {
									switch (v.getId()) {
									case R.id.tv_area:
										// pop
										popArea(v);
										break;
									case R.id.tv_depart:
										// pop
										popDepart(v);
										break;
									}
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

	// 添加问诊数据 url:是图片上传成功后返回的url
	private void addInterration(final String url) {

		final CommonWaitDialog dg = new CommonWaitDialog(this, "请稍等");
		RequestParams params = new RequestParams();
		if (!TextUtils.isEmpty(url)) {// 图片不为空时加这个参数
			params.addQueryStringParameter("basicImage", url);// 图片
		}
		params.addQueryStringParameter("patientId",
				LoginPreference.getUserInfo().partyId);
		params.addQueryStringParameter("susDiagnoEnum", patientNames);
		params.addQueryStringParameter("provinceId", provience.geoId);
		params.addQueryStringParameter("cityId", city.geoId);
		params.addQueryStringParameter("description", desp);
		params.addQueryStringParameter("departmentId", departs1.deptId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.CREATE_INTERROGATION, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {

						dg.clearAnimation();
						if (result != null) {

							LogUtil.d2File(result.toString());
							final JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json
										.optString(CommonConstant.SUCCESS_RESPONSEMESSAGE);
								if ("success".equals(code)) {
									CommonDialog dg = new CommonDialog(
											ActAddInterrgeration.this);
									dg.setTitle(null);
									dg.setMessage("问诊已经提交推送给医生，请等待医生给您开放日程");
									dg.setPositiveButton(
											new BtnClickedListener() {

												@Override
												public void onBtnClicked() {

													String askId = json
															.optString("interrogationId");
													String askTime = json
															.optString("createdStamp");
													PatientIntegerationBean bean = new PatientIntegerationBean();
													bean.interrogationId = askId;
													bean.createTime = askTime;
													bean.description = desp;
													bean.basicImage = url;
													Intent i = new Intent();
													i.putExtra(
															CommonConstant.KEY_RESLUT,
															bean);
													setResult(
															Activity.RESULT_OK,
															i);
													finish();
												}
											}, "确定");
									dg.showDialog();
								} else {
									CommonUtil.showToast("新建问诊失败");
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						dg.clearAnimation();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}

	/** 上传头像 */
	CommonWaitDialog dg;
	private void uploadHead() {
		RequestParams params = new RequestParams();
		for (int i = 0; i < mListPhotoPath.size(); i++) {
			params.addBodyParameter("image" + i, new File(mListPhotoPath.get(i)));
		}
		params.addQueryStringParameter("flag", "dtsc");

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.UPLOAD_ICON, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {
						if (result != null) {
							dg.clearAnimation();
							LogUtil.d2File(result.toString());
							LogUtil.d(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								String urls = json.optString("outputList");
								if (code == CommonConstant.SUCCESS) {
									url = urls;
									addInterration(url);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								dg.clearAnimation();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						LogUtil.d2File(msg);
						dg.clearAnimation();
						CommonUtil.showError(error, msg);
					}
				});
	}
}
