package com.ginsmile.flowviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujin on 16/08/17.
 */
public class FlowView extends LinearLayout implements FlowViewIndicator.OnDrawListener{

    private FlowViewIndicator mFlowViewIndicator;
    private List<String> mLabels = new ArrayList<>();
    private int mDoneNums;//完成的个数

    private float mRadius;//指示器圆形的半径
    private int mDonePaintColor;
    private int mTodoPaintColor;
    private float mPaddingCircle;
    private float mLineHeight;//线段的高度
    private FrameLayout mLabelsLayout;//放label的

    public void setmTodoPaintColor(int mTodoPaintColor) {
        this.mTodoPaintColor = mTodoPaintColor;
        mFlowViewIndicator.setmTodoPaintColor(mTodoPaintColor);
    }

    public void setmDoneNums(int mDoneNums) {
        if(mDoneNums < 0) mDoneNums = 0;
        if(mDoneNums > mLabels.size()) mDoneNums = mLabels.size();
        this.mDoneNums = mDoneNums;
        mFlowViewIndicator.setmDoneNums(mDoneNums);
    }

    public int getmDoneNums() {
        return mDoneNums;
    }

    public void setmDonePaintColor(int mDonePaintColor) {
        this.mDonePaintColor = mDonePaintColor;
        mFlowViewIndicator.setmDonePaintColor(mDonePaintColor);
    }

    public void setmLabels(List<String> mLabels) {
        this.mLabels = mLabels;
        mFlowViewIndicator.setmFlowNums(mLabels.size());

    }


    public void setmLineHeight(float mLineHeight) {
        this.mLineHeight = mLineHeight;
        mFlowViewIndicator.setmLineHeight(mLineHeight);
    }

    public void setmPaddingCircle(float mPaddingCircle) {
        this.mPaddingCircle = mPaddingCircle;
        mFlowViewIndicator.setmPaddingCircle(mPaddingCircle);
    }


    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
        mFlowViewIndicator.setmRadius(mRadius);
    }


    public FlowView(Context context) {
        super(context);
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.FlowView);
        mRadius = ta.getDimension(R.styleable.FlowView_circle_raidus, 10);
        mPaddingCircle = ta.getDimension(R.styleable.FlowView_padding_circle, 10);
        mDonePaintColor = ta.getColor(R.styleable.FlowView_done_color, Color.BLACK);
        mTodoPaintColor = ta.getColor(R.styleable.FlowView_todo_color, Color.GRAY);
        mLineHeight = ta.getDimension(R.styleable.FlowView_line_height, 10);
        ta.recycle();
        init();
    }

    private void init(){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_flowview, this);
        mFlowViewIndicator = (FlowViewIndicator) rootView.findViewById(R.id.flow_indi_view);
        mLabelsLayout = (FrameLayout) rootView.findViewById(R.id.labels_container);

        mLabels.add("one");
        mLabels.add("two");
        mLabels.add("three");
        mLabels.add("four");
        setmLabels(mLabels);
        mDoneNums = 2;

        mFlowViewIndicator.setmDoneNums(0);
        mFlowViewIndicator.setmDonePaintColor(mDonePaintColor);
        mFlowViewIndicator.setmTodoPaintColor(mTodoPaintColor);
        mFlowViewIndicator.setmPaddingCircle(mPaddingCircle);
        mFlowViewIndicator.setmLineHeight(mLineHeight);
        mFlowViewIndicator.setmDoneNums(mDoneNums);
        mFlowViewIndicator.setmRadius(mRadius);
        mFlowViewIndicator.setmFlowNums(mLabels.size());
        mFlowViewIndicator.setDrawListener(this);
    }

    @Override
    public void onReady() {
        drawLabels();
    }

    private void drawLabels() {
        List<Float> indicatorPosition = mFlowViewIndicator.getmCenterXPosition();
        mLabelsLayout.invalidate();
        if (mLabels != null) {
            for (int i = 0; i < mLabels.size(); i++) {
                TextView textView = new TextView(getContext());
                textView.setText(mLabels.get(i));
                textView.setTextColor(i < mDoneNums ? mDonePaintColor : mTodoPaintColor);
                float textLength = textView.getTextSize() * 2;
                textView.setX(indicatorPosition.get(i) - textLength/2);
                textView.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                mLabelsLayout.addView(textView);
            }
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v("tagggg","onDraw................");
    }
}
