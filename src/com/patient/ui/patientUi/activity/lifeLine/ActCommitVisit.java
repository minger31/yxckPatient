package com.patient.ui.patientUi.activity.lifeLine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.patient.commonent.CommonDialog;
import com.patient.commonent.CommonDialog.BtnClickedListener;
import com.patient.commonent.CommonWaitDialog;
import com.patient.commonent.TitleBar;
import com.patient.constant.CommonConstant;
import com.patient.constant.UrlConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.activity.common.ActChoseDiease;
import com.patient.ui.patientUi.activity.common.AudioRecorder;
import com.patient.ui.patientUi.activity.common.BaseActivity;
import com.patient.ui.patientUi.activity.common.DownImageUtil;
import com.patient.ui.patientUi.activity.common.PopPicDialog;
import com.patient.ui.patientUi.activity.common.PreviewActivity;
import com.patient.ui.patientUi.adapter.BasicImageAdapter;
import com.patient.ui.patientUi.adapter.BasicImageAdapter.RefreshBasicInfo;
import com.patient.ui.patientUi.adapter.CheckImageAdapter;
import com.patient.ui.patientUi.adapter.CheckImageAdapter.RefreshCheckInfo;
import com.patient.ui.patientUi.adapter.CureImageAdapter;
import com.patient.ui.patientUi.adapter.CureImageAdapter.RefreshCureInfo;
import com.patient.ui.patientUi.entity.baseTable.TreatmentBean;
import com.patient.ui.patientUi.entity.extendsTable.DiseaseBean;
import com.patient.util.CommonUtil;
import com.patient.util.DateUtil;
import com.patient.util.LogUtil;
import com.patient.util.NetWorkUtil;
import com.yxck.patient.R;

/**
 * 生命线-就诊-添加就诊 - 确认就诊数据
 * <dl>
 * <dt>ActPatientInterrogation.java</dt>
 * <dd>Description:添加就诊</dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company:</dd>
 * <dd>CreateDate: 2014-12-26 下午3:27:19</dd>
 * </dl>
 * 
 * @author  
 */
@SuppressLint("HandlerLeak")
public class ActCommitVisit extends BaseActivity implements OnClickListener {

	private static final String TAG = ActCommitVisit.class.getName();

	private String doctorId = "";
	private TextView tvDiase;

	private EditText diaseDescription;
	private EditText etCardNumber;

	// 基本数据的图片信息
	private GridView basicGv;
	private BasicImageAdapter basicImageAdapter;
	private List<String> basicPhotos = null;

	private GridView checkGv;
	private CheckImageAdapter checkImageAdapter;
	private List<String> checkPhotos = null;

	private GridView cureGv;
	private CureImageAdapter cureImageAdapter;
	private List<String> curePhotos = null;

