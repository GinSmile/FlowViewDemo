package com.ginsmile.flowviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujin on 16/08/17.
 */
public class FlowView extends View {
    private int mDoneNums = 3;//完成的个数
    private int mFlowNums = 5;//总个数
    private List<String> labels = new ArrayList<>();

    private float mRadius;//指示器圆形的半径
    private float mPaddingCircle;//圆圈的padding,从圆圈的边界开始
    private float mCenterY;//圆心的Y坐标
    private List<Float> mCenterXPosition = new ArrayList<>();//圆心的X坐标的列表

    private float mLineHeight;//线段的高度
    private float mLeftX,mLeftY,mRightX,mRightY;//线段为细长的矩形，mLeftX,mLeftY为矩形左上角位置，mRightX, mRightY为右下角位置

    private Paint mDonePaint = new Paint();//已经完成的部分的画笔
    private int mDonePaintColor = Color.BLUE;
    private Paint mTodoPaint = new Paint();//未完成部分的画笔
    private int mTodoPaintColor = Color.GRAY;

    private float mTextSize;//画笔画出的text的大小
    private float mTextPadding;//label和圆的距离，当这个数为0时，距离仅仅是mRadius+mPaddingCircle；

    private boolean is_has_start_end_line;


    public List<Float> getmCenterXPosition() {
        return mCenterXPosition;
    }

    public int getmDoneNums() {
        return mDoneNums;
    }

    public void setmDoneNums(int mDoneNums) {
        this.mDoneNums = mDoneNums;
        invalidate();
    }

