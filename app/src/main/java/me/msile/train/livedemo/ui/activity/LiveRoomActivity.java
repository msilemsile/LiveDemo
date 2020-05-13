package me.msile.train.livedemo.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import me.msile.train.livedemo.LiveDemoApplication;
import me.msile.train.livedemo.R;
import me.msile.train.livedemo.manager.LiveFloatViewManager;
import me.msile.train.livedemo.model.LiveChatMessage;
import me.msile.train.livedemo.ui.adapter.LiveChatMessageAdapter;
import me.msile.train.livedemo.ui.view.ChatInputLayout;
import me.msile.train.livedemo.ui.view.HeartFloatView;
import me.msile.train.livedemo.ui.view.KeyboardFrameLayout;
import me.msile.train.livedemo.ui.view.LiveFixedSizeFrameLayout;
import me.msile.train.livedemo.ui.view.LiveTransOffsetView;
import me.msile.train.livedemo.ui.view.LiveVideoView;
import me.msile.train.livedemo.utils.DisplayUtils;
import me.msile.train.livedemo.utils.floatpermission.FloatWindowUtils;

/**
 * 直播间
 */

public class LiveRoomActivity extends AppCompatActivity implements ChatInputLayout.OnSendMessageListener {

    public static final String LIVE_TEST_ADDRESS = "rtmp://58.200.131.2:1935/livetv/hunantv";

    private KeyboardFrameLayout mRootFl;
    private LiveFixedSizeFrameLayout mFixedSizeLayout;
    private View mTitleLayout, mCloseIv;
    private RecyclerView mMessageRv;
    private ChatInputLayout mChatInputLayout;
    private HeartFloatView mHeartFloatView;
    private LiveTransOffsetView mTransOffsetView;
    private LiveVideoView mLiveVideoView;

    LiveChatMessageAdapter mMessageAdapter;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_room_main_layout);
        findViews();
        init();
    }

    private void findViews() {
        mRootFl = (KeyboardFrameLayout) findViewById(R.id.root_fl);
        mFixedSizeLayout = (LiveFixedSizeFrameLayout) findViewById(R.id.live_fixed_lay);
        mTitleLayout = findViewById(R.id.title_fl);
        mMessageRv = (RecyclerView) findViewById(R.id.chat_message_rv);
        mChatInputLayout = (ChatInputLayout) findViewById(R.id.chat_input_layout);
        mHeartFloatView = (HeartFloatView) findViewById(R.id.heart_float);
        mTransOffsetView = (LiveTransOffsetView) findViewById(R.id.live_trans_view);
        mCloseIv = findViewById(R.id.close_iv);
        mTransOffsetView.addTransView(mTitleLayout);
        mTransOffsetView.addTransView(mChatInputLayout);
        //添加视频播放器
        mLiveVideoView = LiveFloatViewManager.getInstance().getLiveVideoView(0);
        mFixedSizeLayout.addView(mLiveVideoView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private void init() {
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mRootFl.setOnKeyboardListener(new KeyboardFrameLayout.OnKeyboardListener() {
            @Override
            public void onChanged(boolean showKeyboard) {
                if (!showKeyboard) {
                    mChatInputLayout.refreshHideLayout();
                }
            }
        });
        mChatInputLayout.setSendMessageListener(this);
        mMessageRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMessageRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = DisplayUtils.dip2px(LiveRoomActivity.this, 6);
            }
        });
        mMessageRv.setPadding(0, DisplayUtils.dip2px(this, 100), 0, DisplayUtils.dip2px(this, 9));
        mMessageRv.setClipToPadding(false);
        mMessageAdapter = new LiveChatMessageAdapter(this, new ArrayList<LiveChatMessage>());
        mMessageRv.setAdapter(mMessageAdapter);
        mHeartFloatView.setStartPointX(DisplayUtils.dip2px(this, 72.5f));
        mHeartFloatView.setFixedHeartCanFloat(true);
        mChatInputLayout.setHeartFloatView(mHeartFloatView);
        mMessageRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                //添加聊天室欢迎消息
                addLiveMessage(LiveChatMessage.obtainMessage("房管", "欢迎进入msile直播间"));
                for (int i = 1; i < 6; i++) {
                    addLiveMessage(LiveChatMessage.obtainMessage("user" + i, "测试聊天" + i));
                }
            }
        }, 36);
        if (mLiveVideoView != null && mLiveVideoView.hasLiveVideo()) {
            mFixedSizeLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLive();
        }
    }

    private void refreshLive() {
        if (mLiveVideoView != null) {
            mLiveVideoView.setVideoUrl(LIVE_TEST_ADDRESS, null);
        }
    }

    @Override
    public void onSendMessage(String msg) {
        String userName = "user" + random.nextInt(66);
        addLiveMessage(LiveChatMessage.obtainMessage(userName, msg));
        mChatInputLayout.clearInput();
    }

    @Override
    public void onClickSmall(int from) {
        if (FloatWindowUtils.getInstance().canShowFloatWindow(this, (from == 0))) {
            LiveFloatViewManager.getInstance().addLiveFloatView(LIVE_TEST_ADDRESS);
            if (LiveFloatViewManager.getInstance().hasLiveFloatView()) {
                leaveLiveRoom();
                finish();
            } else {
                Toast.makeText(this, "请开启悬浮窗权限!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 添加聊天消息
     */
    private void addLiveMessage(LiveChatMessage chatMessage) {
        if (chatMessage != null) {
            int lastMessageCount = mMessageAdapter.getItemCount();
            mMessageAdapter.add(chatMessage);
            mMessageAdapter.notifyItemInserted(lastMessageCount);
            final int lastMessagePos = mMessageAdapter.getItemCount() - 1;
            mMessageRv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMessageRv.scrollToPosition(lastMessagePos);
                }
            }, 36);
        }
    }

    /**
     * 离开直播间
     */
    public void leaveLiveRoom() {
        //释放播放器和移除消息回调
        mLiveVideoView = null;
        LiveFloatViewManager.getInstance().clearLastLiveRoomData();
    }

    protected void onPause() {
        super.onPause();
        if (mLiveVideoView != null && mLiveVideoView.isLiveVideoPlaying()) {
            mLiveVideoView.pauseLiveVideo();
        }
        //清除键盘焦点
        if (!mRootFl.isShowKeyboard()) {
            mChatInputLayout.clearInputFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveVideoView != null && mLiveVideoView.isLiveVideoPause()) {
            mLiveVideoView.startLiveVideo();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveLiveRoom();
        LiveDemoApplication.getContext().appExit();
    }

}