	private AnimationDrawable vodiceRightAnimation;// 播放声音动画
	private static boolean playState = false; // 播放状态
	private MediaPlayer mediaPlayer;
	private String date = "";
	private String path;// 录音地址
	public static boolean isCheck = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_commit_visit);

		date = new SimpleDateFormat("yy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());

		doctorId = getIntent().getStringExtra(CommonConstant.KEY_RESLUT);
		// 如果医生的ID 不为空进行添加这个医生进行就诊
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("添加就诊", R.color.white);
		bar.enableRightBtn("确认", 0, new OnClickListener() {

			@Override
			public void onClick(View v) {
				uploadPic();
			}
		});
		
		init();
	}

	private void init() {
		// 有照片显示GridView，否则隐藏，默认先隐藏，onresume方法中显示
		findViewById(R.id.ll_basic).setVisibility(View.GONE);
		findViewById(R.id.ll_check).setVisibility(View.GONE);
		findViewById(R.id.ll_cure).setVisibility(View.GONE);
		findViewById(R.id.ll_record).setVisibility(View.GONE);

		findViewById(R.id.ll_record_playing).setOnClickListener(this);// 播放录音
		findViewById(R.id.ll_recording).setOnClickListener(this);// 录音
		findViewById(R.id.ll_basic_photo).setOnClickListener(this);// 基本信息拍照
		findViewById(R.id.ll_check_photo).setOnClickListener(this);// 检查信息拍照
		findViewById(R.id.ll_cure_photo).setOnClickListener(this);// 治疗信息拍照

		etCardNumber = (EditText) findViewById(R.id.et_card_number);// 就诊卡号
		treamentTime = (TextView) findViewById(R.id.treamentTime);///时间
		diaseDescription = (EditText) findViewById(R.id.tv_description);
		
		findViewById(R.id.treamentTime).setOnClickListener(this);
		findViewById(R.id.rl_diase).setOnClickListener(this);
		tvDiase = (TextView) findViewById(R.id.tv_diase);
		// 基本数据的图片信息
		basicGv = (GridView) findViewById(R.id.basicInfoGv);
		checkGv = (GridView) findViewById(R.id.checkGv);
		cureGv = (GridView) findViewById(R.id.cureGv);

		basicImageAdapter = new BasicImageAdapter(this, basicGv, true);// true代表是这个界面过去的
		checkImageAdapter = new CheckImageAdapter(this, checkGv, true);
		cureImageAdapter = new CureImageAdapter(this, cureGv, true);

		basicGv.post(new Runnable() {

			@Override
			public void run() {

				basicImageAdapter.setData(basicPhotos);

			}
		});
		checkGv.post(new Runnable() {

			@Override
			public void run() {

				checkImageAdapter.setData(checkPhotos);
			}
		});
		cureGv.post(new Runnable() {

			@Override
			public void run() {

				cureImageAdapter.setData(curePhotos);
			}
		});

		basicGv.setAdapter(basicImageAdapter);
		checkGv.setAdapter(checkImageAdapter);
		cureGv.setAdapter(cureImageAdapter);
		basicImageAdapter.setRefreshBasicInfo(new RefreshBasicInfo() {

			@Override
			public void doDel(String url) {
				if (basicPhotos == null) {
					basicPhotos = new ArrayList<String>();
				}
				basicPhotos.remove(url);
				basicImageAdapter.setData(basicPhotos);
			}
		});
		checkImageAdapter.setRefreshCheckInfo(new RefreshCheckInfo() {

			@Override
			public void doDel(String url) {

				if (checkPhotos == null) {
					checkPhotos = new ArrayList<String>();
				}
				checkPhotos.remove(url);
				checkImageAdapter.setData(checkPhotos);
			}
		});
		cureImageAdapter.setRefreshCureInfo(new RefreshCureInfo() {

			@Override
			public void doDel(String url) {
				if (curePhotos == null) {
					curePhotos = new ArrayList<String>();
				}
				curePhotos.remove(url);
				cureImageAdapter.setData(curePhotos);
			}
		});
	}

	// 先上传图片然后在把服务端的图片地址和其绑定
	private String basicInfoUrl;
	private String cureInfoTUrl;
	private String checkInfoUrl;
	private String soundUrl;

	private void uploadPic() {

		if (TextUtils.isEmpty(tvDiase.getText().toString())) {
			CommonUtil.showToast("请输入诊断疾病的名称");
			return;
		}
		if (TextUtils.isEmpty(diaseDescription.getText().toString())) {
			CommonUtil.showToast("请输入简单症状表现");
			return;
		}
		if (TextUtils.isEmpty(treamentTime.getText().toString())) {
			CommonUtil.showToast("请上传就诊时间");
			return;
		}
		if (basicPhotos == null || basicPhotos.size() == 0) {
			CommonUtil.showToast("请上传基本信息图片");
			return;
		}
		if (checkPhotos == null || checkPhotos.size() == 0) {
			CommonUtil.showToast("请上传检查单");
			return;
		}
		if (curePhotos == null || curePhotos.size() == 0) {
			CommonUtil.showToast("请上传治疗单据");
			return;
		}
		if (TextUtils.isEmpty(path)) {
			CommonUtil.showToast("请上传就诊录音");
			return;
		}
		int netType = NetWorkUtil.checkNetworkType(ActCommitVisit.this);
		String internet = "";
		if (netType == NetWorkUtil.TYPE_WIFI) {
			// 直接上传
			wdg = new CommonWaitDialog(ActCommitVisit.this, "正在上传基本信息");
			asyTaskImage(UPLOA_BASIC);
		} else {
			if (netType == NetWorkUtil.TYPE_DX_3G) {
				internet = "3G";
			} else if (netType == NetWorkUtil.TYPE_DX_2G) {
				internet = "2G";
			} else {
				internet = "其它网络";
			}
			double isBig = 0d;
			for (String item : basicPhotos) {
				isBig += CommonUtil.getFileSize(item);
			}
			for (String item : basicPhotos) {
				isBig += CommonUtil.getFileSize(item);
			}
			for (String item : basicPhotos) {
				isBig += CommonUtil.getFileSize(item);
			}
			String big = CommonUtil.getFileSizeString(isBig);
			CommonDialog dg = new CommonDialog(this);
			dg.setTitle(null);
			dg.setMessage("您当前使用的网络是" + internet + "," + "当前图片的流量是" + big
					+ "您确定要上传吗？\n土豪随意哟。");
			dg.setCancleButton(null, "取消");
			dg.setPositiveButton(new BtnClickedListener() {

				@Override
				public void onBtnClicked() {
					wdg = new CommonWaitDialog(ActCommitVisit.this, "正在上传基本信息");
					asyTaskImage(UPLOA_BASIC);
				}
			}, "上传");
			dg.showDialog();
		}
	}

	private void asyTaskImage(final int type){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				switch (type) {
				case UPLOA_BASIC://基本信息
					basicPhotos=DownImageUtil.listSave(basicPhotos);
					handler.sendEmptyMessage(type);
					break;
				case UPLOA_CHECK://检查信息
					checkPhotos=DownImageUtil.listSave(checkPhotos);
					handler.sendEmptyMessage(type);
					break;
				case UPLOA_CURE://治疗信息
					curePhotos=DownImageUtil.listSave(curePhotos);
					handler.sendEmptyMessage(type);
					break;
				}
			}
		}).start();
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPLOA_BASIC:
				uploadHead(UPLOA_BASIC);
				break;
			case UPLOA_CHECK://检查信息
				uploadHead(UPLOA_CHECK);
				break;
			case UPLOA_CURE://治疗信息
				uploadHead(UPLOA_CURE);
				break;
			}
		}
	};
	
	private void createTreatment() {

		final CommonWaitDialog dg = new CommonWaitDialog(this, "上传就诊信息，请稍等");
		RequestParams params3 = new RequestParams();
		
		params3.addQueryStringParameter("patientId",LoginPreference.getUserInfo().partyId);//
		params3.addQueryStringParameter("doctorId", doctorId);//
		params3.addQueryStringParameter("treatmentTime", treamentTime.getText().toString());// 就诊时间
		params3.addQueryStringParameter("patientDisease", bean.index_Name);// 诊断（疾病名称）
		params3.addQueryStringParameter("report", diaseDescription.getText().toString());// 主诉（症状）

		params3.addQueryStringParameter("diseaseId", bean.disease_Id);// 疾病ID
		params3.addQueryStringParameter("basicImage", basicInfoUrl);// 基本信息图片路径
		params3.addQueryStringParameter("examineImages", checkInfoUrl);// 检查图片路径
		params3.addQueryStringParameter("cureImages", cureInfoTUrl);// 治疗图片路径
		if (!TextUtils.isEmpty(soundUrl)) {
			params3.addQueryStringParameter("soundRecorder", soundUrl);// 录音文件
		}

		if (!TextUtils.isEmpty(etCardNumber.getText().toString())) {
			params3.addQueryStringParameter("treatmentCardId", etCardNumber.getText().toString());// 就诊卡号
		}

		HttpUtils http3 = new HttpUtils();
		http3.configTimeout(15 * 1000);
		http3.send(HttpMethod.POST, UrlConstant.CREATE_TREATMENT, params3,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {

						dg.clearAnimation();
						if (result != null) {
							LogUtil.d2File(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String code = json.optString("responseMessage");
								//组装一个实体传值用
								final TreatmentBean treatmentBean = new TreatmentBean();
								treatmentBean.diseaseId = bean.disease_Id;
								treatmentBean.treatmentId = json.optString("treatmentId");
										
								if (code.equals(CommonConstant.STATUS_SUCCESS)) {
									CommonUtil.showToast("创建就诊成功");
									finish();
								} else {
									CommonUtil.showToast("创建就诊失败");
								}
							} catch (JSONException e) {

							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						dg.clearAnimation();
						CommonUtil.showToast("创建就诊失败");
					}
				});
	}

	private static final int UPLOA_BASIC = 1;
	private static final int UPLOA_CHECK = 2;
	private static final int UPLOA_CURE = 3;
	private static final int UPLOA_RECORD = 4;
	private CommonWaitDialog wdg;
	private void uploadHead(final int type) {

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("flag", "dtsc");
		switch (type) {
		case UPLOA_BASIC:
			for (int i = 0; i < basicPhotos.size(); i++) {
				params.addBodyParameter("image" + i, new File(basicPhotos.get(i)));
			}
			break;
		case UPLOA_CHECK:
			for (int i = 0; i < checkPhotos.size(); i++) {
				params.addBodyParameter("image" + i, new File(checkPhotos.get(i)));
			}
			break;
		case UPLOA_CURE:
			for (int i = 0; i < curePhotos.size(); i++) {
				params.addBodyParameter("image" + i, new File(curePhotos.get(i)));
			}
			break;
		case UPLOA_RECORD:
			params.addBodyParameter("soundRecorder", new File(path));// 录音文件
			break;
		default:
			break;
		}
	
		HttpUtils http = new HttpUtils();
		http.configTimeout(60 * 1000);
		http.send(HttpMethod.POST, UrlConstant.UPLOAD_ICON, params,
				new RequestCallBack<Object>() {

					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(Object result) {
						if (result != null) {
							wdg.clearAnimation();
							LogUtil.d2File(result.toString());
							LogUtil.d(result.toString());
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								String headurl = json.optString("outputList");
								int code = json.optInt("status");
								if (code == CommonConstant.SUCCESS) {
									switch (type) {
									case UPLOA_BASIC:
										basicInfoUrl = headurl;
										wdg = new CommonWaitDialog(ActCommitVisit.this, "正在上传检查信息");
										asyTaskImage(UPLOA_CHECK);
										break;
									case UPLOA_CHECK:
										checkInfoUrl = headurl;
										wdg = new CommonWaitDialog(ActCommitVisit.this, "正在上传治疗信息");
										asyTaskImage(UPLOA_CURE);
										break;
									case UPLOA_CURE:
										cureInfoTUrl = headurl;
										wdg = new CommonWaitDialog(ActCommitVisit.this, "正在上传录音信息");
										uploadHead(UPLOA_RECORD);
										DownImageUtil.delete();
										break;
									case UPLOA_RECORD:
										soundUrl = headurl;
										createTreatment();
										break;
									default:
										break;
									}
								} else {
									switch (type) {
									case UPLOA_BASIC:
										CommonUtil.showToast("上传基本信息失败");
										break;
									case UPLOA_CHECK:
										CommonUtil.showToast("上传检查信息失败");
										break;
									case UPLOA_CURE:
										CommonUtil.showToast("上传治疗信息失败");
										break;
									case UPLOA_RECORD:
										CommonUtil.showToast("上传录音信息失败");
										break;
									default:
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
						wdg.clearAnimation();
						LogUtil.d2File(msg);
						switch (type) {
						case UPLOA_BASIC:
							CommonUtil.showToast("上传基本信息失败");
							break;
						case UPLOA_CHECK:
							CommonUtil.showToast("上传检查信息失败");
							break;
						case UPLOA_CURE:
							CommonUtil.showToast("上传治疗信息失败");
							break;
						case UPLOA_RECORD:
							CommonUtil.showToast("上传录音信息失败");
							break;
						default:
							break;
						}
					}
				});
	}

	private TextView treamentTime;//时间
	private int year;
	private int monthOfYear;
	private int dayOfMonth;

	private void poptreamentTime() {

		String value = treamentTime.getText().toString();
		// 2014-12-11
		if (!TextUtils.isEmpty(value)) {
			year = Integer.parseInt(value.substring(0, 4));
			monthOfYear = Integer.parseInt(value.substring(5, 7));
			dayOfMonth = Integer.parseInt(value.substring(8, 10));
		} else {
			String date = DateUtil
					.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD);
			year = Integer.parseInt(date.substring(0, 4));
			monthOfYear = Integer.parseInt(date.substring(5, 7));
			dayOfMonth = Integer.parseInt(date.substring(8, 10));
		}
		new DatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				StringBuffer sb = new StringBuffer();
				sb.append(year)
						.append("-")
						.append(monthOfYear < 9 ? ("0" + (monthOfYear + 1))
								: (monthOfYear + 1))
						.append("-")
						.append(dayOfMonth < 10 ? ("0" + (dayOfMonth))
								: (dayOfMonth));
				// 判断时间是否大于当天的时间
				Date date1 = DateUtil.formatString2Date(sb.toString(),
						DateUtil.FORMAT_YYYY_MM_DD);
				Date date2 = DateUtil.formatString2Date(
						DateUtil.getCurrentTimeSpecifyFormat(DateUtil.FORMAT_YYYY_MM_DD),
						DateUtil.FORMAT_YYYY_MM_DD);
				if (date1.getTime() > date2.getTime()) {
					CommonUtil.showToast("就诊时间应该小于等于当天时间");
					isCheck = true;
					return;
				}
				isCheck = true;
				treamentTime.setText(sb.toString());
			}
		}, year, monthOfYear - 1, dayOfMonth).show();
	}

	/** 播放声音的方法 */
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
							findViewById(R.id.paly_voice_btn)
									.setBackgroundResource(
											R.anim.record_voice_animation);
							findViewById(R.id.ll_record_playing).setEnabled(
									true);// 播放完毕才可点击
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

	private Thread recordThread;
	private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 3; // 最短录制时间，单位秒，0为无时间限制，建议设为3
	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音
	private static int RECODE_STATE = 0; // 录音的状态
	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private AudioRecorder mr;
	private ImageView dialog_img;

	// 录音计时线程
	private void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音线程
	private Runnable ImgThread = new Runnable() {
		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					// 录音超过60秒自动停止
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (dialog1.isShowing()) {
							dialog1.dismiss();
						}
						try {
							mr.stop();
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (recodeTime < 1.0) {
							// showWarnToast();
							RECODE_STATE = RECORD_NO;
						}
					}
					break;
				case 1:
					setDialogImage();
					break;
				case 3:
					break;
				default:
					break;
				}
			}
		};
	};

	// 录音Dialog图片随声音大小切换
	private void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	// 录音dialog
	CommonDialog dialog1;
	View view;

	private void showDialog() {
		view = LayoutInflater.from(ActCommitVisit.this).inflate(R.layout.my_dialog1, null);
		dialog_img = (ImageView) view.findViewById(R.id.dialog_img);
		RECODE_STATE = RECORD_ING;
		dialog1 = new CommonDialog(ActCommitVisit.this);
		dialog1.setTitle("正在录音中");
		dialog1.addView(view);
		dialog1.setCancleButton(new BtnClickedListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onBtnClicked() {
				if (dialog1.isShowing()) {
					dialog1.dismiss();
					recordThread.destroy();// dialog消失将线程销毁
				}
				isCheck = true;
			}
		}, "取消");
		dialog1.setPositiveButton(new BtnClickedListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onBtnClicked() {
				if (RECODE_STATE == RECORD_ING) {
					RECODE_STATE = RECODE_ED;
					if (dialog1.isShowing()) {
						dialog1.dismiss();
						recordThread.destroy();// dialog消失将线程销毁
					}
					try {
						mr.stop();
						voiceValue = 0.0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (recodeTime < MIX_TIME) {
						CommonUtil.showToast("时间太短   录音失败");
						RECODE_STATE = RECORD_NO;
						if (!TextUtils.isEmpty(path)) {
							File file = new File(path);
							if (file.isFile()) {
								file.delete();
							}
						}
					} else {
						path = getRecordFileName();
						CommonUtil.showToast("录音完成可以播放重听");
						if (!TextUtils.isEmpty(path)) {
							findViewById(R.id.ll_record).setVisibility(View.VISIBLE);
						}
					}
				}
				isCheck = true;
			}
		}, "完成");

		mr = new AudioRecorder(date);
		try {
			mr.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mythread();
		dialog1.showDialog();
	}

	// 录音保存的地址
	private String getRecordFileName() {
		File sampleDir = new File(Environment.getExternalStorageDirectory(),
				"my/" + date + ".amr");
		if (!sampleDir.exists()) {
			try {
				sampleDir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sampleDir.getAbsolutePath();
	}

	private DiseaseBean bean = null;

//	private void popDiease() {
//		String dieaseId = "";
//		if (bean != null) {
//			dieaseId = bean.disease_Id;
//		}
//		PopSelectDiease pop = new PopSelectDiease(this, allData, dieaseId);
//		pop.setDieaseListener(new PopDieaseListener() {
//
//			@Override
//			public void doRefresh(DiseaseBean bean1) {
//				bean = bean1;
//				tvDiase.setText(bean.index_Name);
//			}
//		});
//		pop.show();
//	}

//	private boolean getDataFromDb() {
//		allData = new DiseaseDaoImpl(this).getAllDisease("all");
//		if (allData != null && allData.size() > 0) {
//			return true;
//		}
//		return false;
//	}
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		isCheck = true;
		if (resultCode == RESULT_OK) {
			if (requestCode == PopPicDialog.REQUEST_CODE_ADD && data != null
					&& data.getExtras() != null) {
				refreshBlum(data);
			} else if (requestCode == PopPicDialog.ACTION_SHARE_FROM_CAMERA) {
				refreshCameraPic();
			} else if (requestCode == CommonConstant.SUCCESS) {
				bean = (DiseaseBean) data.getSerializableExtra(CommonConstant.KEY_RESLUT);
				tvDiase.setText(bean.index_Name);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshCameraPic() {

		if (!TextUtils.isEmpty(PopPicDialog.cameraFilePath)) {
			CommonUtil.scanFileAsync(this, PopPicDialog.cameraFilePath);
			LogUtil.d(TAG, "拍照返回的图片路径：" + PopPicDialog.cameraFilePath);
		} else {
			CommonUtil.showToast("解析数据出错", this);
		}
		int choseType = PopPicDialog.chose_type;
		switch (choseType) {
		case PopPicDialog.BASIC_INFO:
			if (basicPhotos == null) {
				basicPhotos = new ArrayList<String>();
			}
			if (!basicPhotos.contains(PopPicDialog.cameraFilePath)) {
				basicPhotos.add(PopPicDialog.cameraFilePath);
			}
			basicImageAdapter.setData(basicPhotos);
			break;
		case PopPicDialog.CURE_INFO:
			if (curePhotos == null) {
				curePhotos = new ArrayList<String>();
			}
			if (!curePhotos.contains(PopPicDialog.cameraFilePath)) {
				curePhotos.add(PopPicDialog.cameraFilePath);
			}
			cureImageAdapter.setData(curePhotos);
			break;
		case PopPicDialog.CHECK_INFO:
			if (checkPhotos == null) {
				checkPhotos = new ArrayList<String>();
			}
			if (!checkPhotos.contains(PopPicDialog.cameraFilePath)) {
				checkPhotos.add(PopPicDialog.cameraFilePath);
			}
			checkImageAdapter.setData(checkPhotos);
			break;
		default:
			break;
		}
	}

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
		bundle.putBoolean(ActCommitVisit.KEY_ACTION_CUSTOM, true);
		bundle.putInt(ActCommitVisit.KEY_FOR_SHARE_TYPE, SHARE_TYPE_PIC);
		bundle.putString(PreviewActivity.ACTIVITY_FLAG,PreviewActivity.AVITVITY_START_FOR_RESULT);
		bundle.putInt(PreviewActivity.SELECTED_IMG_INDEX, position);
		Intent intent = new Intent(ActCommitVisit.this, PreviewActivity.class);
		intent.putExtras(bundle);
		ActCommitVisit.this.startActivityForResult(intent, REQUEST_CODE_PREVIEW);
	}

	private void refreshBlum(Intent data) {

		// 增加照片从相册里面
		ArrayList<String> addedPathList = data.getExtras().getStringArrayList(Intent.EXTRA_STREAM);
		int choseType = PopPicDialog.chose_type;
		switch (choseType) {
		case PopPicDialog.BASIC_INFO:
			if (addedPathList != null && addedPathList.size() > 0) {
				if (basicPhotos == null) {
					basicPhotos = new ArrayList<String>();
				}
				int repeatCnt = 0;
				for (int i = 0; i < addedPathList.size(); i++) {
					String addedPath = addedPathList.get(i);
					if (!basicPhotos.contains(addedPath)) {
						basicPhotos.add(addedPath);
					} else {
						repeatCnt++;
					}
				}
				if (repeatCnt > 0) {
					Toast.makeText(ActCommitVisit.this,
							"您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片",
							Toast.LENGTH_SHORT).show();
				}
				basicImageAdapter.setData(basicPhotos);
			}

			break;
		case PopPicDialog.CURE_INFO:
			if (addedPathList != null && addedPathList.size() > 0) {
				if (curePhotos == null) {
					curePhotos = new ArrayList<String>();
				}
				int repeatCnt = 0;
				for (int i = 0; i < addedPathList.size(); i++) {
					String addedPath = addedPathList.get(i);
					if (!curePhotos.contains(addedPath)) {
						curePhotos.add(addedPath);
					} else {
						repeatCnt++;
					}
				}
				if (repeatCnt > 0) {
					Toast.makeText(ActCommitVisit.this,
							"您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片",
							Toast.LENGTH_SHORT).show();
				}
				cureImageAdapter.setData(curePhotos);
			}
			break;
		case PopPicDialog.CHECK_INFO:
			if (addedPathList != null && addedPathList.size() > 0) {
				if (checkPhotos == null) {
					checkPhotos = new ArrayList<String>();
				}
				int repeatCnt = 0;
				for (int i = 0; i < addedPathList.size(); i++) {
					String addedPath = addedPathList.get(i);
					if (!checkPhotos.contains(addedPath)) {
						checkPhotos.add(addedPath);
					} else {
						repeatCnt++;
					}
				}
				if (repeatCnt > 0) {
					Toast.makeText(ActCommitVisit.this,
							"您选的照片有" + repeatCnt + "张重复了，已自动为您移除重复的照片",
							Toast.LENGTH_SHORT).show();
				}
				checkImageAdapter.setData(checkPhotos);
			}
			break;
		default:
			break;
		}
	}

	PopPicDialog dg;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_diase://接口取数据
			if (isCheck) {
				isCheck = false;
				Intent i = new Intent(ActCommitVisit.this, ActChoseDiease.class);
				startActivityForResult(i, 1);
			}
			break;
		case R.id.treamentTime:// 时间
			if (isCheck) {
				isCheck = false;
				poptreamentTime();
			}
			break;
		case R.id.ll_recording:// 录音
			if (isCheck) {
				isCheck = false;
				showDialog();
			}
			break;
		case R.id.ll_basic_photo:// 基本信息拍照
			if (basicPhotos == null) {
				basicPhotos = new ArrayList<String>();
			}else if (basicPhotos.size() >= 9) {// 有照片显示GridView，否则隐藏
				CommonUtil.showToast("只能上传9张图片");
				return;
			} 
			dg = new PopPicDialog(ActCommitVisit.this, PopPicDialog.BASIC_INFO,basicPhotos.size());
			dg.showDg();
			break;
		case R.id.ll_check_photo:// 检查信息拍照
			if (checkPhotos == null) {
				checkPhotos = new ArrayList<String>();
			}else if (checkPhotos != null && checkPhotos.size() >= 9) {// 有照片显示GridView，否则隐藏
				CommonUtil.showToast("只能上传9张图片");
				return;
			}
			dg = new PopPicDialog(ActCommitVisit.this, PopPicDialog.CHECK_INFO,checkPhotos.size());
			dg.showDg();
			break;
		case R.id.ll_cure_photo:// 治疗信息拍照
			if (curePhotos == null) {
				curePhotos = new ArrayList<String>();
			}else if (curePhotos.size() >= 9) {// 有照片显示GridView，否则隐藏
				CommonUtil.showToast("只能上传9张图片");
				return;
			}
			dg = new PopPicDialog(ActCommitVisit.this, PopPicDialog.CURE_INFO,curePhotos.size());
			dg.showDg();
			break;
		case R.id.ll_record_playing:// 播放录音
			findViewById(R.id.ll_record_playing).setEnabled(false);// 点击一次不可点击，播放完毕之后...
			vodiceRightAnimation = (AnimationDrawable) findViewById(R.id.paly_voice_btn).getBackground();
			vodiceRightAnimation.start();
			playVodic(path);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d(TAG, "-------------onResume");
		if (basicPhotos != null && basicPhotos.size() > 0) {// 有照片显示GridView，否则隐藏
			findViewById(R.id.ll_basic).setVisibility(View.VISIBLE);
		}
		if (checkPhotos != null && checkPhotos.size() > 0) {
			findViewById(R.id.ll_check).setVisibility(View.VISIBLE);
		}
		if (curePhotos != null && curePhotos.size() > 0) {
			findViewById(R.id.ll_cure).setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(path)) {
			findViewById(R.id.ll_record).setVisibility(View.VISIBLE);
		}
		treamentTime.setText(DateUtil.getCurrentTimeSpecifyFormat("yyyy-MM-dd"));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCheck = true;
	}
}
