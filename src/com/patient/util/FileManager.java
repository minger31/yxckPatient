package com.patient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FileManager {
    public static final Uri IMAGE_BASEURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri VIDEO_BASEURI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	private static FileManager instance = null;

	public static FileManager getFileManager(){
		if(null == instance){
			instance = new FileManager();
		}
		return instance;
	}

	public static void cleanManager(){
		if(null != instance){
			instance = null;
		}
	}

	public static boolean isImageFile(File file){
		boolean isTrue = false;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
            isTrue = isImageFileInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream = null;
            }
        }
		return isTrue;
	}

    public static boolean isImageFileInputStream(FileInputStream fis) {
        if (fis == null) {
            return false;
        }
        boolean isTrue = false;
			byte[] buffer=new byte[2];
			String filecode = "";
        try {
            if (-1 != fis.read(buffer)) {
				for (int i = 0; i < buffer.length; i++) {
					filecode+=Integer.toString((buffer[i]&0xFF));
				}
				switch (Integer.parseInt(filecode)) {
				case 255216:
				case 7173:
				case 6677:
				case 13780:
					isTrue = true;
					break;
				default:
				}
			}
        } catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isTrue;
	}

	@SuppressWarnings("deprecation")
	public String getFilePath(Context context ,Uri DBUri){
		String path  = null;
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = ((Activity) context).managedQuery(DBUri, proj, null, null, null);
		if(null == cursor)
			return path;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		path = cursor.getString(column_index);
		cursor.close();
		cursor = null;
		return path;
	}

	public static Uri getImageUriThrowPath(Context context, String filePath) {
		Uri uri = null;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if(null != cursor){
			cursor.moveToFirst();
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			uri = Uri.withAppendedPath(IMAGE_BASEURI, "" + id);
			cursor.close();
		}
		return uri;
	}

	/** 小米1 手机上，此段代码查询不出来图片路径 */
//	public String getImagePathFromUri(Context context, Uri contentUri) {
//		String[] proj = { MediaStore.Images.Media.DATA };
//		String path = null;
//		Cursor cursor = context.getContentResolver().query(
//				contentUri, proj, // Which columns to return
//				null, // WHERE clause; which rows to return (all rows)
//				null, // WHERE clause selection arguments (none)
//				null); // Order-by clause (ascending by name)
//		if(null != cursor){
//			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			cursor.moveToFirst();
//			path = cursor.getString(column_index);
//			cursor.close();
//		}
//		return  path;
//	}

	/**
     * 获取uri物理地址
     * 
     * @return
     */
    public static String getPhotoPath(Context context, Uri uri) {
        String photoPath = "";
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(uri, null, null, null, null);
            int actual_image_column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            photoPath = cursor.getString(actual_image_column_index);
        } catch (Exception e) {
            photoPath = uri.getPath();
            Log.e("zhagt", uri.toString() + "|" + uri.getPath());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return photoPath;
    }

    /**
     * 获取uri物理地址
     * 
     * @return
     */
    public static String getVideoPath(Context context, Uri uri) {
        if (uri == null) {
            return "";
        }
        String photoPath = "";
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(uri, null, null, null, null);
            int actual_image_column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            photoPath = cursor.getString(actual_image_column_index);
        } catch (Exception e) {
            photoPath = uri.getPath();
            Log.e("zhagt", uri.toString() + "|" + uri.getPath());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return photoPath;
    }

    public static String getImgBucketName(Context context, String _id) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        String name = "相册";
        try {
            String[] selectArgs = new String[] { _id };
            cursor = cr
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Images.Media.BUCKET_DISPLAY_NAME },
                            MediaStore.Images.Media.BUCKET_ID + " = ? ",
                            selectArgs, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                name = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e("getContentsName", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return name;
    }

    public static String getVideoBucketName(Context context, String _id) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        String name = "视频";
        try {
            String[] selectArgs = new String[] { _id };
            cursor = cr
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Video.Media.BUCKET_DISPLAY_NAME },
                            MediaStore.Video.Media.BUCKET_ID + " = ? ",
                            selectArgs, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                name = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e("getContentsName", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return name;
    }
}

