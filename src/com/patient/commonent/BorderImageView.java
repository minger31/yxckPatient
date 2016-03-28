package com.patient.commonent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("DrawAllocation")
public class BorderImageView extends ImageView {

	Paint paint = null;
 
	public BorderImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}
  
	@Override
	protected void onDraw(Canvas canvas) {
		 
		super.onDraw(canvas);
        Rect rec = canvas.getClipBounds();
        paint.setAntiAlias(true);                       //设置画笔为无锯齿   
//        paint.setColor(getResources().getColor(R.color.color_D5D5D5));                    //设置画笔颜色   
//	    canvas.drawColor(Color.WHITE);                  //白色背景   
	    paint.setStrokeWidth((float) 2.0);              //线宽   
	    paint.setStyle(Style.STROKE);                   //空心效果   
 
	    RectF r2=new RectF();                           //RectF对象   
	    r2.left= rec.left;                                 //左边   
	    r2.top=  rec.top;                                 //上边   
	    r2.right= rec.right;                                   //右边   
	    r2.bottom=  rec.bottom;                              //下边   
	    canvas.drawRoundRect(r2, 10, 10, paint);        //绘制圆角矩形   
 
	}

}
