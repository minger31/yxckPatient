package com.patient.common.iflytek;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.EditText;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MutationUtil {
	static EditText editTexts;
	@SuppressWarnings("static-access")
	public MutationUtil (EditText editText){
		this.editTexts = editText;
	}
	// 用HashMap存储听写结果
	private static HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	
	int ret = 0; // 函数调用返回值
	/** 初始化监听器 */
	public static InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
//				showTip("初始化失败，错误码：" + code);
			}
		}
	};
	
	private static void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		asd = resultBuffer.toString().substring(0, resultBuffer.toString().length() - 1);
		editTexts.setText(asd);
		editTexts.setSelection(editTexts.length());
	}
	static String asd ; 

	/** 听写UI监听器 */
	public static RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/** 识别回调错误 */
		public void onError(SpeechError error) {
//			showTip(error.getPlainDescription(true));
		}
	};
}
