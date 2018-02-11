package com.example.a5days.rumahmakan;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a5days.rumahmakan.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fauzan Muhammad on 25/01/2018.
 */

public class Activity_Rec extends AppCompatActivity {

    private String TAG = Activity_Rec.class.getSimpleName();
    private ListView lv;

    int success;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "tag_json_obj";


    ArrayList<HashMap<String, String>> listRekomendasi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recomendasi);

        listRekomendasi = new ArrayList<>();
        lv = (ListView) findViewById(R.id.rekomendasi);
        new AmbilData().execute();
    }
    class AmbilData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Activity_Rec.this, "Sedang mengambil data rekomendasi", Toast.LENGTH_LONG).show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://iddota.hol.es/rm/android/method.php";

           final String iduser = Menu_Utama.id;
            StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "Response from url: " + response);
                    if (response != null) {
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    for (int i=0; i< jsonObj.length(); i++){
                        String name_rm = jsonObj.names().getString(i);
                        HashMap<String, String> listRm = new HashMap<>();
                        listRm.put("name_rm", name_rm);
                        listRekomendasi.add(listRm);
                    }
                    ListAdapter adapter = new SimpleAdapter(Activity_Rec.this, listRekomendasi, R.layout.list_rekomendasi, new String[]{"name_rm"}, new int[]{R.id.namaRM});
                    lv.setAdapter(adapter);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                Log.e(TAG, "Gagal mendapatakan data JSON");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan data JSON", Toast.LENGTH_LONG).show();
                    }
                });
            }

                    Log.e(TAG, "Hasil permintaan: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);
                        success = jObj.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            Log.e("Berhasil mengirim id", jObj.toString());
                            Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            iduser.toString();
                        } else {
                            Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Gagal kirim id" + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("iduser", iduser);
                    return params;
                }
            };


            AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);


            return null;
        }

    }

    Intent intent;
    @Override
    public void onBackPressed() {
        intent = new Intent(Activity_Rec.this, Menu_Utama.class);
        finish();
        startActivity(intent);
    }
}