    public void setmFlowNums(int mFlowNums) {
        this.mFlowNums = mFlowNums;
        invalidate();
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
        setmFlowNums(labels.size());
        invalidate();
    }

    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }

    public void setmPaddingCircle(float mPaddingCircle) {
        this.mPaddingCircle = mPaddingCircle;
        invalidate();
    }

    public void setmLineHeight(float mLineHeight) {
        this.mLineHeight = mLineHeight;
        invalidate();
    }

    public void setmDonePaintColor(int mDonePaintColor) {
        this.mDonePaintColor = mDonePaintColor;
        invalidate();
    }


    public void setmTodoPaintColor(int mTodoPaintColor) {
        this.mTodoPaintColor = mTodoPaintColor;
        invalidate();
    }



    public FlowView(Context context) {
        super(context);
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowView);
        mRadius = ta.getDimension(R.styleable.FlowView_circle_raidus, 8);
        mPaddingCircle = ta.getDimension(R.styleable.FlowView_circle_padding, 10);
        mLineHeight = ta.getDimension(R.styleable.FlowView_line_height, 5);

        mDonePaintColor = ta.getColor(R.styleable.FlowView_done_color, Color.BLACK);
        mTodoPaintColor = ta.getColor(R.styleable.FlowView_todo_color, Color.GRAY);

        mTextPadding = ta.getDimension(R.styleable.FlowView_text_padding, 10);
        mTextSize = ta.getDimension(R.styleable.FlowView_text_size, 30);

        mDoneNums = ta.getInt(R.styleable.FlowView_done_nums, 3);
        mFlowNums = ta.getInt(R.styleable.FlowView_flow_nums, 5);

        is_has_start_end_line = ta.getBoolean(R.styleable.FlowView_is_has_start_end_line, true);

        ta.recycle();

        for(int i = 0 ; i < mFlowNums; i++){
            labels.add("label" + i);
        }

    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }else{
            width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
        }

        int height =(int)(mTextPadding + 2 * mPaddingCircle + 2 * mRadius + mTextSize);
        if (MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }else{
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }

        setMeasuredDimension(width, height);
    }


    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCenterY = getHeight() - Math.max(mRadius, mLineHeight/2);//画到view的最下面
        mLeftX = 0;
        mLeftY = mCenterY - (mLineHeight / 2);//最左边的点的位置
        mRightX = getWidth() - mRadius;
        mRightY = (float) (getHeight() - Math.max(mRadius, mLineHeight/2) + 0.5 * mLineHeight);//最右边的位置

        //如果要画两端的线段，那么需要画的线就有 mFlowNums + 1 条
        if(is_has_start_end_line){
            float mLineLength = (mRightX - mLeftX) / (mFlowNums + 1);//两个圆心之间的距离

            mCenterXPosition.add(mLeftX + mLineLength);//第一个圆心在初始位置右边mLineLength的地方
            for (int i = 1; i < mFlowNums - 1; i++) {
                mCenterXPosition.add(mLeftX + (i + 1) * mLineLength);
            }
            mCenterXPosition.add(mLeftX + mLineLength *  mFlowNums);
        }else{
            //如果不画两端的线段，那么需要画的线就有 mFlowNums + 1 条

            if(labels.size() > 0){
                //为了防止显示不全，选择第一个字副串的像素长度和圆圈半径中最大的，作为第一个圆心的X坐标
                //开始的点mLeftX，结束的点mRightX
                mDonePaint.setTextSize(mTextSize);//因为首先会调用onSiezChange()，然后才会调用onDraw，所以要在这里确定字大小。
                float textSize0 = mDonePaint.measureText(labels.get(0));//获取label0的字符总长度，像素
                Log.v("tag",""+textSize0 + labels.get(0));
                mLeftX = Math.max(textSize0/2, mRadius);//最开始的x坐标是：第一个字副串的像素长度，和圆圈半径中最大的

                float textSize1 = mDonePaint.measureText(labels.get(labels.size()-1));//获取label0的字符总长度，像素
                mRightX = getWidth() - Math.max(textSize1/2, mRadius);//最开始的x坐标是：第一个字副串的像素长度，和圆圈半径中最大的
            }

            float mLineLength = (mRightX - mLeftX) / (mFlowNums - 1);///两个圆心之间的距离

            mCenterXPosition.add(mLeftX);//第一个圆心的x坐标在mRadius处。
            for (int i = 1; i < mFlowNums - 1; i++) {
                mCenterXPosition.add(mLeftX + i * mLineLength);
            }
            mCenterXPosition.add(mRightX);
        }

    }

    /**
     * 画开始处和结束处的线段
     */
    private void drawSELine(Canvas canvas){
        //最左边的线段
        float firstEndX = 0;
        if(mCenterXPosition.size() > 0){
            firstEndX = mCenterXPosition.get(0) - mRadius - mPaddingCircle;
        }
        canvas.drawRect(
                0,
                mLeftY,
                firstEndX,
                mRightY,
                (0 < mDoneNums) ? mDonePaint : mTodoPaint);


        //最右边的线段
        float lastStartX = 0;
        if(mCenterXPosition.size() > 0){
            lastStartX = mCenterXPosition.get(mCenterXPosition.size() - 1) + mRadius + mPaddingCircle;
        }
        canvas.drawRect(
                lastStartX,
                mLeftY,
                getWidth(),
                mRightY,
                (mDoneNums >= mFlowNums) ? mDonePaint : mTodoPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTodoPaint.setColor(mTodoPaintColor);
        mTodoPaint.setAntiAlias(true);
        mTodoPaint.setStyle(Paint.Style.FILL);
        mTodoPaint.setStrokeWidth(3);
        mTodoPaint.setTextSize(mTextSize);
        mTodoPaint.setTextAlign(Paint.Align.CENTER);

        mDonePaint.setColor(mDonePaintColor);
        mDonePaint.setAntiAlias(true);
        mDonePaint.setStyle(Paint.Style.FILL);
        mDonePaint.setStrokeWidth(3);
        mDonePaint.setTextSize(mTextSize);
        mDonePaint.setTextAlign(Paint.Align.CENTER);


        //如果要画最开始处和结束处的线段
        if(is_has_start_end_line){
            drawSELine(canvas);
        }


        //画中间的线段
        for(int i = 0; i < mCenterXPosition.size() - 1; i++){
            canvas.drawRect(
                    mCenterXPosition.get(i) + mRadius + mPaddingCircle,
                    mLeftY,
                    mCenterXPosition.get(i + 1) - mRadius - mPaddingCircle,
                    mRightY,
                    (i < mDoneNums - 1) ? mDonePaint : mTodoPaint);
        }



        //画圆圈
        for(int i = 0; i < mCenterXPosition.size(); i++){
            canvas.drawCircle(mCenterXPosition.get(i), mCenterY, mRadius,
                    (i < mDoneNums) ? mDonePaint : mTodoPaint);

        }



        //在圆圈上写label
        for(int i = 0; i < mCenterXPosition.size(); i++){
            canvas.drawText(
                    labels.get(i),
                    0,
                    labels.get(i).length(),
                    mCenterXPosition.get(i),
                    mCenterY - mRadius -  mPaddingCircle - mTextPadding,
                    (i < mDoneNums) ? mDonePaint : mTodoPaint);
        }

    }

}