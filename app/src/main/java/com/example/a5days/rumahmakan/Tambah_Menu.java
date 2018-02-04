package com.example.a5days.rumahmakan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Fauzan Muhammad on 29/01/2018.
 */



public class Tambah_Menu extends AppCompatActivity {
    private ListView listrm;
    private String[] id_rm, nama_rm;
    private ArrayAdapter papanList;

    private Dialog loading, gagal;
    private Button btnCoba, btnTambah;
    private TextView statusLoading;


    Intent intent;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_menu);

        listrm = (ListView) findViewById(R.id.listNamaRm);
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
        btnTambah = (Button) findViewById(R.id.simpanMenu);

        try {
            dataRm();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dataRm()  throws Exception{
        RequestParams params = new RequestParams();
        params.put("aksi", "rm");
        ServerClass.post("android/getlistrm.php", params,new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                loading.show();
                statusLoading.setText("rm");
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
                        Toast.makeText(Tambah_Menu.this, "belum ada komentar ", Toast.LENGTH_SHORT).show();
                        getIntent();
                    }
                    else {

                        id_rm = new String[response.length()];
                        nama_rm = new String[response.length()];
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response
                                    .getJSONObject(i);
                            id_rm[i] = jsonObject.getString("id_rm");
                            nama_rm[i] = jsonObject.getString("username")+"\n" +jsonObject.getString("komentar");
                        }
                        papanList = new ArrayAdapter(Tambah_Menu.this, R.layout.layout, nama_rm);
                        listrm.setAdapter(papanList);
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
                            startActivity(new Intent(Tambah_Menu.this, Menu_Utama.class));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        intent = new Intent(Tambah_Menu.this, Menu_Utama.class);
        finish();
        startActivity(intent);
    }
}
