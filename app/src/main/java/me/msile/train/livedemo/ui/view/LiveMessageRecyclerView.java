package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import me.msile.train.livedemo.utils.DisplayUtils;

/**
 * 直播消息列表
 */

public class LiveMessageRecyclerView extends RecyclerView {

    private Paint mShaderPaint;

    public LiveMessageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveMessageRecyclerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int width = getMeasuredWidth();
        if (width > 0 && mShaderPaint == null) {
            mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShaderPaint.setShader(new LinearGradient(width / 2, 0, width / 2, DisplayUtils.dip2px(getContext(), 30), 0x00000000, 0xffffffff, Shader.TileMode.CLAMP));
            mShaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
        if (mShaderPaint != null) {
            c.drawPaint(mShaderPaint);
        }
    }

}
