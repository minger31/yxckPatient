package com.patient.commonent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.yxck.patient.R;

/**
 * <dl>
 * <dt>BottomGridMenu.java</dt>
 * <dd>Description:底部按钮菜单</dd>
 * <dd>Copyright: Copyright (C) 2013</dd>
 * <dd>Company:  </dd>
 * <dd>CreateDate: 2013-12-18 上午9:59:00</dd>
 * </dl>
 * 
 * @author lihs
 */
public class BottomButtonMenu extends BottomMenuWindow {

    private List<Button> dispButtons = new ArrayList<Button>();

    public BottomButtonMenu(Activity activity) {
        super(activity);
        setContentView(R.layout.bottom_menu_pick_photo);
        ((Button) getContentView().findViewById(R.id.btn_cancel))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
    }

    public void addButtonFirst(final MenuClickedListener listener,
            String textContent) {
        // 普�?按钮1
        Button button = ((Button) getContentView().findViewById(R.id.button1));
        button.setText(textContent);
        button.setVisibility(View.VISIBLE);
        dispButtons.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onMenuClicked();
                }
            }
        });
    }

    public void addButtonSecond(final MenuClickedListener listener,
            String textContent) {
        // 普�?按钮2
        Button button = ((Button) getContentView().findViewById(R.id.button2));
        button.setText(textContent);
        button.setVisibility(View.VISIBLE);
        dispButtons.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onMenuClicked();
                }
            }
        });
    }

    /**
     * @author: lihs
     * @Title: addButtonThird
     * @Description:
     * @param listener
     * @param textContent
     * @param isLineShow
     * @param isTopOrBottom
     *            0:top;1:bottom;
     * @date: 2014-1-15 下午8:57:28
     */
    public void addButtonThird(final MenuClickedListener listener,
            String textContent) {
        // 普�?按钮3
        Button button = ((Button) getContentView().findViewById(R.id.button3));
        button.setText(textContent);
        button.setVisibility(View.VISIBLE);
        dispButtons.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onMenuClicked();
                }
            }
        });
    }

    public void addButtonFourth(final MenuClickedListener listener,
            String textContent) {
        // 普�?按钮4
        Button button = ((Button) getContentView().findViewById(R.id.button4));
        button.setText(textContent);
        button.setVisibility(View.VISIBLE);
        dispButtons.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onMenuClicked();
                }
            }
        });
    }

    public void addButtonChangeNumber(final MenuClickedListener listener,
            String textContent) {
        // 红色按钮
        Button button = ((Button) getContentView().findViewById(R.id.btn_exit));
        button.setText(textContent);
        button.setVisibility(View.VISIBLE);
        dispButtons.add(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onMenuClicked();
                }
            }
        });
    }

    public void show() {
        if (dispButtons.size() > 0) {
            for (int i = 0; i < dispButtons.size(); i++) {
                Button dispButton = dispButtons.get(i);
                if (i == 0) {
                    if (dispButtons.size() == 1) {
                        // 只有�?��按钮，显示上下圆角按�?
                        dispButton
                                .setBackgroundResource(R.drawable.bg_item_shape_top_bottom);
                    } else {
                        // 多于�?��按钮，显示上圆角按钮
                        dispButton.setBackgroundResource(R.drawable.bg_item_shape_top);
                        View lineView = getContentView().findViewWithTag(
                                "line" + (String) dispButton.getTag());
                        if (lineView != null) {
                            lineView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (i < dispButtons.size() - 1) {
                        // 中间按钮
                        dispButton.setBackgroundResource(R.drawable.bg_item_shape);
                        View lineView = getContentView().findViewWithTag(
                                "line" + (String) dispButton.getTag());
                        if (lineView != null) {
                            lineView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // 下圆角按�?
                        dispButton
                                .setBackgroundResource(R.drawable.bg_item_shape_bottom);
                    }
                }
            }
        }
        super.show();
    }

    public void setSelected(int res) {
        ((Button) getContentView().findViewById(res)).setPressed(true);
    }
}
