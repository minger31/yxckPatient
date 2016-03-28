//package com.patient.library.zxing.view;
//
//import android.R;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.zxing.WriterException;
//import com.patient.library.zxing.encoding.EncodingHandler;
//import com.patient.ui.patientUi.defineView.defineImgGallery.MultiBucketChooserActivity;
//
//public class BarCodeTestActivity extends Activity {
//    /** Called when the activity is first created. */
//	private TextView resultTextView;
//	private EditText qrStrEditText;
//	private ImageView qrImgImageView;
//	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//		final Resources r = getResources();
//		
//        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
//        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
//        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
//        
//        Button scanBarCodeButton = (Button) this.findViewById(R.id.btn_scan_barcode);
//        scanBarCodeButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				//鎵撳紑鎵弿鐣岄潰鎵弿鏉″舰鐮佹垨浜岀淮鐮�			
////				Intent openCameraIntent = new Intent(BarCodeTestActivity.this,CaptureActivity.class);
////				startActivityForResult(openCameraIntent, 0);
//				
//				   // 从手机选图
//                Intent i = new Intent(BarCodeTestActivity.this, MultiBucketChooserActivity.class);
//                i.putExtra(MultiBucketChooserActivity.KEY_BUCKET_TYPE, MultiBucketChooserActivity.BUCKET_TYPE_IMAGE);
//                startActivity(i);
//			}
//		});
//        
//        Button generateQRCodeButton = (Button) this.findViewById(R.id.btn_add_qrcode);
//        generateQRCodeButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				try {
//					String contentString = qrStrEditText.getText().toString();
//					if (!contentString.equals("")) {
//						//鏍规嵁瀛楃涓茬敓鎴愪簩缁寸爜鍥剧墖骞舵樉绀哄湪鐣岄潰涓婏紝绗簩涓弬鏁颁负鍥剧墖鐨勫ぇ灏忥紙600*600锛�					
//						
//						Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 600);
//						
//						//------------------娣诲姞logo閮ㄥ垎------------------//
//						Bitmap logoBmp = BitmapFactory.decodeResource(r, R.drawable.app_icon);
//						//浜岀淮鐮佸拰logo鍚堝苟
//						Bitmap bitmap = Bitmap.createBitmap(qrCodeBitmap.getWidth(), qrCodeBitmap.getHeight(), qrCodeBitmap.getConfig());
//				        Canvas canvas = new Canvas(bitmap);
//				        //浜岀淮鐮�				       
//				        canvas.drawBitmap(qrCodeBitmap, 0,0, null);
//				        //logo缁樺埗鍦ㄤ簩缁寸爜涓ぎ
//						canvas.drawBitmap(logoBmp, qrCodeBitmap.getWidth() / 2
//								- logoBmp.getWidth() / 2, qrCodeBitmap.getHeight()
//								/ 2 - logoBmp.getHeight() / 2, null);
//						//------------------娣诲姞logo閮ㄥ垎------------------//
//						
//						qrImgImageView.setImageBitmap(bitmap);
//					}else {
//						Toast.makeText(BarCodeTestActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
//					}
//					
//				} catch (WriterException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
//    }
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			Bundle bundle = data.getExtras();
//			String scanResult = bundle.getString("result");
//			resultTextView.setText(scanResult);
//		}
//	}
//}