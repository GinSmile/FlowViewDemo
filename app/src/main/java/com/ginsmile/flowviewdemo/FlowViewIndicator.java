package com.ginsmile.flowviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujin on 16/08/17.
 */
public class FlowViewIndicator extends View {
    private int mFlowNums;//流程的个数
    private int mDoneNums;//完成的个数

    private float mRadius;//指示器圆形的半径
    private Paint mDonePaint = new Paint();//已经完成的部分的画笔
    private int mDonePaintColor;

    private Paint mTodoPaint = new Paint();//未完成部分的画笔
    private int mTodoPaintColor;

    private float mPaddingCircle;//圆圈的padding,从圆圈的边界开始

    private float mLineHeight;//线段的高度

    private float mCenterY;//圆心的Y坐标
    private float mLeftX,mLeftY,mRightX,mRightY;//线段为细长的矩形，mLeftX,mLeftY为矩形左上角位置，mRightX, mRightY为右下角位置
    private List<Float> mCenterXPosition = new ArrayList<>();//存放圆心的位置坐标的列表

    private OnDrawListener mDrawListener;


    public List<Float> getmCenterXPosition() {
        return mCenterXPosition;
    }

    public void setmDoneNums(int mDoneNums) {
        this.mDoneNums = mDoneNums;
        invalidate();
    }

    public void setmDonePaintColor(int mDonePaintColor) {
        this.mDonePaintColor = mDonePaintColor;
    }

    public void setmFlowNums(int mFlowNums) {
        this.mFlowNums = mFlowNums;
        invalidate();
    }

    public void setmPaddingCircle(float mPaddingCircle) {
        this.mPaddingCircle = mPaddingCircle;
    }

    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public void setmTodoPaintColor(int mTodoPaintColor) {
        this.mTodoPaintColor = mTodoPaintColor;
    }

    public void setmLineHeight(float mLineHeight) {
        this.mLineHeight = mLineHeight;
    }



    public FlowViewIndicator(Context context) {
        super(context);
    }

    public FlowViewIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 200;
        if (MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }else{
            width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
        }

        int height = 50;
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

        mCenterY = 0.5f * getHeight();//画到view的中央
        mLeftX = mRadius;
        mLeftY = mCenterY - (mLineHeight / 2);//最左边的点的位置
        mRightX = getWidth() - mRadius;
        mRightY = (float) (0.5f * getHeight() + 0.5 * mLineHeight);//最右边的位置

        float mDelta = (mRightX - mLeftX) / (mFlowNums + 1);//一条线段的长度

        mCenterXPosition.add(mLeftX + mDelta);
        for (int i = 1; i < mFlowNums - 1; i++) {
            mCenterXPosition.add(mLeftX + mDelta + (i * mDelta));
        }
        mCenterXPosition.add(mLeftX + mDelta *  mFlowNums);

        Log.v("tagggg3",mCenterXPosition.size()+"");


        mDrawListener.onReady();
    }

    public void setDrawListener(OnDrawListener drawListener) {
        mDrawListener = drawListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTodoPaint.setColor(mTodoPaintColor);
        mTodoPaint.setAntiAlias(true);
        mTodoPaint.setStyle(Paint.Style.FILL);
        mTodoPaint.setStrokeWidth(3);

        mDonePaint.setColor(mDonePaintColor);
        mDonePaint.setAntiAlias(true);
        mDonePaint.setStyle(Paint.Style.FILL);
        mDonePaint.setStrokeWidth(3);


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
        Log.v("sss",firstEndX+"");

        //画线段
        for(int i = 0; i < mCenterXPosition.size() - 1; i++){
            canvas.drawRect(
                    mCenterXPosition.get(i) + mRadius + mPaddingCircle,
                    mLeftY,
                    mCenterXPosition.get(i + 1) - mRadius - mPaddingCircle,
                    mRightY,
                    (i < mDoneNums - 1) ? mDonePaint : mTodoPaint);
        }

        //最右边的线段
        //最左边的线段
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

        //画圆圈
        for(int i = 0; i < mCenterXPosition.size(); i++){
            canvas.drawCircle(mCenterXPosition.get(i), mCenterY, mRadius,
                    (i < mDoneNums) ? mDonePaint : mTodoPaint);
            Log.v("taggg","..circle...");
        }


        mDrawListener.onReady();


    }

    interface OnDrawListener{
        void onReady();
    }
}
