package com.example.a5days.rumahmakan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a5days.rumahmakan.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 5Days on 30/11/2017.
 */

public class Daftar_Rm extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_tambah, btn_login;
    EditText txt_nama_rm, txt_alamat,txt_desc, txt_jam;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = ServerClass.getUrl("android/daftar_rm.php") ;

    private static final String TAG = Daftar_Rm.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_rumah_makan);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

      //  btn_login = (Button) findViewById(R.id.btn_login);
        btn_tambah = (Button) findViewById(R.id.btn_tambah);
        txt_nama_rm = (EditText) findViewById(R.id.txt_namaRm);
        txt_alamat = (EditText) findViewById(R.id.txt_alamat);
        txt_desc = (EditText) findViewById(R.id.txt_deskripsi);
        txt_jam = (EditText) findViewById(R.id.txt_jam);

        btn_tambah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String nama_rm = txt_nama_rm.getText().toString();
                String alamat = txt_alamat.getText().toString();
                String desc = txt_desc.getText().toString();
                String jam = txt_jam.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkRegister(nama_rm,alamat,desc,jam);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void checkRegister(final String nama_rm, final String alamat, final String desc, final String jam) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());
                        intent = new Intent(Daftar_Rm.this, Menu_Utama.class);
                        finish();
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_nama_rm.setText("");
                        txt_alamat.setText("");
                        txt_desc.setText("");
                        txt_jam.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama_rm", nama_rm);
                params.put("alamat", alamat);
                params.put("deskripsi", desc);
                params.put("waktu_operasional", jam);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(Daftar_Rm.this, Menu_Utama.class);
        finish();
        startActivity(intent);
    }

    public static void start(Menu_Utama menu_utama) {
    }
}
