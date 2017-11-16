package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import me.msile.train.livedemo.utils.KeyBoardUtils;

/**
 * 键盘监听布局
 */
public class KeyboardFrameLayout extends FrameLayout {

    private OnKeyboardListener onKeyboardListener;
    protected boolean mShowKeyboard = false;

    public KeyboardFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public KeyboardFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardFrameLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowKeyboard) {
                    KeyBoardUtils.hideSoftKeyboard(context, KeyboardFrameLayout.this);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (0 != oldw && 0 != oldh) {
            boolean tempShowKeyboard = h < oldh;
            if (tempShowKeyboard != mShowKeyboard) {
                mShowKeyboard = tempShowKeyboard;
                if (null != onKeyboardListener) {
                    onKeyboardListener.onChanged(mShowKeyboard);
                }
                if (mShowKeyboard) {
                    doShow();
                } else {
                    doHide();
                }
            }
        }
    }

    protected void doShow() {

    }

    protected void doHide() {

    }

    public boolean isShowKeyboard() {
        return mShowKeyboard;
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }

    public interface OnKeyboardListener {
        void onChanged(boolean showKeyboard);
    }

}