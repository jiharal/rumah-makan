package com.example.a5days.rumahmakan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a5days.rumahmakan.adapter.ListImageAdapterVertical;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 5Days on 20/11/2017.
 */

public class HasilPencarian extends AppCompatActivity {
    public static String kolom;
    public static String isi;
    private ListView studio, makan;
    private ListImageAdapterVertical listImageAdapter;
    private ClassItem[] itemClass;
    private String[] idRuamahM;

    private Dialog loading, gagal;
    private Button btnCoba;
    private TextView statusLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hasil_pencarian);
        studio = (ListView) findViewById(R.id.listStudio);
makan = (ListView) findViewById(R.id.list_pMenu);
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
        try {
            dataStudio();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    public void dataStudio() throws Exception{
        RequestParams params = new RequestParams();
        params.put("aksi", "data pencarian");
        params.put("kolom", kolom);
        params.put("isi", isi);

        ServerClass.post("android/rmakan.php", params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        loading.show();
                        statusLoading.setText("melakukan pencarian");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONArray response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        try {
                            loading.dismiss();
                            gagal.dismiss();
                            if (response.isNull(0)) {
                                Toast.makeText(HasilPencarian.this, "pencarian tidak ditemukan", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                idRuamahM = new String[response.length()];
                                itemClass = new ClassItem[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response
                                            .getJSONObject(i);
                                    idRuamahM[i] = jsonObject.getString("id_rm");
                                    itemClass[i] = new ClassItem(ServerClass.getUrl("rm/"+jsonObject.getString("gambar")), jsonObject.getString("nama_rm")+"\n"+jsonObject.getString("alamat"));
                                }
                                listImageAdapter = new ListImageAdapterVertical(HasilPencarian.this, R.layout.item_gambar, itemClass);
                                studio.setAdapter(listImageAdapter);
                                studio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> parent, View view,
                                            int position, long id) {
                                        // TODO Auto-generated method stub
                                        DetailRumahMakan.idRM = idRuamahM[position];
                                        startActivity(new Intent(HasilPencarian.this, DetailRumahMakan.class));
                                    }
                                });

                            }

                        } catch (Exception e) {
                            // TODOhandle exception
                            e.printStackTrace();
                        }
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
                                    dataStudio();
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
                                    dataStudio();
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
                                    dataStudio();
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
