package com.felix.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  felix on 2016/11/10.
 * Company :IV-Tech
 * Website :www.iv-tech.com
 * 芝麻分趋势图
 */

public class ScoreTrend extends View
{
    private float viewWith;
    private float viewHeight;

    private float brokenLineWith = 0.5f;

    private int brokenLineColor   = 0xff02bbb7;
    private int straightLineColor = 0xffe2e2e2;//0xffeaeaea
    private int textNormalColor   = 0xff7e7e7e;

    private int maxScore = 700;
    private int minScore = 650;

    private int monthCount  = 6;
    private int selectMonth = 6;//选中的月份

    private String[] monthText = new String[]{"6月", "7月", "8月", "9月", "10月", "11月"};
    private int[]    score     = new int[]{660, 663, 669, 678, 682, 689};

    private List<Point> scorePoints;

    private int textSize = dipToPx(15);

    private Paint brokenPaint;
    private Paint straightPaint;
    private Paint dottedPaint;
    private Paint textPaint;

    private Path brokenPath;

    public ScoreTrend(Context context)
    {
        super(context);
        initConfig(context,null);
        init();
    }

    public ScoreTrend(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initConfig(context,attrs);
        init();
    }

    public ScoreTrend(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initConfig(context,attrs);
        init();

    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initConfig(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoreTrend);

        maxScore=a.getInt(R.styleable.ScoreTrend_max_score,700);
        minScore=a.getInt(R.styleable.ScoreTrend_min_score,650);
        brokenLineColor=a.getColor(R.styleable.ScoreTrend_broken_line_color,brokenLineColor);

        a.recycle();

    }

    private void init()
    {
        brokenPath = new Path();

        brokenPaint = new Paint();
        brokenPaint.setAntiAlias(true);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPaint.setStrokeWidth(dipToPx(brokenLineWith));
        brokenPaint.setStrokeCap(Paint.Cap.ROUND);

        straightPaint = new Paint();
        straightPaint.setAntiAlias(true);
        straightPaint.setStyle(Paint.Style.STROKE);
        straightPaint.setStrokeWidth(brokenLineWith);
        straightPaint.setColor((straightLineColor));
        straightPaint.setStrokeCap(Paint.Cap.ROUND);

        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeWidth(brokenLineWith);
        dottedPaint.setColor((straightLineColor));
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor((textNormalColor));
        textPaint.setTextSize(dipToPx(15));


    }

