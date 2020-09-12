package com.example.gesturerolldemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;


/**
 * Created by jianjiu on 2020/9/7
 * 邮箱：244401689@qq.com
 */

public class GestureRollView extends View {

    private Paint cruveLinePaint;  //曲线画笔
    private Paint circlePaint;      // 小白圆画笔
    private Paint textPaint;        // 时间数字画笔
    private int lineWidth;
    private int lineColor;
    private float circleCenterX;      //圆心X坐标
    private float circleCenterY;      //圆心Y坐标
    private int circleRadius = 30;              //圆的半径
    private Path linePath = new Path();  //小圆上面的曲线path
    private int viewWidth;
    private float timeTextX1, timeTextX2, timeTextX3, timeTextX4, timeTextX5;   //时间text的X坐标

    public GestureRollView(Context context) {
        this(context, null);
    }

    public GestureRollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureRollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GestureRollView);
        lineWidth = typedArray.getDimensionPixelOffset(R.styleable.GestureRollView_line_width, 1);
        lineColor = typedArray.getColor(R.styleable.GestureRollView_line_color, 0Xffffffff);
        init();
    }

    private void init() {
        cruveLinePaint = new Paint();
        cruveLinePaint.setStyle(Paint.Style.FILL);
        cruveLinePaint.setAntiAlias(true);
        cruveLinePaint.setStrokeWidth(dp2px(lineWidth));
        cruveLinePaint.setColor(lineColor);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        cruveLinePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(1);
        circlePaint.setColor(0xffffffff);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(sp2px(10));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0xff666666);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        circleCenterX = MeasureSpec.getSize(widthMeasureSpec) / 2;
        circleCenterY = MeasureSpec.getSize(heightMeasureSpec) - circleRadius * 2;
        timeTextX1 = circleRadius * 3;
        timeTextX2 = (viewWidth / 2 + circleRadius * 3) / 2;
        timeTextX3 = viewWidth / 2;
        timeTextX4 = (viewWidth - circleRadius * 3 + viewWidth / 2) / 2;
        timeTextX5 = viewWidth - circleRadius * 3;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, circlePaint);

        drawLine(canvas);

        drawText(canvas);
    }

    /**
     * 绘制时间
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        doDrawText(canvas, "09/07", "20:00", timeTextX1);
        doDrawText(canvas, null, "02:00", timeTextX2);
        doDrawText(canvas, null, "08:00", timeTextX3);
        doDrawText(canvas, null, "14:00", timeTextX4);
        doDrawText(canvas, "09/08", "20:00", timeTextX5);
    }


    /**
     * 绘制时间随手势滚轮上下移动逻辑
     *
     * @param canvas
     * @param date
     * @param time
     * @param timeTextX
     */
    private void doDrawText(Canvas canvas, String date, String time, float timeTextX) {
        if (Math.abs(circleCenterX - timeTextX) < 50) {
            canvas.drawText(time, timeTextX, circleCenterY - circleRadius - 25, textPaint);
            if (date == null)
                return;
            canvas.drawText(date, timeTextX, circleCenterY - circleRadius - 53, textPaint);
        } else if (Math.abs(circleCenterX - timeTextX) > 110) {
            canvas.drawText(time, timeTextX, circleCenterY - 10, textPaint);
            if (date == null)
                return;
            canvas.drawText(date, timeTextX, circleCenterY - 38, textPaint);
        } else {
            canvas.drawText(time, timeTextX, (circleRadius + 15) * (Math.abs(circleCenterX - timeTextX) - 50) / 60 + (circleCenterY - circleRadius - 25), textPaint);
            if (date == null)
                return;
            canvas.drawText(date, timeTextX, (circleRadius + 15) * (Math.abs(circleCenterX - timeTextX) - 50) / 60 + (circleCenterY - circleRadius - 53), textPaint);
        }
    }

    /**
     * 三阶贝塞尔曲线绘制滚轮上线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        linePath.reset();
        linePath.moveTo(0, circleCenterY);
        linePath.lineTo(circleCenterX - circleRadius - 60, circleCenterY);
        //一开始二阶贝塞尔曲线的绘制
//        linePath.quadTo(circleCenterX, circleCenterY - circleRadius - 50, circleCenterX + circleRadius + 15, circleCenterY);
        linePath.cubicTo(circleCenterX - circleRadius, circleCenterY, circleCenterX - circleRadius - 5
                , circleCenterY - circleRadius - 10, circleCenterX, circleCenterY - circleRadius - 10);
        linePath.cubicTo(circleCenterX + circleRadius + 5, circleCenterY - circleRadius - 10
                , circleCenterX + circleRadius, circleCenterY, circleCenterX + circleRadius + 60, circleCenterY);
        linePath.lineTo(viewWidth, circleCenterY);
        linePath.lineTo(viewWidth, 0);
        linePath.lineTo(0, 0);
        linePath.close();
        canvas.drawPath(linePath, cruveLinePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < circleRadius || event.getX() > viewWidth - circleRadius)
                    return super.onTouchEvent(event);
                circleCenterX = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() < viewWidth - circleRadius && event.getX() > circleRadius) {
                    circleCenterX = event.getX();
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

}

