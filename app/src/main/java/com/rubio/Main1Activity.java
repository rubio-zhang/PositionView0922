package com.rubio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;


public class Main1Activity extends AppCompatActivity implements View.OnClickListener {

    SlideView slideView;
    SeekBar seekBar;
    SeekBar sbAlpha;
    TextView btnUp;
    TextView btnDown;
    TextView btnLeft;
    TextView btnRight;
    TextView tvView;
    ScrollView sv;

    boolean isChange=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        sv = (ScrollView) findViewById(R.id.sv);
        slideView = (SlideView) findViewById(R.id.svview);
        sbAlpha = (SeekBar) findViewById(R.id.sb_alpha);
        seekBar = (SeekBar) findViewById(R.id.sb);
        tvView = (TextView) findViewById(R.id.tvText);
        btnUp = (TextView) findViewById(R.id.UP);
        btnDown = (TextView) findViewById(R.id.DOWN);
        btnLeft = (TextView) findViewById(R.id.LEFT);
        btnRight = (TextView) findViewById(R.id.RIGHT);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        sbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progress2 = seekBar.getProgress();
                slideView.setAplha(progress2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress2 = seekBar.getProgress();
                slideView.setAplha(progress2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress2 = seekBar.getProgress();
                slideView.setAplha(progress2);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isChange){
                    if (progress<=25){
                        slideView.setZoom((float) ((float) progress / 50+0.5));
                    }else{
                        slideView.setZoom((((float)(progress-25)/25)+1));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
            }
        });

        slideView.setCall(new SlideView.CallView() {
            @Override
            public void onMessage(String name) {
                tvView.append(name + "\n");
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }

            @Override
            public void onScale(float size) {
                if (size<1){
                    seekBar.setProgress((int) ((size-0.5)*50));
                }else{
                    seekBar.setProgress(25+(int) ((size-1)*25));
                }
//                size
//                (0.5 + ((float) progress / 50))
//                seekBar
            }

            @Override
            public void onZoomTouch(boolean flag) {
                isChange=!flag;
            }

            @Override
            public void onBitmapMessage(int TotalLength, int TotalWidth,float scale,float alpha, int centerPointX, int centerPointY, int bitWidth, int bitHeight) {
                tvView.setText("TotalLength: "   +TotalLength+"\n"
                +"TotalWidth:    "+TotalWidth+"\n"
                +"scale:    "+scale+"\n"
                +"alpha:    "+alpha+"\n"
                +"centerPointX:   "+centerPointX+"\n"
                +"centerPointY:     "+centerPointY+"\n"
                +"bitWidth:       "+bitWidth+"\n"
                +"bitHeight:      "+bitHeight+"\n"
                );
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.UP:
                slideView.setOpre(SlideView.UP);
                break;
            case R.id.DOWN:
                slideView.setOpre(SlideView.DOWN);
                break;
            case R.id.LEFT:
                slideView.setOpre(SlideView.LEFT);
                break;
            case R.id.RIGHT:
                slideView.setOpre(SlideView.RIGHT);
                break;
        }
    }
}
