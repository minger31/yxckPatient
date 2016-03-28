package com.patient.ui.patientUi.defineView.flowerAnimal;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.yxck.patient.R;

public class SnowView extends View {
	
	int MAX_SNOW_COUNT = 10;
	// 雪锟斤拷图片
	Bitmap bitmap_snows = null;
	// 锟斤拷锟斤拷
	private final Paint mPaint = new Paint();
	// 锟芥即锟斤拷锟斤拷锟�
	private static final Random random = new Random();
	// 锟斤拷锟斤拷位锟斤拷
	private Snow[] snows = new Snow[MAX_SNOW_COUNT];
	// 锟斤拷幕锟侥高度和匡拷锟�
	int view_height = 0;
	int view_width = 0;
	int MAX_SPEED = 60;

	/**
	 * 锟斤拷锟斤拷锟斤拷
	 * 
	 * 
	 */
	public SnowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SnowView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/**
	 * 锟斤拷锟斤拷锟斤拷女散锟斤拷锟侥伙拷图片锟斤拷锟节达拷锟斤拷
	 * 
	 */
	public void LoadSnowImage() {
		Resources r = this.getContext().getResources();
		bitmap_snows = ((BitmapDrawable) r.getDrawable(R.drawable.ic_send_flowers_false))
				.getBitmap();
	}

	/**
	 * 锟斤拷锟矫碉拷前锟斤拷锟斤拷锟绞碉拷矢叨群涂锟斤拷
	 * 
	 */
	public void SetView(int height, int width) {
		view_height = height - 100;
		view_width = width - 50;

	}

	/**
	 * 锟斤拷锟斤拷锟斤拷苫锟斤拷锟斤拷位锟斤拷
	 * 
	 */
	public boolean isLoad = false;
	public void addRandomSnow() {
		
		isLoad = true;
		for(int i =0; i< MAX_SNOW_COUNT;i++){
			snows[i] = new Snow(random.nextInt(view_width), 0,random.nextInt(MAX_SPEED));
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 if (!isLoad) {
			return;
		 }
		for (int i = 0; i < MAX_SNOW_COUNT; i += 1) {
			if (snows[i].coordinate.x >= view_width || snows[i].coordinate.y >= view_height) {
				snows[i].coordinate.y = 0;
				snows[i].coordinate.x = random.nextInt(view_width);
			}
			// 雪锟斤拷锟斤拷锟斤拷锟斤拷俣锟�
			snows[i].coordinate.y += snows[i].speed;
			//雪锟斤拷飘锟斤拷锟斤拷效锟斤拷

			// 锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷锟街ｏ拷锟斤拷雪锟斤拷锟斤拷水平锟狡讹拷锟斤拷效锟斤拷
			int tmp = MAX_SPEED/2 - random.nextInt(MAX_SPEED);
			//为锟剿讹拷锟斤拷锟斤拷锟斤拷然锟皆ｏ拷锟斤拷锟剿斤拷锟斤拷俣却锟斤拷锟窖╋拷锟斤拷锟斤拷锟斤拷锟斤拷俣龋锟斤拷锟矫此斤拷锟斤拷俣锟斤拷锟斤拷锟饺★拷锟斤拷锟斤拷锟劫度★拷
			snows[i].coordinate.x += snows[i].speed < tmp ? snows[i].speed : tmp;
			canvas.drawBitmap(bitmap_snows, ((float) snows[i].coordinate.x),
					((float) snows[i].coordinate.y), mPaint);
		}

	}

}
