package me.msile.train.livedemo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.msile.train.livedemo.R;
import me.msile.train.livedemo.model.LiveChatMessage;
import me.msile.train.livedemo.ui.view.ChatItemMessageLayout;

/**
 * 直播聊天室消息
 */

public class LiveChatMessageAdapter extends BaseRecyclerAdapter<LiveChatMessage> {

    public LiveChatMessageAdapter(Context context, List<LiveChatMessage> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LiveMsgHolder(mInflater.inflate(R.layout.live_item_chat_message, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LiveChatMessage chatMessage = getItemData(position);
        ((ChatItemMessageLayout) holder.itemView).setData(chatMessage);
    }

    static class LiveMsgHolder extends RecyclerView.ViewHolder {
        public LiveMsgHolder(View itemView) {
            super(itemView);
            ((ChatItemMessageLayout) itemView).findViews();
        }
    }

}
