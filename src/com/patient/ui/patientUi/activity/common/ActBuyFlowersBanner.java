package com.patient.ui.patientUi.activity.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

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
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

/**
 * 购买鲜花和锦旗的公用界面
 */
public class ActBuyFlowersBanner extends BaseActivity {
	
	private TitleBar bar;
	private EditText etNum = null;//购买数量
	private TextView tvAmount = null;//应付金额
	private PartyBean bean;
	private boolean isFlowers = false;//true为购买鲜花，false为购买锦旗
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_buy_flowers_banner);
		
		isFlowers = getIntent().getExtras().getBoolean(CommonConstant.KEY_RESLUT, false);
		bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		if (isFlowers) {
			bar.setTitle("购买鲜花",R.color.white);
			((TextView)findViewById(R.id.tv_unit)).setText("朵");
		}else {
			bar.setTitle("购买锦旗",R.color.white);
			((TextView)findViewById(R.id.tv_unit)).setText("面");
		}
		bar.enableRightBtn("充值", -1, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ActBuyFlowersBanner.this,ActAlipay.class));				
			}
		});
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (TextUtils.isEmpty(bean.academicCoin)) {
			bean.academicCoin = "0";
		}
		((TextView) findViewById(R.id.tv_balance)).setText("您的钱包余额：" + bean.academicCoin + "元");// 余额
		if (isFlowers) {
			((TextView) findViewById(R.id.tv_price)).setText("单价：1元/朵");// 单价
		}else {
			((TextView) findViewById(R.id.tv_price)).setText("单价：1元/面");// 单价
		}
	}
	private void init() {
		bean = LoginPreference.getUserInfo();
		etNum = (EditText) findViewById(R.id.et_num);// 购买数量
		tvAmount = (TextView) findViewById(R.id.tv_amount);// 应付金额
		etNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(etNum.getText().toString())) {
					int integer = Integer.valueOf(etNum.getText().toString());
					tvAmount.setText(integer * 1 + "元");// 购买鲜花锦旗
				} else {
					tvAmount.setText("0元");
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		findViewById(R.id.tv_submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Integer.valueOf(bean.academicCoin) < Integer.valueOf(etNum.getText().toString()) && !"0".equals(etNum.getText().toString())) {
					CommonUtil.showToast("请确认钱包余额可以完成此次交易");
					return;
				}
				CommonDialog dialog = new CommonDialog(ActBuyFlowersBanner.this);
				dialog.setTitle("提示");
				dialog.setMessage("本次交易将扣除您钱包" + Integer.valueOf(etNum.getText().toString()) + "元，是否确认");
				dialog.setCancleButton(null, "取消");
				dialog.setPositiveButton(new BtnClickedListener() {
					
					@Override
					public void onBtnClicked() {
						buyFlowersBanner();//购买接口
					}
				}, "确认");
				dialog.showDialog();
			}
		});
	}
	
	// 购买鲜花和锦旗
	private void buyFlowersBanner() {
		final CommonWaitDialog dg = new CommonWaitDialog(this, "购买中，请稍等");
		RequestParams params = new RequestParams();
		
		params.addQueryStringParameter("partyId",LoginPreference.getUserInfo().partyId);//
		params.addQueryStringParameter("academicCoin", etNum.getText().toString());//购买数量
		if (isFlowers) {
			params.addQueryStringParameter("businessType", "businessType_1");//购买类型businessType_1鲜花businessType_2锦旗
		}else {
			params.addQueryStringParameter("businessType", "businessType_2");//购买类型businessType_1鲜花businessType_2锦旗
		}


		HttpUtils http3 = new HttpUtils();
		http3.configTimeout(15 * 1000);
		http3.send(HttpMethod.POST, UrlConstant.PURCHASE_CITIGROUP, params, new RequestCallBack<Object>() {

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
								if (code.equals(CommonConstant.STATUS_SUCCESS)) {
									CommonUtil.showToast("购买成功");
									//将缓存中的金币扣掉，增加相应的鲜花数或者锦旗数
									bean.academicCoin = String.valueOf(Integer.valueOf(bean.academicCoin) - Integer.valueOf(etNum.getText().toString()));
									if (isFlowers) {
										if (TextUtils.isEmpty(bean.flowersNum) || bean.flowersNum.equals("0")) {
											bean.flowersNum = etNum.getText().toString();
										}else {//如果有鲜花余额，将原有的鲜花数和充值的鲜花数相加并更新缓存数据
											bean.flowersNum =String.valueOf(Integer.valueOf(bean.flowersNum) + Integer.valueOf(etNum.getText().toString()))  ;
										}
									}else {
										if (TextUtils.isEmpty(bean.bannerNum) || bean.bannerNum.equals("0")) {
											bean.bannerNum = etNum.getText().toString();
										}else {//如果有锦旗余额，将原有的锦旗数和充值的锦旗数相加并更新缓存数据
											bean.bannerNum =String.valueOf(Integer.valueOf(bean.bannerNum) + Integer.valueOf(etNum.getText().toString()))  ;
										}
									}
									finish();
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
}
