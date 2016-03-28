package com.patient.commonent;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.patient.util.CommonUtil;
import com.yxck.patient.R;

 
public class BottomMenuWindow {

    private Activity mActivity = null;

    private Dialog mDialog = null;

    private View contentView = null;

    public BottomMenuWindow(Activity activity) {
        mActivity = activity;
        mDialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
    }

    public BottomMenuWindow(Activity activity, int res) {
        this(activity);
        setContentView(res);
    }

    public void setContentView(int res) {
    	if(null == mDialog){
    		return;
    	}
    	
        contentView = mActivity.getLayoutInflater().inflate(res, null);
        mDialog.setContentView(contentView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = mDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 设置显示动画
        window.setWindowAnimations(R.style.bottom_menu_windown_animstyle);

        // 设置窗口大小和位�?
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = CommonUtil.getDeviceSize(mActivity).y;
        wl.width = CommonUtil.getDeviceSize(mActivity).x;
        mDialog.onWindowAttributesChanged(wl);
        // 点击窗口以外区域，关闭窗�?
        mDialog.setCanceledOnTouchOutside(true);
    }

    public View getContentView() {
        return contentView;
    }

    public void show() {
    	if(mDialog!=null){
        mDialog.show();
    }
    }

    public void dismiss() {
    	if(mDialog!=null){
        mDialog.dismiss();
    }
    }

    public interface MenuClickedListener {
        public void onMenuClicked();
    }
}
