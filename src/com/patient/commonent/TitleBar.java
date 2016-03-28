package com.patient.commonent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yxck.patient.R;

 
public class TitleBar {

    private Activity mActivity = null;
    // 返回按钮
    private TextView backBtn = null;
    private LinearLayout leftView = null;
    // 自定义按�?
    private LinearLayout rightView = null;
    private TextView rightBtn = null;
    // 标题
    private TextView titleTxt = null;
    // 标题
    private LinearLayout titleLine = null;
    
    private View titleView = null;

    public TitleBar(Activity activity, View parent) {
        mActivity = activity;
        initWidget(parent);
    }

    public void setTitle(String title,int color) {
    	if (!TextUtils.isEmpty(title)) {
    		 titleTxt.setBackgroundResource(0);
    		// titleTxt.setTextColor(mActivity.getResources().getColor(R.color.color_b5b5b5));
    		 titleTxt.setTextColor(mActivity.getResources().getColor(R.color.white));
    		 titleTxt.setText(title);
		} 
        if (color > 0 ) {
        	titleTxt.setTextColor(mActivity.getResources().getColor(R.color.white));
//			titleTxt.setTextColor(mActivity.getResources().getColor(color));
		}
    }
 
    public void setTitle(int title,int color) {
    	if (title > 0) {
    		 titleTxt.setBackgroundResource(0);
    		 titleTxt.setBackgroundResource(title);
		} 
        if (color > 0 ) {
        	titleTxt.setTextColor(mActivity.getResources().getColor(R.color.white));
//			titleTxt.setTextColor(mActivity.getResources().getColor(color));
		}
    }
    
    
    public void setTitle(int  bgId) {
        titleTxt.setBackgroundResource(bgId);
    }

	public void setBackground(int index){
    	if(titleView != null) 
    		titleView.setBackgroundColor(mActivity.getResources().getColor(index));
    }

    public void enableBack() {
        backBtn.setVisibility(View.VISIBLE);
        // 默认返回事件，关闭当前activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }
    
    public TextView getBackBtn(){
    	return backBtn;
    }

    /**
     * @author: lihs
     * @Title: setBack
     * @Description: setBack返回�?
     * @param backStr
     * @param clickListener
     * @date: 2013-12-17 下午4:53:11
     */
    public void setBack(String backStr, OnClickListener clickListener,int icon) {
        backBtn.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(backStr)) {
            backBtn.setText(backStr);
        }
        
        backBtn.setTextColor(mActivity.getResources().getColor(R.color.white));
        if (icon > 0) {
        	// 设置图片在文字的左边
        	backBtn.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        }
        
        if (clickListener != null) {
            // 用户自定义返回事�?
            leftView.setOnClickListener(clickListener);
         } else {
            // 默认返回事件，关闭当前activity
        	leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    mActivity.finish();
                }
            });
        }
    }

   
    public void enableRightBtn(String btnStr, int icon, View.OnClickListener clickListener) {
    	
        rightBtn.setVisibility(View.VISIBLE);
       
        if (btnStr != null) {
            rightBtn.setText(btnStr);
            rightBtn.setTextColor(mActivity.getResources().getColor(R.color.white));
        }
        
        if (icon > 0) {
            rightBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
        }
        
        if (clickListener != null) {
            // 用户自定义事�?
        	rightView.setOnClickListener(clickListener);
        }
    }
    
    @SuppressLint("NewApi")
	public void enableRightBtn(int drawableId , View.OnClickListener clickListener){
    	
    	if (clickListener != null) {
            // 用户自定义事�?
            rightBtn.setOnClickListener(clickListener);
        }
    }

    public void setRightBtnVisibility(int visibility) {
        rightBtn.setVisibility(visibility);
    }

    public void addCustomTitleView(View view) {
    	
        if (titleLine != null) {
            titleLine.addView(view);
        }
    }

    public void addCustomRightView(View view) {
    	
        if (rightView != null) {
            rightView.addView(view);
        }
    }
    
    public void addCustomLeftView(View view) {
    	
        if (leftView != null) {
        	leftView.removeAllViews();
        	leftView.addView(view);
        }
    }
    public void removeCustomLeftView() {
    	
        if (leftView != null) {
        	leftView.removeAllViews();
        }
    }

    public boolean isShowing() {
        if (titleView.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void hide() {
        Animation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(200);
        titleView.startAnimation(mHiddenAction);
        titleView.setVisibility(View.GONE);
    }

    public void show() {
    	
        Animation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,   
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,   
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(200);
        titleView.startAnimation(mShowAction);
        titleView.setVisibility(View.VISIBLE);
        
    }

    public void setTitleVisibility(int visibility) {
    	
        titleView.setVisibility(visibility);
    }

    private LinearLayout rt;
    public void setTitleBg(int bgId){
    	
    	if (bgId > 0) {
    		rt.setBackgroundColor(mActivity.getResources().getColor(bgId));
		}
    }
    
    private void initWidget(View parent) {
    	
        backBtn = (TextView) parent.findViewById(R.id.back_btn);
        leftView = (LinearLayout) parent.findViewById(R.id.title_left);
        rightView = (LinearLayout) parent.findViewById(R.id.title_right);
        rightBtn = (TextView) parent.findViewById(R.id.right_btn);
        titleTxt = (TextView) parent.findViewById(R.id.title_txt);
        titleLine = (LinearLayout) parent.findViewById(R.id.title_center);
        rt = (LinearLayout) parent.findViewById(R.id.bg);
    }
}
