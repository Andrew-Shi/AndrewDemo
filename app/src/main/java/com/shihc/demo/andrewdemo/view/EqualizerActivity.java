package com.shihc.demo.andrewdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.shihc.demo.andrewdemo.R;

public class EqualizerActivity extends AppCompatActivity implements View.OnClickListener {

    EqualizerView equalizerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        equalizerView = (EqualizerView) findViewById(R.id.equalizer_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                equalizerView.setProgress(0, 90);
                break;
            case R.id.btn2:
                equalizerView.setProgress(1, 20);
                break;
            case R.id.btn3:
                equalizerView.setProgress(2, 20);
                break;
            case R.id.btn4:
                SparseArray<Integer> array = new SparseArray<>();
                array.put(0, 20);
                array.put(1, 80);
                array.put(3, 10);
                array.put(4, 100);
                array.put(2, 80);
                equalizerView.setMulProgress(array);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        equalizerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                SparseArray<Integer> array = new SparseArray<>();
                array.put(0, 20);
                array.put(1, 80);
                array.put(3, 10);
                array.put(4, 100);
                array.put(2, 80);
                equalizerView.setMulProgress(array);
            }
        }, 300);
    }
}
