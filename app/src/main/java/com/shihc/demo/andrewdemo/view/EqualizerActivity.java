package com.shihc.demo.andrewdemo.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        equalizerView.setOnChangeListener(new EqualizerView.OnChangeListener() {
            @Override
            public void onProgressBefore() {
                Log.d("EqualizerActivity", "自定义");
            }

            @Override
            public void onProgressChanged(EqualizerView seekBar, int[] progress) {
                StringBuilder builder = new StringBuilder();
                for (Integer item : progress){
                    builder.append(item).append(",");
                }
                Log.d("EqualizerActivity", "自定义完成 ：" + builder.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                equalizerView.setProgress(new int[]{-9, -2, 2, 5, 11});
                break;
            case R.id.btn2:
                equalizerView.setProgress(new int[]{12, 8, 5, 0, -7});
                break;
            case R.id.btn3:
                equalizerView.setProgress(new int[]{-10, 0, 8, 2, -8});
                break;
            case R.id.btn4:
                equalizerView.setProgress(new int[]{11, 3, -7, -1, 7});
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        equalizerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                equalizerView.setProgress(new int[]{-9, -2, 2, 5, 11});
            }
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
