package com.beerwhere.poster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class Jump extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
        final Intent intent = new Intent();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                intent.setClass(Jump.this, MainActivity.class);
                startActivity(intent);
            }
        };
        timer.schedule(timerTask, 1000 * 3);
    }
}
