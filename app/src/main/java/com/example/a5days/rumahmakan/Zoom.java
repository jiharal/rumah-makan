package com.example.a5days.rumahmakan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by 5Days on 20/11/2017.
 */

public class Zoom extends Activity {
    public static String urlGambar;
    private ImageView zoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        zoom = (ImageView) findViewById(R.id.zoom);
        Picasso.with(this).load(urlGambar).into(zoom);
    }
}
