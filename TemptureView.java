package com.veepoo.hband.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.veepoo.hband.R;
import com.veepoo.hband.util.DpUtil;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by Administrator on 2015/8/7.
 */
public class TemptureView extends View {
    private final static String TAG = "TemptureView";
    int width, height;
    private Paint mPaint, mPaintRect, mPainbottom, mPaintContent;
    private Paint mPaintDrawRoundRect;
    private Paint mPaintLine, mPaintDashLine;
    private Paint mPaintLeftText, mPaintBottomText, mPaintMarkValueTextTop, mPaintMarkValueTextBottom;
    float leftRectWidth = 0;
    float bottomRectHeight = 0;
    float paddingLeft = 0;
    float paddingRight = 0;
    float paddingTop = 0;
    float paddingBottomLine = 0;
    float paddingMarkRect = 0;
    float textSizeLeft, textSizeBottom = 0;
    RectF leftRect, bottomRect, contentRect;
    RectF[] bottomTimeRectArr;
    RectF[] leftValueRectArr;
    RectF[] contentRectArr;
    Rect mLeftTextBoundRect = new Rect();
    Rect mBottomTextBoundRect = new Rect();
    Rect mContentTextBoundRectTop = new Rect();
    Rect mContentTextBoundRectBottom = new Rect();
    int MAX = 40;
    int MIN = 35;
    int DEFAULT = 37;
    String[] timeStr12 = new String[]{"12:00 am", "06:00 am", "12:00 pm", "06:00 pm", "12:00 am", ""};
    String[] timeStr24 = new String[]{"00:00", "06:00", "12:00", "18:00", "24:00", ""};
    boolean isDebug = false;
    int temptureValueCount = 48;
    float[] temptureMax = new float[temptureValueCount];
    float[] temptureMin = new float[temptureValueCount];
    float onTemptureValueWidth;
    boolean isModle24 = false;

    /**
     * 第一个构造方法是提供给我们在代码中生成控件使用的
     *
     * @param context
     */
    public TemptureView(Context context) {
        this(context, null);

    }

