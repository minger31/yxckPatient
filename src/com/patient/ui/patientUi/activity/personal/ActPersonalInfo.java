package com.patient.ui.patientUi.activity.personal;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.commonent.UpdateImageMenu;
import com.patient.commonent.UpdateImageMenu.DoPhtot;
import com.patient.constant.CommonConstant;
import com.patient.constant.EnumConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.preference.LoginPreference;
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.activity.common.ActChoosePCH;
import com.patient.ui.patientUi.activity.common.ActSendComment;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.defineView.PopSex;
import com.patient.ui.patientUi.defineView.PopSex.SexRefreshSearch;
import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.ui.patientUi.entity.baseTable.ProvCityBean;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActPersonalInfo extends BaseActivity implements OnClickListener {

	private PartyBean bean = null;
	
	private ImageView ivHead = null;// 头像
	private TextView tvPhone = null;// 手机号
	private TextView tvName = null;// 姓名
	private TextView tvSex = null;// 性别
	private TextView tvAge = null;// 年龄
	private TextView tvAddress = null;// 地址
	private TextView tvEmail = null;// 邮箱

	private File mCurPhotoFile = null;
	private String cameraFilePath = "";
	private static final int ACTION_SHARE_FROM_CAMERA = 1000;
	private Bitmap bitmap;
	
	// 男
	private String sexId = "sexEnum_0";
	
	private ProvCityBean provCityBean;// 医院的bean
	private ProvCityBean provinceBean;// 省
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_personal_info);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("个人信息", 1);
		bar.enableRightBtn("", -1, null);
		
		init();
	}

	private void setValue() {
		bean = LoginPreference.getUserInfo();

		tvPhone.setText(bean.partyId);// 手机号
		tvName.setText(bean.partyName);// 姓名
		tvSex.setText(EnumConstant.enumMap.get(bean.sexEnum));// 性别
		tvAge.setText(bean.age);// 出生日期
		if (!TextUtils.isEmpty(bean.provinceName)) {
			tvAddress.setText(bean.provinceName + "省 " + bean.cityName);
		}
		tvEmail.setText(bean.email);// 邮件

		LoaderImage.getInstance(R.drawable.ic_heads).ImageLoaders(bean.partyheadUrl, ivHead);
	}

	private void init() {
		
		ivHead = (ImageView) findViewById(R.id.iv_head);// 头像
		tvPhone = (TextView) findViewById(R.id.tv_phone);// 手机号
		tvName = (TextView) findViewById(R.id.tv_name);// 姓名
		tvSex = (TextView) findViewById(R.id.tv_sex);// 性别
		tvAge = (TextView) findViewById(R.id.tv_age);// 年龄
		tvAddress = (TextView) findViewById(R.id.tv_address);// 地址
		tvEmail = (TextView) findViewById(R.id.tv_email);// 邮箱

		findViewById(R.id.ll_head).setOnClickListener(this);// 头像
		findViewById(R.id.ll_phone).setOnClickListener(this);// 手机号
		findViewById(R.id.ll_name).setOnClickListener(this);// 姓名
		findViewById(R.id.ll_sex).setOnClickListener(this);// 性别
		findViewById(R.id.ll_age).setOnClickListener(this);// 年龄
		findViewById(R.id.ll_address).setOnClickListener(this);// 地址
		findViewById(R.id.ll_email).setOnClickListener(this);// 邮箱
	}

	@Override
	public void onClick(View v) {
		final CommonDialog dialog = new CommonDialog(ActPersonalInfo.this);
		switch (v.getId()) {
		case R.id.ll_head:// 头像
			sharePhotoMenu();
			break;
		case R.id.ll_phone:// 手机号
			break;
		case R.id.ll_name:// 姓名
			final View view = LayoutInflater.from(ActPersonalInfo.this).inflate(R.layout.dialog_layout, null);
			final TextView tv = (TextView) view.findViewById(R.id.info);
			tv.setText(tvName.getText().toString());
			dialog.addView(view);
			dialog.setTitle("修改姓名");
			dialog.setPositiveButton(new BtnClickedListener() {

				@Override
				public void onBtnClicked() {
					if (TextUtils.isEmpty(tv.getText().toString().trim())) {
						return;
					} else if (tv.getText().toString().trim().equals(tvName.getText().toString())) {
						return;
					} else {
						updateOutsideExperts("firstName",tvName,tv.getText().toString().trim());
					}
				}
			}, getResources().getString(R.string.ok));
			dialog.setCancleButton(null,getResources().getString(R.string.cancle));
			dialog.showDialog();
			break;
		case R.id.ll_sex:// 性别
			PopSex sex = new PopSex(ActPersonalInfo.this, v, tvSex.getText().toString(),true);
			sex.setRefreshListener(new SexRefreshSearch() {

				@Override
				public void doRefresh(int choseSearch) {
					if (choseSearch == 0) {
						// 提交接口数据
						sexId = "sexEnum_0";
					} else {
						sexId = "sexEnum_1";
					}
					if (!EnumConstant.enumMap.get(sexId).equals(tvSex.getText().toString())) {
						updateOutsideExperts("sexEnum",tvSex,sexId);
					}
				}
			});
			break;
		case R.id.ll_email:// 邮箱
			final View view2 = LayoutInflater.from(ActPersonalInfo.this).inflate(R.layout.dialog_layout, null);
			final TextView tv2 = (TextView) view2.findViewById(R.id.info);
			
			tv2.setText(tvEmail.getText().toString());
			dialog.addView(view2);
			dialog.setTitle("修改邮箱");
			dialog.setPositiveButtonOpen(new BtnClickedListener() {

				@Override
				public void onBtnClicked() {
					if (TextUtils.isEmpty(tv2.getText().toString().trim())) {
						tv2.setError("请输入正确的邮箱");
						return;
					} else if (tvEmail.getText().toString().equals(tv2.getText().toString())) {
						dialog.dismiss();
						return;
					} else if (!Pattern.compile("\\w+([-+.]\\w)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*").matcher(tv2.getText().toString()).matches()){
						tv2.setError("请输入正确的邮箱");
						return;
					} else {
						updateOutsideExperts("email",tvEmail,tv2.getText().toString().trim());
						dialog.dismiss();
					}
				}
			}, getResources().getString(R.string.ok));
			dialog.setCancleButton(null,getResources().getString(R.string.cancle));
			dialog.showDialog();	
			break;
		case R.id.ll_age:// 年龄
			final View view1 = LayoutInflater.from(ActPersonalInfo.this).inflate(R.layout.dialog_layout, null);
			final TextView tv1 = (TextView) view1.findViewById(R.id.info);
			tv1.setInputType(InputType.TYPE_CLASS_NUMBER);
			tv1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
			
			tv1.setText(tvAge.getText().toString());
			dialog.addView(view1);
			dialog.setTitle("修改年龄");
			dialog.setPositiveButton(new BtnClickedListener() {

				@Override
				public void onBtnClicked() {
					if (TextUtils.isEmpty(tv1.getText().toString().trim())) {
						tv1.setError("请输入正确的年龄");
					} else if (tv1.getText().toString().trim().equals(tvAge.getText().toString())) {
						return;
					} else {
						updateOutsideExperts("age",tvAge,tv1.getText().toString().trim());
					}
				}
			}, getResources().getString(R.string.ok));
			dialog.setCancleButton(null,getResources().getString(R.string.cancle));
			dialog.showDialog();
			break;
		case R.id.ll_address:// 地址
			Intent intent = new Intent(ActPersonalInfo.this, ActChoosePCH.class);
			startActivityForResult(intent, CommonConstant.SUCCESS);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setValue();
	}
	
	/** 修改数据 */
	public void updateOutsideExperts(final String key, final TextView tv,final String value) {

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId",LoginPreference.getKeyValue(LoginType.PARTY_ID, "000000"));// 参数
		params.addQueryStringParameter("userLoginId",LoginPreference.getKeyValue(LoginType.PARTY_ID, "000000"));// 参数

		
		if (tv.getId() == R.id.tv_address) {// 工作单位
			//省市id 
			params.addQueryStringParameter("provinceId", provinceBean.geo_id);
			params.addQueryStringParameter("cityId", provCityBean.geo_id);
		}
		 
		if (tv.getId() == R.id.tv_name || tv.getId() == R.id.tv_email || tv.getId() == R.id.tv_age) {//姓名、邮箱、年龄
			params.addQueryStringParameter(key, value);
		} else if (tv.getId() == R.id.tv_sex) {// 性别
			params.addQueryStringParameter(key, sexId);
		}
		

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.UPDATE_OUTSIDE_EXPERTS, params,
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
								String code = json.optString("responseMessage");

								if (CommonConstant.STATUS_SUCCESS.equals(code)) {

									CommonUtil.showToast("修改成功");
									tv.setText(value);
									if (tv.getId() == R.id.tv_name) {// 姓名
										bean.partyName = value;
									} else if (tv.getId() == R.id.tv_sex) {// 性别
										bean.sexEnum = value;
										tv.setText(EnumConstant.enumMap.get(value));
									} else if (tv.getId() == R.id.tv_email) {// 邮箱
										bean.email = value;
									} else if (tv.getId() == R.id.tv_age) {// 年龄
										bean.age = value;
									}  else if (tv.getId() == R.id.tv_address) {// 工作单位
										//省市id 
										
										bean.provinceName = provinceBean.geo_name;//省
										bean.cityName = provCityBean.geo_name;//市
										tv.setText(provinceBean.geo_name + "省 " + provCityBean.geo_name);
									}
								} else {
									// CommonUtil.showToast("修改失败，请稍后重试");
								}
							} catch (JSONException e) {
								e.printStackTrace();
//								 wdg.clearAnimation();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
//						 wdg.clearAnimation();
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}
	

//  头像 start===========================================================================
	private void sharePhotoMenu() {

		UpdateImageMenu buttonMenu = new UpdateImageMenu(this,"请上传您的照片");
		buttonMenu.setListener(new DoPhtot() {
			
			@Override
			public void takePhoto() {
				shareFromCamera();
			}
			
			@Override
			public void selecAblum() {
				Bundle bundle = new Bundle();
				bundle.putString(MultiBucketChooserActivity.KEY_ACTIVITY_TYPE,MultiBucketChooserActivity.ACTIVITY_START_FOR_RESULT);
				Intent intent = new Intent(ActPersonalInfo.this,MultiBucketChooserActivity.class);
				intent.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE,MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
				intent.putExtra(MultiBucketChooserActivity.KEY_IS_MULTI, false);
				intent.putExtras(bundle);
				startActivityForResult(intent, ActSendComment.REQUEST_CODE_ADD);
			}
		});
 
		buttonMenu.show();
	}

	private void shareFromCamera() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhotoNative();
		}
	}

	//相机拍照
	protected void doTakePhotoNative() {
		try {
			// 启动相机拍照为选定的联系
			mCurPhotoFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());// attach
			if (mCurPhotoFile != null) {
				final Intent intent = getTakePickIntent(mCurPhotoFile);
				cameraFilePath = mCurPhotoFile.getPath();
				startActivityForResult(intent, ACTION_SHARE_FROM_CAMERA);
			}
		} catch (ActivityNotFoundException e) {

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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (Activity.RESULT_CANCELED == resultCode) {
			return;
		}

		switch (requestCode) {

		case ActSendComment.REQUEST_CODE_ADD:
			List<String> path = data.getStringArrayListExtra(Intent.EXTRA_STREAM);
			if (path != null && path.size() > 0) {
				bitmap = BitmapFactory.decodeFile(path.get(0));
				uploadHead(path.get(0));
			} else {
				CommonUtil.showToast("获取相册图片异常");
			}
			return;
		case ACTION_SHARE_FROM_CAMERA: 	// 手机拍照的结果
			if (!TextUtils.isEmpty(cameraFilePath)) {
				CommonUtil.scanFileAsync(ActPersonalInfo.this, cameraFilePath);
			}
			// 直接绑定图片到 imageview
			bitmap = BitmapFactory.decodeFile(cameraFilePath);
			uploadHead(cameraFilePath);
			break;
		case CommonConstant.SUCCESS ://选择省市医院
			provCityBean = (ProvCityBean) data.getExtras().getSerializable(CommonConstant.KEY_RESLUT);//市
			provinceBean = (ProvCityBean) data.getExtras().getSerializable(CommonConstant.JSON);//省
			updateOutsideExperts(null,tvAddress,null);
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}
//  头像 end===========================================================================

	/** 上传头像 */
	private void uploadHead(String file) {
		final CommonWaitDialog dg = new CommonWaitDialog(this, "正在上传头像");
		RequestParams params = new RequestParams();

		params.addBodyParameter("image", new File(file));
		params.addQueryStringParameter("partyId",LoginPreference.getKeyValue(LoginType.PARTY_ID, ""));

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
							ivHead.setImageBitmap(bitmap);
							CommonUtil.showToast("上传头像成功");

							LogUtil.d2File(result.toString());
							LogUtil.d(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								String headurl = json.optString("outputList");
								if (code == CommonConstant.SUCCESS) {
									CommonUtil.showToast("上传头像成功");
									bean.partyheadUrl = headurl;
									LoaderImage.getInstance(R.drawable.ic_heads).ImageLoaders(headurl, ivHead);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						dg.clearAnimation();
						CommonUtil.showToast("上传头像失败");
						LogUtil.d2File(msg);
						CommonUtil.showError(error, msg);
					}
				});
	}

}
