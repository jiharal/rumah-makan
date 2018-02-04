package com.example.a5days.rumahmakan;


import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a5days.rumahmakan.adapter.ListImageAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

import static com.example.a5days.rumahmakan.DetailRumahMakan.idRM;

/**
 * Created by Fauzan Muhammad on 29/12/2017.
 */

public class List_Komentar extends AppCompatActivity {
    public static String idRM;
    private ListView listkomentar;
    private String[] idkomentar, komentar;
    private ArrayAdapter papanList;

    private Dialog loading, gagal;
    private Button btnCoba, btnTambah;
    private TextView statusLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_komentar);
        listkomentar = (ListView) findViewById(R.id.listkomentar);
        loading = new Dialog(this);
        loading.setContentView(R.layout.dialog_loading);
        statusLoading = (TextView) loading.findViewById(R.id.statusLoading);
        loading.setTitle("loading");
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        gagal = new Dialog(this);
        gagal.setContentView(R.layout.dialog_gagal);
        gagal.setTitle("koneksi error");
        gagal.setCancelable(false);
        gagal.setCanceledOnTouchOutside(false);
        btnCoba = (Button) gagal.findViewById(R.id.btnCoba);
        btnTambah = (Button) findViewById(R.id.btntambah);

        try {
            dataKoemntar();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void dataKoemntar() throws Exception{
        RequestParams params = new RequestParams();
        params.put("aksi", "komentar");
        params.put("id", idRM);

        ServerClass.post("android/rmakan.php", params,new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        loading.show();
                        statusLoading.setText("komentar");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONArray response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        try {
                            loading.dismiss();
                            gagal.dismiss();
                            if (response.isNull(0))
                            {
                                Toast.makeText(List_Komentar.this, "belum ada komentar ", Toast.LENGTH_SHORT).show();
                                getIntent();
                            }
                            else {

                                idkomentar = new String[response.length()];
                                komentar = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response
                                            .getJSONObject(i);
                                   idkomentar[i] = jsonObject.getString("id_komentar");
                                    komentar[i] = jsonObject.getString("username")+"\n" +jsonObject.getString("komentar");
                                }
                                papanList = new ArrayAdapter(List_Komentar.this, R.layout.layout, komentar);
                                listkomentar.setAdapter(papanList);
                            }
                        } catch (Exception e) {
                            // TODOhandle exception
                            e.printStackTrace();
                        }
                        btnTambah.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                try {
                                    List_Komentar.idRM = idRM;
                                    startActivity(new Intent(List_Komentar.this, TambahKomentar.class));
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        loading.dismiss();
                        gagal.show();
                        btnCoba.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                try {
                                    dataKoemntar();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONArray errorResponse) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        loading.dismiss();
                        gagal.show();
                        btnCoba.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                try {
                                    dataKoemntar();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        loading.dismiss();
                        gagal.show();
                        btnCoba.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                try {
                                    dataKoemntar();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
    }
}