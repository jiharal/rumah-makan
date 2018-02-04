package com.example.a5days.rumahmakan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Fauzan Muhammad on 25/01/2018.
 */

public class Activity_Rec extends AppCompatActivity {

    private String TAG = Activity_Rec.class.getSimpleName();
    private ListView lv;

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
            HttpHandler sh = new HttpHandler();

            String url = "http://iddota.hol.es/rm/android/method.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    for (int i=0; i< jsonObj.length(); i++){
                        String name_rm = jsonObj.names().getString(i);
                        HashMap<String, String> listRm = new HashMap<>();
                        listRm.put("name_rm", name_rm);
                        listRekomendasi.add(listRm);
                    }
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

            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(Activity_Rec.this, listRekomendasi, R.layout.list_rekomendasi, new String[]{"name_rm"}, new int[]{R.id.namaRM});
            lv.setAdapter(adapter);
        }
    }
}