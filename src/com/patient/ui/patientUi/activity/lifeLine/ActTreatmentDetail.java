package com.patient.ui.patientUi.activity.lifeLine;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.library.jsonConvert.JSONConverter;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.common.PreviewActivity;
import com.patient.ui.patientUi.adapter.BasicImageDetailAdapter;
import com.patient.ui.patientUi.adapter.CheckImageDetailAdapter;
import com.patient.ui.patientUi.adapter.CureImageDetileAdapter;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.util.CommonUtil;
import com.patient.util.LoaderImage;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * <dl>
 * <dt>FrgPatientEducation.java</dt>
 * <dd>Description:就诊信息</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-11-18 上午10:18:11</dd>
 * </dl>
 */
public class ActTreatmentDetail extends BaseActivity implements OnClickListener {

	private static final String TAG = ActTreatmentDetail.class.getName();

	// 医生的就诊信息，可以作为详情和编辑使用
	private TreatmentBean bean;
	private String treatmentId;

	private TextView tvDate = null;// 就诊时间
	private TextView tvPatientDisease = null;// 诊断疾病
	private TextView tvReport = null;// 主诉
	private TextView tvCardNumber;// 就诊卡号

	// 基本数据的图片信息
	private GridView basicGv;
	private BasicImageDetailAdapter basicImageAdapter;
	private List<String> basicPhotos = null;
	private ImageView basicIv;
	private ImageView basicPack;
	private FrameLayout basicFl;

	private GridView checkGv;
	private CheckImageDetailAdapter checkImageAdapter;
	private List<String> checkPhotos = null;
	private ImageView checkIv;
	private ImageView checkPack;
	private FrameLayout checkFl;

	private GridView cureGv;
	private CureImageDetileAdapter cureImageAdapter;
	private List<String> curePhotos = null;
	private ImageView cureIv;
	private ImageView curePack;
	private FrameLayout cureFl;

