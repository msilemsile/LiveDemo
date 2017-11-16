package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.msile.train.livedemo.model.LiveChatMessage;
import me.msile.train.livedemo.R;

/**
 * 聊天item普通消息
 */

public class ChatItemMessageLayout extends FrameLayout {

    private TextView mMessageTv;

    public ChatItemMessageLayout(Context context) {
        super(context);
    }

    public ChatItemMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatItemMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void findViews() {
        mMessageTv = (TextView) findViewById(R.id.message_tv);
    }

    public void setData(LiveChatMessage chatMessage) {
        if (chatMessage != null) {
            String messageContent = chatMessage.userName + "  " + chatMessage.message;
            if (TextUtils.isEmpty(messageContent)) {
                mMessageTv.setText("");
                return;
            }
            int startIndex = chatMessage.userName.length();
            SpannableStringBuilder msgBuilder = new SpannableStringBuilder(messageContent);
            msgBuilder.setSpan(new ForegroundColorSpan(0xffffffff), startIndex, messageContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mMessageTv.setText(msgBuilder);
        }
    }

}
