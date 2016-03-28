package com.patient.ui.patientUi.activity.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

public class DownImageUtil {
	
	private static String path;
	public static List<String> listSave(List<String> photos){
		List<String> smallImg = null;
		if (photos != null) {
			smallImg = new ArrayList<String>();
			for (String bigImg : photos) {
				String temp =  DownImageUtil.save(bigImg);
				if (TextUtils.isEmpty(temp)) {
					temp = bigImg;
				}
				smallImg.add(temp);
			}
		}
		return smallImg;
	}
	public static ArrayList<String> arraySave(ArrayList<String> photos){
		ArrayList<String> smallImg = null;
		if (photos != null) {
			smallImg = new ArrayList<String>();
			for (String bigImg : photos) {
				String temp =  DownImageUtil.save(bigImg);
				if (TextUtils.isEmpty(temp)) {
					temp = bigImg;
				}
				smallImg.add(temp);
			}
		}
		return smallImg;
	}
	
	
	public static  String save(String path) {
		if(path!=null){
			try {
				File f = new File(path);
				//根据路径获得突破并压缩返回bitmap用于显示
				Bitmap bm = getSmallBitmap(path);
				//保存图片的名字
				File fileName=new File(getAlbumDir(), "small_1" + f.getName());
				//保存到sd卡
				FileOutputStream fos=null;
				try {
					fos = new FileOutputStream(fileName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
				return fileName.getPath();

			} catch (Exception e) {
                 return null;
			}
		}
		return null;
	}
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getAlbumDir() {
		 path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				+getAlbumName();
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取保存 隐患检查的图片文件夹名称
	 * 
	 * @return
	 */
	public static String getAlbumName() {
		return "Pictic";
	}
	public static void delete(){
		deleteFile( getAlbumDir());
	}
	/**
	 * 删除文件夹
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					DownImageUtil.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			return;
		}
	}
}
