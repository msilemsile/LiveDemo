package me.msile.train.livedemo.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.msile.train.livedemo.R;

/**
 * 漂浮的心
 */

public class HeartFloatView extends View {

    private Bitmap[] mHeartImgs = new Bitmap[6];
    private int mHeight, mWidth;
    private int mHeartWidth, mHeartHeight, mHalfHeartWidth, mHalfHeartHeight;
    private Random mRandom = new Random();
    private int mAnimDuration = 3000;

    private List<InnerHeartItem> mHeartItems = new ArrayList<>();
    private List<InnerHeartItem> mFixedHeartItems = new ArrayList<>();
    private int mFixedHeartSize = 6;

    private Paint bitmapPaint;
    private Matrix bitmapMatrix;

    private int mStartPointX;
    private boolean mFixedHeartCanFloat;
    private boolean isRecycleBitmap;

    public HeartFloatView(Context context) {
        super(context);
        init();
    }

    public HeartFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        Resources resources = getResources();
        mHeartImgs[0] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart1);
        mHeartImgs[1] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart2);
        mHeartImgs[2] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart3);
        mHeartImgs[3] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart4);
        mHeartImgs[4] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart5);
        mHeartImgs[5] = BitmapFactory.decodeResource(resources, R.mipmap.live_heart6);
        mHeartWidth = mHeartImgs[0].getWidth();
        mHeartHeight = mHeartImgs[0].getHeight();
        mHalfHeartWidth = mHeartWidth / 2;
        mHalfHeartHeight = mHeartHeight / 2;
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isRecycleBitmap) {
            return;
        }
        if (mWidth == 0 || mHeight == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }
        if (checkOutOfEdge()) {
            return;
        }
        if (mFixedHeartCanFloat && mFixedHeartItems.isEmpty()) {
            for (int i = 0; i < mFixedHeartSize; i++) {
                InnerHeartItem innerHeartItem = buildHeartItem(true);
                innerHeartItem.animatorSet.setStartDelay(i * 500);
                innerHeartItem.animatorSet.start();
                mFixedHeartItems.add(innerHeartItem);
            }
        }
        //先移除可回收的item
        recycleHearts();
        //开始绘制所的心
        for (InnerHeartItem heartItem : mHeartItems) {
            List<Animator> animators = heartItem.animatorSet.getChildAnimations();
            bitmapMatrix.reset();
            float posX = 0;
            float posY = 0;
            for (Animator animator : animators) {
                Object value = ((ValueAnimator) animator).getAnimatedValue();
                if (value instanceof Integer) {
                    int alpha = (Integer) value;
                    bitmapPaint.setAlpha(alpha);
                } else if (value instanceof PointF) {
                    PointF pointF = (PointF) value;
                    posX = pointF.x + mHalfHeartWidth;
                    posY = pointF.y + mHalfHeartHeight;
                    bitmapMatrix.postTranslate(pointF.x, pointF.y);
                } else if (value instanceof Float) {
                    float scale = (float) value;
                    if (posX > 0 && posY > 0) {
                        bitmapMatrix.postScale(scale, scale, posX, posY);
                    }
                }
            }
            if (heartItem.heartIndex < 0 || heartItem.heartIndex >= mHeartImgs.length) {
                heartItem.heartIndex = 0;
            }
            Bitmap bitmap = mHeartImgs[heartItem.heartIndex];
            if (!bitmap.isRecycled()) {
                canvas.drawBitmap(mHeartImgs[heartItem.heartIndex], bitmapMatrix, bitmapPaint);
            }
        }
        for (InnerHeartItem heartItem : mFixedHeartItems) {
            List<Animator> animators = heartItem.animatorSet.getChildAnimations();
            bitmapMatrix.reset();
            float posX = 0;
            float posY = 0;
            for (Animator animator : animators) {
                Object value = ((ValueAnimator) animator).getAnimatedValue();
                if (value instanceof Integer) {
                    int alpha = (Integer) value;
                    bitmapPaint.setAlpha(alpha);
                } else if (value instanceof PointF) {
                    PointF pointF = (PointF) value;
                    posX = pointF.x + mHalfHeartWidth;
                    posY = pointF.y + mHalfHeartHeight;
                    bitmapMatrix.postTranslate(pointF.x, pointF.y);
                } else if (value instanceof Float) {
                    float scale = (float) value;
                    if (posX > 0 && posY > 0) {
                        bitmapMatrix.postScale(scale, scale, posX, posY);
                    }
                }
            }
            if (heartItem.heartIndex < 0 || heartItem.heartIndex >= mHeartImgs.length) {
                heartItem.heartIndex = 0;
            }
            Bitmap bitmap = mHeartImgs[heartItem.heartIndex];
            if (!bitmap.isRecycled()) {
                canvas.drawBitmap(mHeartImgs[heartItem.heartIndex], bitmapMatrix, bitmapPaint);
            }
        }
        postInvalidate();
    }

    public void setStartPointX(int mStartPointX) {
        this.mStartPointX = mStartPointX - mHeartWidth / 2;
    }

    public void setFixedHeartCanFloat(boolean mFixedHeartCanFloat) {
        this.mFixedHeartCanFloat = mFixedHeartCanFloat;
        postInvalidate();
    }

    /**
     * 回收已经结束动画的item
     */
    private void recycleHearts() {
        if (mHeartItems.isEmpty()) {
            return;
        }
        List<InnerHeartItem> recycleItems = new ArrayList<>();
        for (InnerHeartItem innerHeartItem : mHeartItems) {
            if (innerHeartItem.canRecycle) {
                recycleItems.add(innerHeartItem);
            }
        }
        for (InnerHeartItem recycleHeartItem : recycleItems) {
            mHeartItems.remove(recycleHeartItem);
        }
    }

    /**
     * 添加随机心
     */
    public void addHeartItem() {
        if (checkOutOfEdge()) {
            return;
        }
        InnerHeartItem innerHeartItem = buildHeartItem(false);
        innerHeartItem.animatorSet.start();
        mHeartItems.add(innerHeartItem);
    }

    private boolean checkOutOfEdge() {
        if (mWidth <= mHeartWidth || mHeight <= mHeartHeight) {
            return true;
        }
        return false;
    }

    private InnerHeartItem buildHeartItem(final boolean isLooper) {
        final InnerHeartItem innerHeartItem = new InnerHeartItem();
        innerHeartItem.heartIndex = mRandom.nextInt(mHeartImgs.length);
        //创建alpha动画
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0);
        alphaAnimator.setDuration(mAnimDuration);
        //创建scale动画
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0, 1.0f);
        scaleAnimator.setDuration(500);
        //创建path动画
        innerHeartItem.startPointF.x = mStartPointX;
        innerHeartItem.startPointF.y = mHeight - mHeartHeight;
        innerHeartItem.endPointF.x = mRandom.nextInt(mWidth - mHeartWidth);
        innerHeartItem.endPointF.y = 0;
        final ValueAnimator pathAnimator = ValueAnimator.ofObject(buildBezierEvaluator(), innerHeartItem.startPointF, innerHeartItem.endPointF);
        pathAnimator.setDuration(mAnimDuration);

        innerHeartItem.animatorSet.playTogether(alphaAnimator, pathAnimator, scaleAnimator);
        innerHeartItem.animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isLooper) {
                    innerHeartItem.canRecycle = true;
                } else {
                    innerHeartItem.heartIndex = mRandom.nextInt(mHeartImgs.length);
                    innerHeartItem.endPointF.x = mRandom.nextInt(mWidth - mHeartWidth);
                    pathAnimator.setEvaluator(buildBezierEvaluator());
                    innerHeartItem.animatorSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return innerHeartItem;
    }


    /**
     * 创建曲线BezierEvaluator
     */
    private BezierEvaluator buildBezierEvaluator() {
        PointF pointF1 = new PointF();
        pointF1.x = mRandom.nextInt((mWidth - mHeartWidth));
        pointF1.y = mRandom.nextInt((mHeight - mHeartHeight)) / 2;

        PointF pointF2 = new PointF();
        pointF2.x = mRandom.nextInt((mWidth - mHeartWidth));
        pointF2.y = mRandom.nextInt((mHeight - mHeartHeight));
        return new BezierEvaluator(pointF1, pointF2);
    }

    /**
     * 内部漂浮的心item
     */
    private class InnerHeartItem {
        //图片index
        int heartIndex;
        //动画
        AnimatorSet animatorSet = new AnimatorSet();
        //是否可以回收
        boolean canRecycle;
        //路径起始点
        PointF startPointF = new PointF();
        PointF endPointF = new PointF();
    }

    /**
     * bei曲
     */
    private class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF1;
        private PointF pointF2;

        public BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRecycleBitmap = true;
        for (Bitmap bitmap : mHeartImgs) {
            bitmap.recycle();
        }
        mFixedHeartItems.clear();
        mHeartItems.clear();
    }
}