    /**
     * 第二个方法是在XML布局文件中插入控件
     *
     * @param context
     * @param attributeSet XML中为TextView声明的属性集
     */
    public TemptureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        leftRectWidth = DpUtil.getDpVlaue(context, 15);
        bottomRectHeight = DpUtil.getDpVlaue(context, 10);
        paddingTop = DpUtil.getDpVlaue(context, 10);
        paddingBottomLine = DpUtil.getDpVlaue(context, 10);
        paddingLeft = DpUtil.getDpVlaue(context, 5);
        paddingRight = DpUtil.getDpVlaue(context, 15);
        textSizeLeft = DpUtil.getSpVlaue(context, 12);
        textSizeBottom = DpUtil.getSpVlaue(context, 6);
        paddingMarkRect = DpUtil.getDpVlaue(context, 6);
        initPaint();
        initRandowValue();
    }

    boolean isHandlerOnTouchEvent = true;
    boolean isDrawLeftValue = true;
    boolean isDrawDashLine = true;
    boolean isDrawBottomLine = true;

    public void setHandlerOnTouchEvent(boolean isHandlerOnTouchEvent) {
        this.isHandlerOnTouchEvent = isHandlerOnTouchEvent;
    }

    public void setDrawDashLine(boolean isDrawDashLine) {
        this.isDrawDashLine = isDrawDashLine;
    }

    public void setDrawLeftValue(boolean drawLeftValue) {
        isDrawLeftValue = drawLeftValue;
    }

    public void setDrawBottomLine(boolean drawBottomLine) {
        isDrawBottomLine = drawBottomLine;
    }

    public void setThemeColor(int themeColor) {
        this.themColor = themeColor;
        mPaintLine.setColor(themColor);
        mPaintDashLine.setColor(themColor);
        mPaintLeftText.setColor(themColor);
        mPaintBottomText.setColor(themColor);
        mPaintDrawRoundRect.setColor(themColor);
    }

    private void initRandowValue() {
        for (int i = 0; i < temptureValueCount; i++) {
            float tempture1 = new Random().nextFloat() + new Random().nextInt(1) + 36.5f;
            float tempture2 = new Random().nextFloat() + new Random().nextInt(1) + 36.5f;
            float temptureHigh = tempture1 > tempture2 ? tempture1 : tempture2;
            float temptureLow = tempture1 > tempture2 ? tempture2 : tempture1;
            temptureMax[i] = getPositionFloat(temptureHigh, 1);
            temptureMin[i] = getPositionFloat(temptureLow, 1);
        }
    }

    public static float getPositionFloat(float value, int position) {
        BigDecimal bigObject = new BigDecimal(value);
        float v = bigObject.setScale(position, BigDecimal.ROUND_DOWN).floatValue();
        return v;
    }

    int themColor = 0;

    private void initPaint() {
        themColor = getResources().getColor(R.color.white);
        int color = getResources().getColor(R.color.app_color_helper_one);
        int colortv = getResources().getColor(R.color.app_color_helper_two);
        int ftgtv = getResources().getColor(R.color.app_color_helper_ftg);
        int colorhrv = getResources().getColor(R.color.app_color_helper_hrv);

        /**
         * 绘制底部的横线
         */
        mPaintLine = new Paint();
        mPaintLine.setColor(themColor);
        mPaintLine.setStyle(Paint.Style.FILL);
        mPaintLine.setStrokeWidth(1);
        mPaintLine.setStrokeCap(Cap.SQUARE);
        mPaintLine.setAntiAlias(true);

        /**
         * 绘制虚线
         */
        mPaintDashLine = new Paint();
        mPaintDashLine.setStrokeCap(Paint.Cap.ROUND);
        mPaintDashLine.setAntiAlias(true);
        mPaintDashLine.setStyle(Paint.Style.STROKE);
        mPaintDashLine.setColor(themColor);
        mPaintDashLine.setStrokeWidth(3);
        mPaintDashLine.setPathEffect(new DashPathEffect(new float[]{10, 8, 10, 8}, 0));// 这时偏移为0，先绘制实线，再绘制透明。

        /**
         * 绘制左边的文本
         */
        mPaintLeftText = new Paint();
        mPaintLeftText.setColor(themColor);
        mPaintLeftText.setColor(Color.WHITE);
        mPaintLeftText.setAntiAlias(true);
        mPaintLeftText.setStyle(Paint.Style.FILL);
        mPaintLeftText.setTextSize(textSizeLeft);
        mPaintLeftText.setTextAlign(Paint.Align.CENTER);
        String mStrVlaue = "12";
        mPaintLeftText.getTextBounds(mStrVlaue, 0, mStrVlaue.length(), mLeftTextBoundRect);

        mPaintMarkValueTextTop = new Paint();
        mPaintMarkValueTextTop.setColor(themColor);
        mPaintMarkValueTextTop.setColor(Color.WHITE);
        mPaintMarkValueTextTop.setAntiAlias(true);
        mPaintMarkValueTextTop.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintMarkValueTextTop.setTextSize(textSizeLeft);
        mPaintMarkValueTextTop.setTextAlign(Paint.Align.CENTER);
        String temptureStr = "36.2-36.8°C";
        mPaintMarkValueTextTop.getTextBounds(temptureStr, 0, temptureStr.length(), mContentTextBoundRectTop);


        mPaintMarkValueTextBottom = new Paint();
        mPaintMarkValueTextBottom.setColor(themColor);
        mPaintMarkValueTextBottom.setColor(Color.WHITE);
        mPaintMarkValueTextBottom.setAntiAlias(true);
        mPaintMarkValueTextBottom.setStyle(Paint.Style.FILL);
        mPaintMarkValueTextBottom.setTextSize(textSizeLeft);
        mPaintMarkValueTextBottom.setTextAlign(Paint.Align.CENTER);
        String time12 = "12:00-12:00 am";
        String time24 = "12:00-12:00";
        if (isModle24) {
            mPaintMarkValueTextBottom.getTextBounds(time24, 0, time24.length(), mContentTextBoundRectBottom);
        } else {
            mPaintMarkValueTextBottom.getTextBounds(time12, 0, time12.length(), mContentTextBoundRectBottom);
        }


        /**
         * 绘制下边的文本
         */
        mPaintBottomText = new Paint();
        mPaintBottomText.setColor(themColor);
        mPaintBottomText.setColor(Color.WHITE);
        mPaintBottomText.setAntiAlias(true);
        mPaintBottomText.setTextAlign(Paint.Align.RIGHT);
        mPaintBottomText.setStyle(Paint.Style.FILL);
        mPaintBottomText.setTextSize(textSizeBottom);
        String mStrVlaue2 = "12:00 am";
        mPaintBottomText.getTextBounds(mStrVlaue2, 0, mStrVlaue2.length(), mBottomTextBoundRect);

        /**
         * 绘制柱形
         */
        mPaintDrawRoundRect = new Paint();
        mPaintDrawRoundRect.setColor(themColor);
        mPaintDrawRoundRect.setStrokeCap(Cap.ROUND);
        mPaintDrawRoundRect.setStrokeWidth(5);
        mPaintDrawRoundRect.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setStrokeCap(Cap.SQUARE);
        mPaint.setAntiAlias(true);

        mPaintRect = new Paint();
        mPaintRect.setColor(colortv);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setStrokeCap(Cap.SQUARE);
        mPaintRect.setStrokeWidth(1);
        mPaintRect.setAntiAlias(true);


        mPainbottom = new Paint();
        mPainbottom.setColor(ftgtv);
        mPainbottom.setStyle(Paint.Style.STROKE);
        mPainbottom.setStrokeCap(Cap.SQUARE);
        mPainbottom.setStrokeWidth(1);
        mPainbottom.setAntiAlias(true);

        mPaintContent = new Paint();
        mPaintContent.setColor(colorhrv);
        mPaintContent.setStyle(Paint.Style.STROKE);
        mPaintContent.setStrokeCap(Cap.SQUARE);
        mPaintContent.setStrokeWidth(1);
        mPaintContent.setAntiAlias(true);


    }

    /**
     * 第三个方法的第三个参数defStyleAttr的意义是从APP或者Activity的Theme中设置的该控件的属性的默认值，
     *
     * @param context
     * @param attrs
     * @param defStyleAttr attr属性值。
     */
    public TemptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        Logger.t(TAG).i("onMeasure width=" + width);
        leftRect = new RectF(0, 0, leftRectWidth, height - bottomRectHeight);
        contentRect = new RectF(leftRectWidth + paddingLeft, paddingTop, width - paddingRight, height - bottomRectHeight - paddingBottomLine);
        bottomRect = new RectF(leftRectWidth + paddingLeft, height - bottomRectHeight, width - paddingRight, height);

        onTemptureValueWidth = contentRect.width() * 1f / 47;

        bottomTimeRectArr = getBottomTimeRectArr();
        leftValueRectArr = getLeftRectArr();
        contentRectArr = getContentRectArr();

        Logger.t(TAG).i("contentRect.left=" + contentRect.left + ",contentRect.right=" + contentRect.right + ",onTemptureValueWidth=" + onTemptureValueWidth);
    }


    int markViewIndex = -1;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw defalut dashline
        if (isDrawDashLine) {
            float pointy = getPointy(DEFAULT);
            canvas.drawLine(leftRect.right + paddingLeft / 2, pointy, bottomRect.right, pointy, mPaintDashLine);
        }

        if (isDebug) {
            canvas.drawRect(new Rect(0, 0, width, height), mPaint);
//            canvas.drawRect(leftRect, mPaintRect);
            canvas.drawRect(contentRect, mPaintContent);
            canvas.drawRect(bottomRect, mPainbottom);
        }


        for (int i = 0; i < temptureValueCount; i++) {
            float x = contentRect.left + onTemptureValueWidth * i;
            RectF rectF = getValueRoundRect(x, temptureMax[i], temptureMin[i]);
            canvas.drawRoundRect(rectF, 5f, 5f, mPaintDrawRoundRect);

        }


        for (int i = 0; i < bottomTimeRectArr.length; i++) {
            RectF rect = bottomTimeRectArr[i];
            if (isDebug) {
                canvas.drawRect(rect, mPaintRect);
            }
            int width = mBottomTextBoundRect.width();
            drawStr(canvas, timeStr12[i], mPaintBottomText, rect.left + width / 2, rect.centerY());
        }

        if (isDrawLeftValue) {
            for (int i = 0; i < leftValueRectArr.length; i++) {
                RectF rect = leftValueRectArr[i];
                if (isDebug) {
                    canvas.drawRect(rect, mPaintRect);
                }
                drawStr(canvas, rect, String.valueOf(MIN + i), mPaintLeftText);
            }
        }

        //draw markview 以竖线为基准
        if (markViewIndex != -1) {
            RectF rectF = contentRectArr[markViewIndex];
            float stopY = rectF.top + rectF.height() / 4;
            canvas.drawLine(rectF.centerX(), contentRect.bottom, rectF.centerX(), stopY, mPaintLine);
            String markTimeStr = getMarkTimeStr(markViewIndex);

            float rectWidth = 0;
            if (mContentTextBoundRectBottom.width() > mContentTextBoundRectTop.width()) {
                rectWidth = mContentTextBoundRectBottom.width() + paddingMarkRect * 2;
            } else {
                rectWidth = mContentTextBoundRectTop.width() + paddingMarkRect * 2;
            }
            float markViewBottomHeight = mContentTextBoundRectBottom.height() + paddingMarkRect * 2;
            float markViewTopHeight = mContentTextBoundRectTop.height() + paddingMarkRect * 2;

            float halfBound = rectWidth / 2;
            float px =0;
            if (rectF.centerX() - halfBound < contentRect.left) {
                px = contentRect.left;
            } else if (rectF.centerX() + halfBound > contentRect.right) {
                px = contentRect.right - rectWidth;
            } else {
                px = rectF.centerX() - halfBound;
            }

            RectF bottomMarkView = new RectF(px, stopY - markViewBottomHeight, px + rectWidth, stopY);
            canvas.drawRoundRect(bottomMarkView, 3f, 3f, mPaintRect);
            drawStr(canvas, bottomMarkView, markTimeStr, mPaintMarkValueTextBottom);

            RectF topMarkView = new RectF(bottomMarkView.left, bottomMarkView.top - markViewTopHeight, bottomMarkView.right, bottomMarkView.top);
            canvas.drawRoundRect(topMarkView, 3f, 3f, mPaintRect);
            drawStr(canvas, topMarkView, getMarkValueStr(markViewIndex), mPaintMarkValueTextTop);

        }

        if (isDrawBottomLine) {
            canvas.drawLine(leftRect.right + paddingLeft / 2, bottomRect.top, bottomRect.right, bottomRect.top, mPaintLine);
        }
    }

    private RectF getValueRoundRect(float x, float temptureHigh, float temptureLow) {
        if (temptureLow == temptureHigh) {
            temptureHigh = temptureLow + 0.1f;
        }
        float y1 = getPointy(temptureHigh);
        float y2 = getPointy(temptureLow);
        RectF rectF = new RectF(x, y1, x + onTemptureValueWidth / 2, y2);
        return rectF;

    }

    private String getMarkValueStr(int markViewIndex) {
        return temptureMin[markViewIndex] + "-" + temptureMax[markViewIndex] + "°C";
    }

    private String getMarkTimeStr(int markViewIndex) {
        if (isModle24) {
            return getTimeStr(markViewIndex) + "-" + getTimeStr(markViewIndex + 1);
        } else {
            String unit = "am";
            return getTimeStr(markViewIndex) + "-" + getTimeStr(markViewIndex + 1) + " " + unit;
        }
    }

    private String getTimeStr(int markViewIndex) {
        int hour = markViewIndex / 2;
        int minute = markViewIndex % 2;
        return getTwoStr(hour) + ":" + getTwoStr(minute * 30);
    }

    private String getTwoStr(int value) {
        if (value <= 9) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }

    /**
     * draw 时间文本
     *
     * @param canvas
     * @param rectF
     * @param text
     */
    private void drawStr(Canvas canvas, RectF rectF, String text, Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseline = rectF.centerY() - (fontMetrics.bottom + fontMetrics.top) / 2;
        canvas.drawText(text, rectF.centerX(), baseline, paint);
    }

    private void drawStr(Canvas canvas, String text, Paint paint, float x, float y) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseline = y - (fontMetrics.bottom + fontMetrics.top) / 2;
        canvas.drawText(text, x, baseline, paint);
    }

    /**
     * 单个温度item
     *
     * @return
     */
    private RectF[] getContentRectArr() {
        RectF[] rectFArr = new RectF[temptureValueCount];
        for (int i = 0; i < temptureValueCount; i++) {
            float x = contentRect.left + onTemptureValueWidth * i;
            rectFArr[i] = new RectF(x, contentRect.top, x + onTemptureValueWidth / 2, contentRect.bottom);
        }
        return rectFArr;
    }

    private RectF[] getLeftRectArr() {
        int leftDot = MAX - MIN + 1;//(30-39,g一共10个点)
        float oneValueHeight = leftRect.height() * 1f / (MAX - MIN);//分9块区域
        RectF[] rectF = new RectF[leftDot];
        for (int i = MIN; i <= MAX; i++) {
            float pointy = getPointy(i);
            int index = i - MIN;
            //对应坐标在rect的中间
            rectF[index] = new RectF(leftRect.left, pointy + oneValueHeight / 2, leftRect.right, pointy - oneValueHeight / 2);
        }
        return rectF;
    }


    private RectF[] getBottomTimeRectArr() {
        int bottomCount = 5;
        RectF[] rectF = new RectF[bottomCount];
        float onTemptureWidth = contentRect.width() * 1f / (bottomCount - 1);
        for (int i = 0; i < bottomCount; i++) {
            float leftTopX = bottomRect.left + i * onTemptureWidth;
            float leftTopY = bottomRect.top;
            float rightBottomX = bottomRect.left + (i + 1) * onTemptureWidth;
            float rightBottomY = bottomRect.bottom;
            rectF[i] = new RectF(leftTopX, leftTopY, rightBottomX, rightBottomY);
        }
        return rectF;
    }

    float getPointy(float vlaue) {
        return Math.abs(vlaue - MAX) / (MAX - MIN) * contentRect.height() + paddingTop;
    }


    /*
     * 事件分发, 请求父控件及祖宗控件是否拦截事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);// 用getParent去请求
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);// 用getParent去请求
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isHandlerOnTouchEvent) {
            return super.onTouchEvent(event);
        }
        //getX     内容布局的位置
        //getRawX 相当于屏幕的位置
        int action = event.getAction();
        float rawX = event.getX();
        float rawY = event.getY();
//        Logger.t(TAG).i("rawY=" + rawY + ",contentRect.top=" + contentRect.top + ",contentRect.bottom=" + contentRect.bottom);

        //如果要控制只能点击在布局内部
        if (rawY > contentRect.top && rawY < contentRect.bottom) {
        } else {
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
                markViewIndex = -1;
                break;
            case MotionEvent.ACTION_DOWN:
                markViewIndex = getIndex(rawX);
                break;
            case MotionEvent.ACTION_MOVE:
                markViewIndex = getIndex(rawX);
                break;
        }
        postInvalidate();
        return true;
    }

    private int getIndex(float rawX) {

        Logger.t(TAG).i("rawX=" + rawX + ",index=0");
        rawX = rawX <= contentRect.left ? contentRect.left : rawX;
        rawX = rawX >= contentRect.right ? contentRect.right : rawX;
        int index = (int) ((rawX - contentRect.left) / onTemptureValueWidth);
        Logger.t(TAG).i("rawX=" + rawX + ",index=" + index);
        return index;
    }


}
