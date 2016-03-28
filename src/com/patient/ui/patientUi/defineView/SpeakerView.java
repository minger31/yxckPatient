package com.patient.ui.patientUi.defineView;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.patient.constant.CommonConstant;
import com.patient.library.http.HttpRequest.HttpMethod;
import com.patient.library.http.HttpUtils;
import com.patient.library.http.RequestCallBack;
import com.patient.library.http.RequestParams;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

// 联合发起人说两句话
public class SpeakerView  {
//
//    private Activity mActivity = null;
//
//    private Dialog mDialog = null;
//
//    private View contentView = null;
//	
//    private RecordService rs;
//    
//    private MediaPlayService mp;
//    
//    
//    public SpeakerView(Activity activity) {
//    	
//    	rs = RecordService.instance();
//    	mp = MediaPlayService.instance();
//    	
//        mActivity = activity;
//        mDialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
//        setContentView(R.layout.speaker_layout);
//        
//    }
//
//    public void setContentView(int res) {
//    	if(null == mDialog){
//    		return;
//    	}
//        contentView = mActivity.getLayoutInflater().inflate(res, null);
//        mDialog.setContentView(contentView, new LinearLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        Window window = mDialog.getWindow();
//        // 设置显示动画
//        window.setWindowAnimations(R.style.bottom_menu_windown_animstyle);
//
//        // 设置窗口大小和位置
//        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.x = 0;
//        wl.y = CommonUtil.getDeviceSize(mActivity).y;
//        wl.width = CommonUtil.getDeviceSize(mActivity).x;
//        mDialog.onWindowAttributesChanged(wl);
//        // 点击窗口以外区域，关闭窗口
//        mDialog.setCanceledOnTouchOutside(true);
//        
//        initUI();
//    }
//
//    public View getContentView() {
//        return contentView;
//    }
//
//    public void show() {
//    	if(mDialog!=null){
//           mDialog.show();
//    }
//    }
//
//    public void dismiss() {
//    	if(mDialog!=null){
//    		rs.setRecordFile(null);
//    		mp.releaseMediaPlayer();
//    		handler.removeMessages(START);
//    		handler.removeMessages(END);
//    		handler = null;
//            mDialog.dismiss();
//       }
//    }
//  
//    private Button cancle;
//    private Button send;
//    private LinearLayout lt;
//    private TextView recordsSeconds;
//    private TextView recordIcon;
//    
//    private void initUI(){
//     
//    	
//    	lt = (LinearLayout)contentView.findViewById(R.id.operation_layout);
//    	lt.setVisibility(View.INVISIBLE);
//    	
//    	cancle = (Button)contentView.findViewById(R.id.cancel_btn);
//    	cancle.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//    	
//    	send = (Button)contentView.findViewById(R.id.confirm_btn);
//    	send.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (TextUtils.isEmpty(rs.getRecordFile())) {
//					CommonUtil.showToast("录音文件为空", mActivity);
//					return;
//				}else{
//					uploadRecordFile(new File(rs.getRecordFile()));
//					dismiss();
//				}
//			}
//		});
//    	
//    	recordsSeconds = (TextView)contentView.findViewById(R.id.recordSeconds);
//    	recordIcon = (TextView)contentView.findViewById(R.id.recordPlayer);
//    	recordIcon.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				 // 控制录音和播放录音
//					if (isRecord) {
//						// 关闭动画
//						rs.stopRecordFile();
//						lt.setVisibility(View.VISIBLE);
//						handler.sendEmptyMessage(END);
//						if (!TextUtils.isEmpty(rs.getRecordFile())) {
////							CommonUtil.playAudio(mActivity, rs.getRecordFile());
//							mp.play(rs.getRecordFile());
//							return;
//						}
//					}else{
//						 // 启动动画
//						 handler.sendEmptyMessageDelayed(START, 1000);
//						 isRecord = true;
//						 rs.recordFile("15855131332");
//					}
//			}
//		});
//    }
//    
//    private boolean isRecord = false;
//    private long recordSecond  = 0;
//    private static final int START =1;
//    private static final int END =2;
//    
//    Handler handler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case START:
//				recordsSeconds.setText(getFormatMM_SS(recordSecond++));
//				handler.sendEmptyMessageDelayed(START, 1000);
//				break;
//			case END:
//				handler.removeMessages(START);
//				break;
//
//			default:
//				break;
//			}
//			 
//		}
//    };
//    private String getFormatMM_SS(long time){
//		String formateTime = "";
//		long minites = time/60;
//		long seconds = time%60;
//		if (minites < 10) {
//			formateTime ="0"+minites;
//		}else{
//			formateTime = minites+"";
//		}
//		formateTime = formateTime+":";
//		if (seconds < 10) {
//			formateTime +="0"+seconds;
//		}else{
//			formateTime += seconds+"";
//		}
//		return formateTime;
//	}
//
//    private MenuClickedListener listener;
//
//	public void setListener(MenuClickedListener listener) {
//		this.listener = listener;
//	}
//
//	public interface MenuClickedListener {
//        public void onMenuClicked(int pos);
//    }
//	
//	
//	private void uploadRecordFile(File recordFile) {
//
//		if (recordFile.exists() && recordFile.length() >= CommonConstant.RECORD_FILE_SIZE) {
//			CommonUtil.showToast("录音文件上传最大为6M", mActivity);
//			return;
//		}
//		
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("image",recordFile);
//		params.addQueryStringParameter("partyId", "10004");
//		params.addQueryStringParameter("flag", "fbhj");
//		
//		HttpUtils http = new HttpUtils();
//		
//		String url = "http://192.168.1.153:8080/yxck/control/ajaxImportPicUrl";
//		http.configTimeout(15 * 1000);
//		http.send(HttpMethod.POST, url, params,
//				new RequestCallBack<Object>() {
//
//					@Override
//					public void onStart() {
//
//					}
//
//					@Override
//					public void onSuccess(Object result) {
//						if (result != null) {
//
//							LogUtil.d2File(result.toString());
//							LogUtil.d(result.toString());
//							JSONObject json;
//							try {
//								json = new JSONObject(result.toString());
//								int code = json.optInt("status");
//								String headurl = json.optString("rows");
//								if (code == CommonConstant.SUCCESS) {
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
//					}
//				});
//	}
}
