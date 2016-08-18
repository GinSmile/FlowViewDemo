package com.ginsmile.flowviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> mLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLabels.add("one");
        mLabels.add("two");
        mLabels.add("three");
        mLabels.add("four");
        mLabels.add("five");
        final FlowView fv = (FlowView)findViewById(R.id.fv);
        fv.setLabels(mLabels);

        Button btn1 = (Button)findViewById(R.id.forward_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fv.setmDoneNums(fv.getmDoneNums() + 1);
            }
        });

        Button btn2 = (Button)findViewById(R.id.backword_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fv.setmDoneNums(fv.getmDoneNums() - 1);
            }
        });

        Button btn3 = (Button)findViewById(R.id.reset_btn);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fv.setmDoneNums(0);
            }
        });






    }
}
