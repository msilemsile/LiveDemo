package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import me.msile.train.livedemo.R;

/**
 * 自定义的可以设置背景shape的TextView
 */
public class ShapeTextView extends TextView {

    protected int radius;
    protected int bottomLeftRadius;
    protected int bottomRightRadius;
    protected int topLeftRadius;
    protected int topRightRadius;
    protected int solidColor;
    protected int strokeColor;
    protected int strokeWidth;
    protected int dashWidth;
    protected int dashGap;
    private boolean needShapeBg = true;  //是否需要圆角背景

    public ShapeTextView(Context context) {
        super(context);
        readStyleParameters(context, null);
    }

    public ShapeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readStyleParameters(context, attrs);
    }

    public ShapeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readStyleParameters(context, attrs);
    }

    protected void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ShapeTextView);
        try {
            radius = a.getDimensionPixelSize(R.styleable.ShapeTextView_radiusT, 0);
            bottomLeftRadius = a.getDimensionPixelSize(R.styleable.ShapeTextView_bottomLeftRadiusT, 0);
            bottomRightRadius = a.getDimensionPixelSize(R.styleable.ShapeTextView_bottomRightRadiusT, 0);
            topLeftRadius = a.getDimensionPixelSize(R.styleable.ShapeTextView_topLeftRadiusT, 0);
            topRightRadius = a.getDimensionPixelSize(R.styleable.ShapeTextView_topRightRadiusT, 0);
            solidColor = a.getColor(R.styleable.ShapeTextView_solidColorT, Color.TRANSPARENT);
            strokeColor = a.getColor(R.styleable.ShapeTextView_strokeColorT, Color.TRANSPARENT);
            strokeWidth = a.getDimensionPixelSize(R.styleable.ShapeTextView_strokeWidthT, 0);
            dashWidth = a.getDimensionPixelSize(R.styleable.ShapeTextView_dashWidthT, 0);
            dashGap = a.getDimensionPixelSize(R.styleable.ShapeTextView_dashGapT, 0);
        } finally {
            a.recycle();
        }
    }

    protected void drawDrawable(Canvas canvas) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        if (radius != 0) {
            gradientDrawable.setCornerRadius(radius);
        } else {
            gradientDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
        }
        gradientDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
        gradientDrawable.draw(canvas);
    }

    public void setNeedShapeBg(boolean needShapeBg) {
        this.needShapeBg = needShapeBg;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setRadius(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
    }

    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
    }

    public void setDashGap(int dashGap) {
        this.dashGap = dashGap;
    }

    public void setDashWidth(int dashWidth) {
        this.dashWidth = dashWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needShapeBg) {
            drawDrawable(canvas);
        }
        super.onDraw(canvas);
    }
}
