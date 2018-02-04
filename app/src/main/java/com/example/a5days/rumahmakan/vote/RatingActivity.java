package com.example.a5days.rumahmakan.vote;

import android.app.AlertDialog;
import android.app.AutomaticZenRule;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a5days.rumahmakan.Menu_Utama;
import com.example.a5days.rumahmakan.R;
import com.example.a5days.rumahmakan.Register;
import com.example.a5days.rumahmakan.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.a5days.rumahmakan.DetailRumahMakan.idRM;
import static com.example.a5days.rumahmakan.Menu_Utama.id;

/**
 * Created by Fauzan Muhammad on 16/01/2018.
 */

public class RatingActivity extends AppCompatActivity {

    ProgressDialog pDialog;


    public ArrayList<HashMap<String, Object>> MyArrList;

    public RatingBar rating;

    Intent intent;

    int success;
    ConnectivityManager conMgr;

    String url = "http://iddota.hol.es/rm/android/updaterating.php";

    private static final String TAG = Register.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    private AutomaticZenRule vote;
    private AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

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

       // final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final RatingBar rating = (RatingBar) findViewById(R.id.rating);
        final Button vote = (Button) findViewById(R.id.btnVote);
//        if (rating.getRating() <= 0) {
//            AlertDialog ad = adb.create();
//            ad.setMessage("Please select rating point 1-5");
//            ad.show();
//        }
        vote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username = rating.toString();
                String ida = id.toString();
                String idRMa = idRM.toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkRegister(rating,id,idRM);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkRegister(final RatingBar rating, final String id, final String idRM) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Vote ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Vote Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Vote!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        rating.setNumStars(5);
                        id.toString();
                        idRM.toString();

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
                Log.e(TAG, "Vote Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("ratingPoint", String.valueOf(rating.getRating()));
                params.put("id_user", id);
                params.put("id_rm", idRM);

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
        intent = new Intent(RatingActivity.this, ListRating.class);
        finish();
        startActivity(intent);
    }

    public static void start(Menu_Utama menu_utama) {
    }
}
