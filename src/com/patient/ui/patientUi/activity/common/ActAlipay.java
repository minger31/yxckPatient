package com.patient.ui.patientUi.activity.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
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
import com.patient.preference.LoginPreference.LoginType;
import com.patient.ui.patientUi.activity.alipay.Result;
import com.patient.ui.patientUi.activity.alipay.SignUtils;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.CommonUtil;
import com.patient.util.LogUtil;
import com.yxck.patient.R;

public class ActAlipay extends BaseActivity {

	private EditText etRecharge = null;// 输入的充值数量
	private TextView tvRecharge = null;// 显示输入的充值数量
	private TextView tvSubmit = null; // 确定

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
//					CommonUtil.showToast("支付成功");
					// 当支付宝支付成功后，将会通知后台刷新将这条数据保存到后台，如果后台接口充值失败会保存到本地数据库，定时上传这个失败的任务
					notifyAcademicCoins();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CommonUtil.showToast("支付结果确认中");
					} else {
						CommonUtil.showToast("支付失败");
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				if ((Boolean) msg.obj) {
					CommonDialog dg = new CommonDialog(ActAlipay.this);
					dg.setTitle(null);
					dg.setMessage("本次支付将花费您 '"
							+ tvRecharge.getText().toString()
							+ "' 人民币，是否确定使用支付宝快捷支付购买？");
					dg.setCancleButton(null, "取消");
					dg.setPositiveButton(new BtnClickedListener() {

						@Override
						public void onBtnClicked() {
							pay();
						}
					}, "支付");
					dg.showDialog();
					// pay();
					// 终端设备是否支持
					// 这个方法就是支付方法，其实流程是如果你的手机有支付宝客户端，它会唤起支付宝客户端让你支付，如果没有，它会跳入到一个支付宝登陆的页面，然后再让你支付。
				} else {
					CommonUtil.showToast("您的设备不支持支付宝快捷支付，请更换终端设备");
				}
				break;
			}
			default:
				break;
			}
		};
	};

	private void notifyAcademicCoins() {
		// http://192.168.1.153:8080/yxck/control/ajaxAddAcaCoinHis?partyId=13260114000&money=100&json=Y
		final CommonWaitDialog wdg = new CommonWaitDialog(this, "更新后台数据");
		// 调登陆接口进行登录
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("partyId", LoginPreference.getKeyValue(LoginType.PARTY_ID, ""));
		params.addQueryStringParameter("money", etRecharge.getText().toString());
		HttpUtils http = new HttpUtils();
		http.configTimeout(15 * 1000);
		http.send(HttpMethod.POST, UrlConstant.ajaxAddAcaCoinHis, params, new RequestCallBack<Object>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onSuccess(Object result) {
						wdg.clearAnimation();
						LogUtil.d(result.toString());

						if (result != null) {
							JSONObject json;
							try {
								json = new JSONObject(result.toString());
								int code = json.optInt("status");
								if (code == CommonConstant.SUCCESS) {
									PartyBean bean = LoginPreference.getUserInfo();
									String coins2 = bean.academicCoin;
									if (!TextUtils.isEmpty(coins2) && !coins2.equals("0")) {
										//将原有金币与充值的金币加起来，然后转String并赋值后为新的金币数额
										int totalCoins2 = Integer.parseInt(coins2) + Integer.parseInt(etRecharge.getText().toString());
										bean.academicCoin = String.valueOf(totalCoins2);
									} 
									finish();
								}  
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(Throwable error, String msg) {
						LogUtil.d2File(msg);
						wdg.clearAnimation();
						CommonUtil.showToast("购买失败，如有疑问请联系客服");
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_buy_academic_currency);
		
		TitleBar bar = getTitleBar();
		bar.setBack("", null, R.drawable.ic_back);
		bar.setTitle("充值", R.color.white);
		init();
	}

	private void init() {
		etRecharge = (EditText) findViewById(R.id.et_recharge);// 输入的充值数量
		tvRecharge = (TextView) findViewById(R.id.tv_recharge);// 显示输入的充值数量
		tvSubmit = (TextView) findViewById(R.id.tv_submit); // 确定
		tvSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String count = etRecharge.getText().toString();
				if (!TextUtils.isEmpty(count) && !TextUtils.equals("0", count)) {
					// CommonUtil.showToast(tvRecharge.getText().toString());
					// 调用支付宝快捷支付
					CommonDialog dg = new CommonDialog(ActAlipay.this);
					dg.setTitle(null);
					dg.setMessage("本次支付将花费您 '"
							+ tvRecharge.getText().toString()
							+ "' 人民币，是否确定使用支付宝快捷支付购买？");
					dg.setCancleButton(null, "取消");
					dg.setPositiveButton(new BtnClickedListener() {

						@Override
						public void onBtnClicked() {
							pay();
						}
					}, "支付");
					dg.showDialog();
				} else {
					CommonUtil.showToast("请输入正确的购买数量");
				}
			}
		});

		etRecharge.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(etRecharge.getText().toString())) {
					int integer = Integer.valueOf(etRecharge.getText().toString());
					tvRecharge.setText(integer*1 + "元");//购买学术币
				} else {
					tvRecharge.setText("0元");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {

		int integer = Integer.valueOf(etRecharge.getText().toString());
 
		String orderInfo = getOrderInfo("学术币", "学术币描述", String.valueOf(integer*1));;//购买学术币
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {

				// 构造PayTask 对象
				PayTask alipay = new PayTask(ActAlipay.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 这个方法就是支付方法，其实流程是如果你的手机有支付宝客户端，它会唤起支付宝客户端让你支付，如果没有，它会跳入到一个支付宝登陆的页面
	 * ，然后再让你支付。
	 * 
	 */
	public void check() {

		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(ActAlipay.this);
				boolean isExist = payTask.checkAccountIfExist();
				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};
		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
		LogUtil.d("当前支付宝快捷支付的版本：" + version);
	}

	/**
	 * create the order info. 创建订单信息
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + CommonConstant.PARTNER + "\"";
		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + CommonConstant.SELLER + "\"";
		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";
		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		// http://192.168.1.153:8080/yxck/control/ajaxAddAcaCoinHis?partyId=13260114000&money=100&json=Y
		String url = UrlConstant.ajaxAddAcaCoinHis +"?partyId="+LoginPreference.getUserInfo().partyId
				     +"&money="+etRecharge.getText().toString()+"&json=Y";
		// 服务器异步通知页面路径
//		orderInfo += "&notify_url=" + "\"" + url + "\"";
		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";
 		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";
		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";
		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";
		LogUtil.d("orderInfo="+orderInfo);
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, CommonConstant.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
