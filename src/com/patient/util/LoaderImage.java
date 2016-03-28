package com.patient.util;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class LoaderImage {
	private DisplayImageOptions options;

	private static LoaderImage instance = null;

	public DisplayImageOptions getOptions() {
		return options;
	}

	private static int image;
	public static LoaderImage getInstance(int image) {
		if (null == instance) {
			instance = new LoaderImage();
		}
		if (image == -1) {
			image = 0;
		}
		LoaderImage.image = image;
		
		return instance;
	}

 
	public void ImageLoaders(String url, ImageView iv) {
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(image)
		.showImageOnFail(image)
		.resetViewBeforeLoading(true).cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.ARGB_8888).considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		
		ImageLoader.getInstance().displayImage(url, iv, options,new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
			}
			
		});
	}
	
	
//	public void ImageLoaders(String url, ImageView iv) {
//
//		ImageLoader.getInstance().displayImage(url, iv, options,
//				new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							Bitmap loadedImage) {
//					}
//					
//				});
//	}

}
