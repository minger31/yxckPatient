package com.patient.ui.patientUi.activity.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.patient.constant.CommonConstant;
import com.patient.library.zxing.encoding.EncodingHandler;
import com.patient.preference.LoginPreference;
import com.patient.ui.patientUi.entity.baseTable.PartyBean;
import com.patient.util.BitmapManager;
import com.patient.util.LoaderImage;
import com.yxck.patient.R;

public class ActMyQr extends Activity {

	private PartyBean party;
	private ImageView myQr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.pop_my_qr);
		party = LoginPreference.getUserInfo();

		Display display = getWindowManager().getDefaultDisplay(); // 为获取屏幕宽、高
		Window window = getWindow();
		LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
		windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.6
		// dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
		windowLayoutParams.dimAmount = 0.4f;
//		if (getIntent() != null) {
//			int loaction[] = getIntent().getIntArrayExtra(CommonConstant.KEY_RESLUT);
//			windowLayoutParams.x = loaction[0];
//			windowLayoutParams.y = loaction[1];
//		}
		window.setAttributes(windowLayoutParams);

		init();
		
	}
	

	private void init() {
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true).cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.ARGB_8888).considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageView myHead = (ImageView) findViewById(R.id.ivMyPersonal);
		myHead.setImageResource(R.drawable.ic_heads);
		
		LoaderImage.getInstance(0).ImageLoaders(party.partyheadUrl, myHead);

		TextView name = (TextView) findViewById(R.id.name);
		name.setText(party.partyName);
		TextView address = (TextView) findViewById(R.id.address);
		address.setText(party.hospitalName);// 工作单位

		myQr = (ImageView) findViewById(R.id.ivMyQr);
		JSONObject item = new JSONObject();
		try {
			item.put("name", "yxck");
			item.put("patientId", party.partyId);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String contentString = item.toString();
	
		// TODO 医生添加扫一扫 直接跳转到患者的生命线，？
		final Bitmap qrCodeBitmap;
		try {
			qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
			// ------加载中间小头像
			if (!TextUtils.isEmpty(party.partyheadUrl)) {
				ImageLoader.getInstance().displayImage(party.partyheadUrl, myQr, options,new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap logoBmp) {
						logoBmp = BitmapManager.zoomImg(logoBmp, 50, 50);
						Bitmap bitmap = Bitmap.createBitmap(
								qrCodeBitmap.getWidth() - 20,
								qrCodeBitmap.getHeight() - 20,
								qrCodeBitmap.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
						canvas.drawBitmap(
								logoBmp,
								qrCodeBitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
								qrCodeBitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);
						myQr.setImageBitmap(bitmap);
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						super.onLoadingFailed(imageUri, view, failReason);
						Bitmap logoBmp = BitmapManager.zoomImg(BitmapFactory.decodeResource(getResources(),R.drawable.ic_heads), 50, 50);
						Bitmap bitmap = Bitmap.createBitmap(
								qrCodeBitmap.getWidth() - 20,
								qrCodeBitmap.getHeight() - 20,
								qrCodeBitmap.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
						canvas.drawBitmap(
								logoBmp,
								qrCodeBitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
								qrCodeBitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);
						myQr.setImageBitmap(bitmap);
					}
					
				});
			}else{
				Bitmap logoBmp = BitmapManager.zoomImg(BitmapFactory.decodeResource(getResources(),R.drawable.ic_heads), 50, 50);
				Bitmap bitmap = Bitmap.createBitmap(
						qrCodeBitmap.getWidth() - 20,
						qrCodeBitmap.getHeight() - 20,
						qrCodeBitmap.getConfig());
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
				canvas.drawBitmap(
						logoBmp,
						qrCodeBitmap.getWidth() / 2 - logoBmp.getWidth() / 2,
						qrCodeBitmap.getHeight() / 2 - logoBmp.getHeight() / 2, null);
				myQr.setImageBitmap(bitmap);
			}
			

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		Intent i = new Intent();
		i.putExtra(CommonConstant.KEY_RESLUT, false);
		setResult(Activity.RESULT_OK,i);
		finish();
//		if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
//			return true;
//		}
		return super.onTouchEvent(event);
	}

	private boolean isOutOfBounds(Activity context, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(context)
				.getScaledWindowTouchSlop();
		final View decorView = context.getWindow().getDecorView();
		return (x < -slop) || (y < -slop)
				|| (x > (decorView.getWidth() + slop))
				|| (y > (decorView.getHeight() + slop));
	}

}
