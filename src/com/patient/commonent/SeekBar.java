package com.patient.commonent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.patient.constant.CommonConstant;
import com.yxck.patient.R;
 
public class SeekBar extends View{
	private List<String> letters = new ArrayList<String>();
	private static String[] letters2 = CommonConstant.LETTERS;
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBkg = false;
	private Bitmap mbitmap;
	private OnTouchingSeekBarChangedListener onTouchingLetterChangedListener;

	public SeekBar(Context context) {
		super(context);
		init();
	}
	public SeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	protected void init() {
		for (int i = 0; i < letters2.length; i++) {
			letters.add(letters2[i]);
		}
		InputStream is = getResources().openRawResource(R.drawable.btn_search_icons); 
		mbitmap = BitmapFactory.decodeStream(is); 
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (letters.size() == 0)
			return;
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		int height = getHeight()-15;
		int width = getWidth();
		int singleHeight = height / letters.size();
		
		for (int i = 0; i < letters.size(); i++) {
			paint.setColor(Color.parseColor("#686D73"));
			paint.setAntiAlias(true);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(16);
			if (i == choose) {
				paint.setColor(Color.parseColor("#40000000"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(letters.get(i)) / 2;
			float yPos = singleHeight * i + singleHeight / 2 + 20;
			
			if(letters.get(i).equals("-99")){
				canvas.drawBitmap(mbitmap, xPos, yPos - 17, paint); 
			}
			else{
				canvas.drawText(letters.get(i), xPos, yPos, paint);
			}
			paint.reset();
		}
	}
	public static String getAlphaString(int index){
		return letters2[index];
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (letters.size() == 0) return true;
		
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int itemHeight = getHeight() / letters.size();
		final int c = (int) ((y - itemHeight / 2) / itemHeight + 0.5);
		final OnTouchingSeekBarChangedListener listener = onTouchingLetterChangedListener;
//		final int c  = y / (getMeasuredHeight() / l.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < letters.size()) {
					listener.onTouchingLetterChanged(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < letters.size()) {
					listener.onTouchingLetterChanged(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}
	public void setOnTouchingSeekBarChangedListener(
			OnTouchingSeekBarChangedListener onTouchingSeekBarChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingSeekBarChangedListener;
	}

	public interface OnTouchingSeekBarChangedListener {
		public void onTouchingLetterChanged(int section);
	}

}
