package com.shihc.demo.andrewdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
                equalizerView.setProgress(new int[]{10, 20, 40, 60, 80});
                break;
            case R.id.btn2:
                equalizerView.setProgress(new int[]{100, 80, 60, 40, 20});
                break;
            case R.id.btn3:
                equalizerView.setProgress(new int[]{20, 40, 80, 50, 30});
                break;
            case R.id.btn4:
                equalizerView.setProgress(new int[]{80, 50, 10, 40, 70});
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        equalizerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                equalizerView.setProgress(new int[]{10, 20, 40, 60, 80});
            }
        }, 300);
    }
}