	private int bucketBgMargin = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_treatment_detail);

		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("就诊史", R.color.white);

		treatmentId = getIntent().getStringExtra(CommonConstant.KEY_RESLUT);

		init();
		getTreatment();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	private void init() {

		bucketBgMargin = getResources().getDimensionPixelSize(R.dimen.multi_bucket_chooser_bg_margin);

		basicFl = (FrameLayout) findViewById(R.id.basic_fl);
		checkFl = (FrameLayout) findViewById(R.id.check_fl);
		cureFl = (FrameLayout) findViewById(R.id.cure_fl);

		tvDate = (TextView) findViewById(R.id.tv_date);// 就诊时间
		tvPatientDisease = (TextView) findViewById(R.id.tv_patient_disease);// 诊断疾病
		tvReport = (TextView) findViewById(R.id.tv_report);// 主诉
		tvCardNumber = (TextView) findViewById(R.id.tv_card_number);// 就诊卡号
		findViewById(R.id.ll_record_playing).setOnClickListener(this);// 就诊录音

		basicGv = (GridView) findViewById(R.id.basicInfoGv);
		checkGv = (GridView) findViewById(R.id.checkGv);
		cureGv = (GridView) findViewById(R.id.cureGv);

		basicImageAdapter = new BasicImageDetailAdapter(this, basicGv);
		basicIv = (ImageView) findViewById(R.id.image_bg_basic);
		basicPack = (ImageView) findViewById(R.id.pack_baisc);
		basicIv.setOnClickListener(this);
		basicPack.setOnClickListener(this);

		checkImageAdapter = new CheckImageDetailAdapter(this, checkGv);
		checkIv = (ImageView) findViewById(R.id.image_bg_check);
		checkPack = (ImageView) findViewById(R.id.pack_check);
		checkIv.setOnClickListener(this);
		checkPack.setOnClickListener(this);

		cureImageAdapter = new CureImageDetileAdapter(this, cureGv);
		cureIv = (ImageView) findViewById(R.id.image_bg_cure);
		curePack = (ImageView) findViewById(R.id.pack_cure);
		cureIv.setOnClickListener(this);
		curePack.setOnClickListener(this);

		basicGv.setAdapter(basicImageAdapter);
		checkGv.setAdapter(checkImageAdapter);
		cureGv.setAdapter(cureImageAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_bg_basic:
			basicFl.setVisibility(View.GONE);
			basicGv.setVisibility(View.VISIBLE);
			basicPack.setVisibility(View.VISIBLE);
			basicImageAdapter.setData(basicPhotos);
			break;
		case R.id.pack_baisc:
			basicFl.setVisibility(View.VISIBLE);
			basicGv.setVisibility(View.GONE);
			basicPack.setVisibility(View.GONE);
			break;
		case R.id.image_bg_check:
			checkFl.setVisibility(View.GONE);
			checkGv.setVisibility(View.VISIBLE);
			checkPack.setVisibility(View.VISIBLE);
			checkImageAdapter.setData(checkPhotos);
			break;
		case R.id.pack_check:
			checkFl.setVisibility(View.VISIBLE);
			checkGv.setVisibility(View.GONE);
			checkPack.setVisibility(View.GONE);
			break;
		case R.id.image_bg_cure:
			cureFl.setVisibility(View.GONE);
			cureGv.setVisibility(View.VISIBLE);
			curePack.setVisibility(View.VISIBLE);
			cureImageAdapter.setData(curePhotos);
			break;
		case R.id.pack_cure:
			cureFl.setVisibility(View.VISIBLE);
			cureGv.setVisibility(View.GONE);
			curePack.setVisibility(View.GONE);
			break;
		case R.id.ll_record_playing:// 就诊录音播放
			findViewById(R.id.ll_record_playing).setEnabled(false);// 点击一次不可点击，播放完毕之后...
			vodiceRightAnimation = (AnimationDrawable) findViewById(R.id.paly_voice_btn).getBackground();
			vodiceRightAnimation.start();
			playVodic(path);
			break;
		default:
			break;
		}
	}

	/** 播放录音 start *************************************************/
	private AnimationDrawable vodiceRightAnimation;// 播放声音动画
	private static boolean playState = false; // 播放状态
	private MediaPlayer mediaPlayer;
	private String path;// 录音地址

	public void playVodic(String url) {
		System.out.println("播放地址" + url);
		if (!playState) {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(url);
				mediaPlayer.prepare();
				mediaPlayer.start();
				playState = true;
				// 设置播放结束时监听
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (playState) {
							// vodiceRightAnimation.stop();
							playState = false;
							vodiceRightAnimation.stop();
							findViewById(R.id.paly_voice_btn).setBackgroundResource(R.anim.record_voice_animation);
							findViewById(R.id.ll_record_playing).setEnabled(true);// 播放完毕才可点击
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				playState = false;
			} else {
				playState = false;
			}
		}
	}

	/** 播放录音 end *************************************************/
	public static final String KEY_IS_VIDEO = "key_is_video";
	public static final String KEY_SELECTED_CNT = "key_selected_cnt";
	public static final String KEY_ACTION_CUSTOM = "action_custom";
	public static final String KEY_FOR_SHARE_TYPE = "key_for_sharetype";
	public static final String KEY_SHARE_MAN = "key_share_man";
	public static final String KEY_FILE_PATH = "file_path";

	// 添加照片
	public static final int REQUEST_CODE_ADD = 112;
	// 预览照片
	public static final int REQUEST_CODE_PREVIEW = 111;
	// 视频文件最大限制：10M
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
	// 图片文件最大限制：2M
	public static final long MAX_IMAGEFILE_SIZE = 2 * 1024 * 1024;

	// 分享 图片音频 视频
	public static final int SHARE_TYPE_PIC = 0;

	public void previewIcon(List<String> mListPhotoPath, int position) {

		Bundle bundle = new Bundle();
		bundle.putStringArrayList(Intent.EXTRA_STREAM,(ArrayList<String>) mListPhotoPath);
		bundle.putBoolean(KEY_ACTION_CUSTOM, true);
		bundle.putInt(KEY_FOR_SHARE_TYPE, SHARE_TYPE_PIC);
		bundle.putString(PreviewActivity.ACTIVITY_FLAG,PreviewActivity.AVITVITY_START_FOR_RESULT);
		bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, position);
		Intent intent = new Intent(this, PreviewActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, REQUEST_CODE_PREVIEW);
	}

	/** 就诊信息 */
	public void getTreatment() {
		LogUtil.d(TAG, "-------- getTreatment()");
		final CommonWaitDialog wdg = new CommonWaitDialog(ActTreatmentDetail.this, "加载数据中");

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("treatmentId", treatmentId);

		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.GET_TREATMENT, params,new RequestCallBack<Object>() {

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
									List<TreatmentBean> list = JSONConverter.convertToArray(json.optString("outputList"), new TypeToken<List<TreatmentBean>>() {});
									if (list != null) {
										bean = list.get(0);
										setValue(bean);
									}
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

	private void setValue(TreatmentBean beans) {
		if (!TextUtils.isEmpty(beans.soundRecorder)) {
			findViewById(R.id.ll_record_playing).setVisibility(View.VISIBLE);// 如果有录音文件则显示
			path = beans.soundRecorder;// 就诊录音
		}
		tvDate.setText(beans.treatmentTime);// 就诊时间
		tvPatientDisease.setText(beans.patientDisease);// 诊断疾病
		tvReport.setText(beans.report);
		if (TextUtils.isEmpty(beans.treatmentCardId)) {
			tvCardNumber.setText("未填写");// 就诊卡号
			tvCardNumber.setTextColor(getResources().getColor(R.color.color_979797));
		} else {
			tvCardNumber.setText(beans.treatmentCardId);// 就诊卡号
		}
		initIcon(basicIv, beans.basicImage);
		initIcon(checkIv, beans.examineImages);
		initIcon(cureIv, beans.cureImages);
	}

	private void initIcon(ImageView v, String icons) {

		int count = 0;
		switch (v.getId()) {
		case R.id.image_bg_basic:
			basicPhotos = getListString(icons);
			if (basicPhotos != null) {
				count = basicPhotos.size();
			}
			if (count > 3) {
				basicFl.setVisibility(View.VISIBLE);
				basicGv.setVisibility(View.GONE);
				basicPack.setVisibility(View.GONE);
				showFirstIcon(v, basicPhotos.get(0), count);
			} else if (count >= 1) {
				basicFl.setVisibility(View.GONE);
				basicGv.setVisibility(View.VISIBLE);
				basicPack.setVisibility(View.GONE);
				basicImageAdapter.setData(basicPhotos);
			} else {
				basicFl.setVisibility(View.GONE);
				basicGv.setVisibility(View.GONE);
				basicPack.setVisibility(View.GONE);
			}
			break;
		case R.id.image_bg_check:
			checkPhotos = getListString(icons);
			if (checkPhotos != null) {
				count = checkPhotos.size();
			}
			if (count > 3) {
				checkFl.setVisibility(View.VISIBLE);
				checkGv.setVisibility(View.GONE);
				checkPack.setVisibility(View.GONE);
				showFirstIcon(v, checkPhotos.get(0), count);
			} else if (count >= 1) {
				checkFl.setVisibility(View.GONE);
				checkGv.setVisibility(View.VISIBLE);
				checkPack.setVisibility(View.GONE);
				checkImageAdapter.setData(checkPhotos);
			} else {
				checkFl.setVisibility(View.GONE);
				checkGv.setVisibility(View.GONE);
				checkPack.setVisibility(View.GONE);
			}
			break;
		case R.id.image_bg_cure:
			curePhotos = getListString(icons);
			if (curePhotos != null) {
				count = curePhotos.size();
			}
			if (count > 3) {
				cureFl.setVisibility(View.VISIBLE);
				cureGv.setVisibility(View.GONE);
				curePack.setVisibility(View.GONE);
				showFirstIcon(v, curePhotos.get(0), count);
			} else if (count >= 1) {
				cureFl.setVisibility(View.GONE);
				cureGv.setVisibility(View.VISIBLE);
				curePack.setVisibility(View.GONE);
				cureImageAdapter.setData(curePhotos);
			} else {
				cureFl.setVisibility(View.GONE);
				cureGv.setVisibility(View.GONE);
				curePack.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	private void showFirstIcon(ImageView v, String path, int count) {

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
		if (count > 3) {
			lp.leftMargin = 0;
			lp.topMargin = 0;
			lp.rightMargin = 0;
			lp.bottomMargin = 0;
			v.setLayoutParams(lp);
			v.setImageResource(R.drawable.bucket_img_bg);
		} else {
			lp.leftMargin = bucketBgMargin;
			lp.topMargin = bucketBgMargin;
			lp.rightMargin = bucketBgMargin;
			lp.bottomMargin = bucketBgMargin;
			v.setLayoutParams(lp);
			v.setImageResource(android.R.color.white);
		}

		ImageView view = null;
		switch (v.getId()) {
		case R.id.image_bg_basic:
			view = (ImageView) findViewById(R.id.image_target_basic);
			break;
		case R.id.image_bg_check:
			view = (ImageView) findViewById(R.id.image_target_check);
			break;
		case R.id.image_bg_cure:
			view = (ImageView) findViewById(R.id.image_target_cure);
			break;
		default:
			break;
		}
		if (!TextUtils.isEmpty(path) && path.startsWith("http")) {
			LoaderImage.getInstance(-1).ImageLoaders(path, view);
		}

	}

	private List<String> getListString(String icons) {

		List<String> temp = null;
		if (!TextUtils.isEmpty(icons)) {
			String temp1[] = icons.split(",");
			temp = new ArrayList<String>();
			for (String string : temp1) {
				temp.add(string);
			}
		}
		return temp;
	}
}