    private void initData()
    {
        scorePoints = new ArrayList<>();
        float maxScoreYCoordinate = viewHeight * 0.15f;
        float minScoreYCoordinate = viewHeight * 0.4f;

        Log.v("ScoreTrend", "initData: " + maxScoreYCoordinate);

        float newWith = viewWith - (viewWith * 0.15f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        int   coordinateX;

        for(int i = 0; i < score.length; i++)
        {
            Log.v("ScoreTrend", "initData: " + score[i]);
            Point point = new Point();
            coordinateX = (int) (newWith * ((float) (i) / (monthCount - 1)) + (viewWith * 0.15f));
            point.x = coordinateX;
            if(score[i] > maxScore)
            {
                score[i] = maxScore;
            }
            else if(score[i] < minScore)
            {
                score[i] = minScore;
            }
            point.y = (int) (((float) (maxScore - score[i]) / (maxScore - minScore)) * (minScoreYCoordinate - maxScoreYCoordinate) + maxScoreYCoordinate);
            scorePoints.add(point);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWith = w;
        viewHeight = h;
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 0.15f, viewWith, viewHeight * 0.15f);
        drawDottedLine(canvas, viewWith * 0.15f, viewHeight * 0.4f, viewWith, viewHeight * 0.4f);
        drawText(canvas);
        drawMonthLine(canvas);
        drawBrokenLine(canvas);
        drawPoint(canvas);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        this.getParent().requestDisallowInterceptTouchEvent(true);//一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                onActionUpEvent(event);
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    private void onActionUpEvent(MotionEvent event)
    {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());

        if(isValidTouch)
        {
            invalidate();
        }
    }

    //是否是有效的触摸范围
    private boolean validateTouch(float x, float y)
    {

        //曲线触摸区域
        for(int i = 0; i < scorePoints.size(); i++)
        {
            // dipToPx(8)乘以2为了适当增大触摸面积
            if(x > (scorePoints.get(i).x - dipToPx(8) * 2) && x < (scorePoints.get(i).x + dipToPx(8) * 2))
            {
                if(y > (scorePoints.get(i).y - dipToPx(8) * 2) && y < (scorePoints.get(i).y + dipToPx(8) * 2))
                {
                    selectMonth = i + 1;
                    return true;
                }
            }
        }

        //月份触摸区域
        //计算每个月份X坐标的中心点
        float monthTouchY = viewHeight * 0.7f - dipToPx(3);//减去dipToPx(3)增大触摸面积

        float newWith       = viewWith - (viewWith * 0.15f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float validTouchX[] = new float[monthText.length];
        for(int i = 0; i < monthText.length; i++)
        {
            validTouchX[i] = newWith * ((float) (i) / (monthCount - 1)) + (viewWith * 0.15f);
        }

        if(y > monthTouchY)
        {
            for(int i = 0; i < validTouchX.length; i++)
            {
                Log.v("ScoreTrend", "validateTouch: validTouchX:" + validTouchX[i]);
                if(x < validTouchX[i] + dipToPx(8) && x > validTouchX[i] - dipToPx(8))
                {
                    Log.v("ScoreTrend", "validateTouch: " + (i + 1));
                    selectMonth = i + 1;
                    return true;
                }
            }
        }

        return false;
    }


    //绘制折线穿过的点
    protected void drawPoint(Canvas canvas)
    {
        if(scorePoints == null)
        {
            return;
        }
        brokenPaint.setStrokeWidth(dipToPx(1));
        for(int i = 0; i < scorePoints.size(); i++)
        {
            brokenPaint.setColor(brokenLineColor);
            brokenPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(3), brokenPaint);
            brokenPaint.setColor(Color.WHITE);
            brokenPaint.setStyle(Paint.Style.FILL);
            if(i == selectMonth - 1)
            {
                brokenPaint.setColor(0xffd0f3f2);
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(8f), brokenPaint);
                brokenPaint.setColor(0xff81dddb);
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(5f), brokenPaint);

                //绘制浮动文本背景框
                drawFloatTextBackground(canvas, scorePoints.get(i).x, scorePoints.get(i).y - dipToPx(8f));

                textPaint.setColor(0xffffffff);
                //绘制浮动文字
                canvas.drawText(String.valueOf(score[i]), scorePoints.get(i).x, scorePoints.get(i).y - dipToPx(5f) - textSize, textPaint);
            }
            brokenPaint.setColor(0xffffffff);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(1.5f), brokenPaint);
            brokenPaint.setStyle(Paint.Style.STROKE);
            brokenPaint.setColor(brokenLineColor);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(2.5f), brokenPaint);
        }
    }

    //绘制月份的直线(包括刻度)
    private void drawMonthLine(Canvas canvas)
    {
        straightPaint.setStrokeWidth(dipToPx(1));
        canvas.drawLine(0, viewHeight * 0.7f, viewWith, viewHeight * 0.7f, straightPaint);

        float newWith = viewWith - (viewWith * 0.15f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float coordinateX;//分隔线X坐标
        for(int i = 0; i < monthCount; i++)
        {
            coordinateX = newWith * ((float) (i) / (monthCount - 1)) + (viewWith * 0.15f);
            canvas.drawLine(coordinateX, viewHeight * 0.7f, coordinateX, viewHeight * 0.7f + dipToPx(4), straightPaint);
        }
    }

    //绘制折线
    private void drawBrokenLine(Canvas canvas)
    {
        brokenPath.reset();
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStyle(Paint.Style.STROKE);
        if(score.length == 0)
        {
            return;
        }
        Log.v("ScoreTrend", "drawBrokenLine: " + scorePoints.get(0));
        brokenPath.moveTo(scorePoints.get(0).x, scorePoints.get(0).y);
        for(int i = 0; i < scorePoints.size(); i++)
        {
            brokenPath.lineTo(scorePoints.get(i).x, scorePoints.get(i).y);
        }
        canvas.drawPath(brokenPath, brokenPaint);

    }

    //绘制文本
    private void drawText(Canvas canvas)
    {
        textPaint.setTextSize(dipToPx(12));
        textPaint.setColor(textNormalColor);

        canvas.drawText(String.valueOf(maxScore), viewWith * 0.1f - dipToPx(10), viewHeight * 0.15f + textSize * 0.25f, textPaint);
        canvas.drawText(String.valueOf(minScore), viewWith * 0.1f - dipToPx(10), viewHeight * 0.4f + textSize * 0.25f, textPaint);

        textPaint.setColor(0xff7c7c7c);

        float newWith = viewWith - (viewWith * 0.15f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float coordinateX;//分隔线X坐标
        textPaint.setTextSize(dipToPx(12));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textNormalColor);
        textSize = (int) textPaint.getTextSize();
        for(int i = 0; i < monthText.length; i++)
        {
            coordinateX = newWith * ((float) (i) / (monthCount - 1)) + (viewWith * 0.15f);

            if(i == selectMonth - 1)
            {

                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setColor(brokenLineColor);
                RectF r2 = new RectF();
                r2.left = coordinateX - textSize - dipToPx(4);
                r2.top = viewHeight * 0.7f + dipToPx(4) + textSize / 2;
                r2.right = coordinateX + textSize + dipToPx(4);
                r2.bottom = viewHeight * 0.7f + dipToPx(4) + textSize + dipToPx(8);
                canvas.drawRoundRect(r2, 10, 10, textPaint);

            }
            //绘制月份
            canvas.drawText(monthText[i], coordinateX, viewHeight * 0.7f + dipToPx(4) + textSize + dipToPx(5), textPaint);

            textPaint.setColor(textNormalColor);

        }

    }

    //绘制显示浮动文字的背景
    private void drawFloatTextBackground(Canvas canvas, int x, int y)
    {
        brokenPath.reset();
        brokenPaint.setColor(brokenLineColor);
        brokenPaint.setStyle(Paint.Style.FILL);

        //P1
        Point point = new Point(x, y);
        brokenPath.moveTo(point.x, point.y);

        //P2
        point.x = point.x + dipToPx(5);
        point.y = point.y - dipToPx(5);
        brokenPath.lineTo(point.x, point.y);

        //P3
        point.x = point.x + dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //P4
        point.y = point.y - dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P5
        point.x = point.x - dipToPx(34);
        brokenPath.lineTo(point.x, point.y);

        //P6
        point.y = point.y + dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P7
        point.x = point.x + dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //最后一个点连接到第一个点
        brokenPath.lineTo(x, y);

        canvas.drawPath(brokenPath, brokenPaint);
    }

    /**
     * 画虚线
     *
     * @param canvas 画布
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终点X坐标
     * @param stopY  终点Y坐标
     */
    private void drawDottedLine(Canvas canvas, float startX, float startY, float stopX, float stopY)
    {
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 4));
        dottedPaint.setStrokeWidth(1);
        // 实例化路径
        Path mPath = new Path();
        mPath.reset();
        // 定义路径的起点
        mPath.moveTo(startX, startY);
        mPath.lineTo(stopX, stopY);
        canvas.drawPath(mPath, dottedPaint);

    }


    public int[] getScore()
    {
        return score;
    }

    public void setScore(int[] score)
    {
        this.score = score;
        initData();
    }

    public void setMaxScore(int maxScore)
    {
        this.maxScore = maxScore;
    }

    public void setMinScore(int minScore)
    {
        this.minScore = minScore;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

}
