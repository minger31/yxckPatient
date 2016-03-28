package com.patient.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;

public class BitmapManager {
	private static final int BUFFER_SIZE = 8 * 1024;
	
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			return inSampleSize;
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename,int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}
	
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getImageDrawable(String path)throws IOException{
		File file = new File(path);
		if(!file.exists()){
			return null;
		}

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] bt = new byte[BUFFER_SIZE];

		InputStream in = new FileInputStream(file);

		int readLength = in.read(bt);
		while (readLength != -1) {
			outStream.write(bt, 0, readLength);
			readLength = in.read(bt);
		}

		byte[] data = outStream.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		BitmapDrawable bd = new BitmapDrawable(bitmap);

		return bd;
	}
	
	/**
	 * 将图片转换为黑白图片
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
	
	/**
	 * @Title: getRoundedCornerBitmap
	 * @Description: 获得圆角图片的方法
	 * @param bitmap 原图片
	 * @param roundPx 圆角的弧度
	 * @return: Bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setColor(color);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/** 
	 * @Title: getGraysRoundedCornerBitmap 
	 * @Description: 获取灰色圆角图片
	 */
	public static Bitmap getGraysRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		

		final Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	// 生成原来的1/4
	public static Bitmap getBitmapByBytes(byte[] bytes){  
	      
	    //对于图片的二次采样,主要得到图片的宽与高  
	    int width = 0;  
	    int height = 0;  
	    int sampleSize = 1; //默认缩放为1  
	    BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true;  //仅仅解码边缘区域  
	    //如果指定了inJustDecodeBounds，decodeByteArray将返回为空  
	    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	    //得到宽与高  
	    height = options.outHeight;  
	    width = options.outWidth;  
	  
//	    //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
//	    while ((height / sampleSize > Cache.IMAGE_MAX_HEIGHT)  
//	            || (width / sampleSize > Cache.IMAGE_MAX_WIDTH)) {  
//	        sampleSize *= 2;  
//	    }  
	    sampleSize *= 2;  
	    //不再只加载图片实际边缘  
	    options.inJustDecodeBounds = false;  
	    //并且制定缩放比例  
	    options.inSampleSize = sampleSize;  
	    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
	}  
	
	// 缩放图片
	public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
	   // 获得图片的宽高
		if (bm == null) {
			return null;
		}
	   int width = bm.getWidth();
	   int height = bm.getHeight();
	   // 计算缩放比例
	   float scaleWidth = ((float) newWidth) / width;
	   float scaleHeight = ((float) newHeight) / height;
	   // 取得想要缩放的matrix参数
	   Matrix matrix = new Matrix();
	   matrix.postScale(scaleWidth, scaleHeight);
	   // 得到新的图片
	   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	    return newbm;
	}
	
}

