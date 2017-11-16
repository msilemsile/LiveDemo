package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.msile.train.livedemo.utils.KeyBoardUtils;
import me.msile.train.livedemo.R;

/**
 * 直播聊天输入框
 */

public class ChatInputLayout extends FrameLayout {

    private EditText mChatEdit;
    private TextView mSendTv;
    private TextView mEditBgTv;
    private ImageView mLikeIv;
    private View mBottomRl;
    private OnSendMessageListener sendMessageListener;
    private HeartFloatView heartFloatView;

    private boolean isShow;

    public ChatInputLayout(Context context) {
        super(context);
    }

    public ChatInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChatEdit = (EditText) findViewById(R.id.chat_etv);
        mSendTv = (TextView) findViewById(R.id.send_tv);
        mLikeIv = (ImageView) findViewById(R.id.like_iv);
        mBottomRl = findViewById(R.id.bottom_rl);
        mEditBgTv = (TextView) findViewById(R.id.edit_bg_tv);
        mEditBgTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        findViewById(R.id.small_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendMessageListener != null) {
                    sendMessageListener.onClickSmall(0);
                }
            }
        });
        mSendTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgTemp = mChatEdit.getText().toString();
                if (!TextUtils.isEmpty(msgTemp) && sendMessageListener != null) {
                    KeyBoardUtils.hideSoftKeyboard(getContext(), v);
                    sendMessageListener.onSendMessage(msgTemp);
                }
            }
        });
        mLikeIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeIv.animate().cancel();
                mLikeIv.setScaleX(1);
                mLikeIv.setScaleY(1);
                mLikeIv.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mLikeIv.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                    }
                });
                if (heartFloatView != null) {
                    heartFloatView.addHeartItem();
                }
            }
        });
    }

    public void setHeartFloatView(HeartFloatView heartFloatView) {
        this.heartFloatView = heartFloatView;
    }

    /**
     * 清除输入数据
     */
    public void clearInput() {
        mChatEdit.setText("");
        mEditBgTv.setText("快来和主播互动吧");
    }

    /**
     * 清除输入焦点
     */
    public void clearInputFocus() {
        mChatEdit.clearFocus();
    }

    public void show() {
        refreshShowLayout();
        mChatEdit.requestFocus();
        KeyBoardUtils.showSoftKeyboard(getContext(), mChatEdit);
        isShow = true;
    }

    public void hide() {
        refreshHideLayout();
        mChatEdit.clearFocus();
        KeyBoardUtils.hideSoftKeyboard(getContext(), mChatEdit);
        isShow = false;
    }

    public void refreshShowLayout() {
        setBackgroundColor(0x66ffffff);
        mSendTv.setVisibility(VISIBLE);
        mChatEdit.setVisibility(VISIBLE);
        mBottomRl.setVisibility(GONE);
    }

    public void refreshHideLayout() {
        setBackgroundColor(0x66000000);
        mSendTv.setVisibility(INVISIBLE);
        mChatEdit.setVisibility(INVISIBLE);
        mBottomRl.setVisibility(VISIBLE);
        String msgTemp = mChatEdit.getText().toString();
        if (TextUtils.isEmpty(msgTemp)) {
            mEditBgTv.setText("快来和主播互动吧");
        } else {
            mEditBgTv.setText(msgTemp);
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setSendMessageListener(OnSendMessageListener sendMessageListener) {
        this.sendMessageListener = sendMessageListener;
    }

    public interface OnSendMessageListener {
        void onSendMessage(String msg);

        void onClickSmall(int from);
    }

}
