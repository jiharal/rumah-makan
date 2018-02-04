package com.example.a5days.rumahmakan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private int waktuLoading = 3000;
    private int counter = 0;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 1000);
                if(counter<waktuLoading){
                    counter+=1000;
                }else{
                    handler.removeCallbacks(runnable);
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
}
