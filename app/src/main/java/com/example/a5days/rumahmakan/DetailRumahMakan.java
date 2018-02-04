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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a5days.rumahmakan.vote.ListRating;
import com.example.a5days.rumahmakan.vote.RatingActivity;
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

import static com.example.a5days.rumahmakan.Menu_Utama.jarakObj;
import static com.example.a5days.rumahmakan.Menu_Utama.latitude;
import static com.example.a5days.rumahmakan.Menu_Utama.latitudeObjek;
import static com.example.a5days.rumahmakan.Menu_Utama.location;
import static com.example.a5days.rumahmakan.Menu_Utama.lokasiPengguna;
import static com.example.a5days.rumahmakan.Menu_Utama.longitude;
import static com.example.a5days.rumahmakan.Menu_Utama.longitudeObjek;
import static com.example.a5days.rumahmakan.Menu_Utama.namaRM;

/**
 * Created by 5Days on 20/11/2017.
 */

public class DetailRumahMakan extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
public EditText tambahkomentar;
    public static String idRM, idUser;
    private ImageView gambar;
    private TextView alamat, deskripsi, jam, komentar,menu2,menu3, kontak;
    private Button  btnCoba, btnHarga, btnKoment, btnRute, btnvote;//, btninfo;
    EditText txtkomentar;
    ProgressDialog pDialog;
    private Dialog loading, gagal;

    private TextView statusLoading;
    private GoogleMap map;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private ClassItem[] itemClass;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String url = ServerClass.getUrl("android/daftar_rm.php") ;
    String tag_json_obj = "json_obj_req";
    int success;
    ConnectivityManager conMgr;
    private static final String TAG = DetailRumahMakan.class.getSimpleName();


    protected LocationManager locationManager;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    // flag for GPS status
    boolean canGetLocation = false;
    DecimalFormat df = new DecimalFormat("#.##");
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    private static final long MAX_DISATANCE_CHANGE_FOR_UPDATES = 5000 ;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = (1000 * 60 * 1) / 60; // 1


    // second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_rumah_makan);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        fm.getMapAsync(this);

        gambar = (ImageView) findViewById(R.id.gambar1);

        alamat = (TextView) findViewById(R.id.alamat);
        deskripsi = (TextView) findViewById(R.id.deskripsi);
        jam = (TextView) findViewById(R.id.jam);
       //komentar = (TextView) findViewById(R.id.komentar);


        btnKoment  = (Button) findViewById(R.id.btn_tambah);
        btnHarga = (Button) findViewById(R.id.btnharga);
        btnRute = (Button) findViewById(R.id.btnRute);
        btnvote = (Button) findViewById(R.id.btnvote);
        //btninfo = (Button) findViewById(R.id.btninfo);

        mShortAnimationDuration = 10000;


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
            dataRM();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    public void dataRM() throws Exception {
        getLocation();
        RequestParams params = new RequestParams();
        params.put("aksi", "detail makan");
        params.put("id", idRM);
        LatLng lokasiUserAktif = new LatLng(getLatitude(), getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiUserAktif, 14));

        ServerClass.post("android/rmakan.php", params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        loading.show();
                        statusLoading.setText("mengambil data rumah makan");
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
                                Toast.makeText(DetailRumahMakan.this, "belum ada data rumah makan ", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                JSONObject jsonObject = response
                                        .getJSONObject(0);
                                setTitle(jsonObject.getString("nama_rm"));

                                alamat.setText(jsonObject.getString("alamat"));
                                deskripsi.setText(jsonObject.getString("deskripsi"));
                                jam.setText(jsonObject.getString("waktu_operasional"));
//                                komentar.setText(jsonObject.getString("komentar"));



                                Picasso.with(DetailRumahMakan.this).load(ServerClass.getUrl("rm/"+jsonObject.getString("gambar"))).into(gambar);
                                final String urlGambar = ServerClass.getUrl("rm/"+jsonObject.getString("gambar"));
//                                gambar.setOnClickListener(new View.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        // TODO Auto-generated method stub
//                                        //zoomImageFromThumb(gambar, R.drawable.maps);
//                                        Zoom.urlGambar = urlGambar;
//                                        startActivity(new Intent(DetailRumahMakan.this, Zoom.class));
//
//                                    }
//                                });
                                Location lokObj = new Location("");

                                lokObj.setLatitude(jsonObject.getDouble("latitude"));
                                lokObj.setLongitude(jsonObject.getDouble("longitude"));
                                for (int i = 0; i < response.length(); i++){
                                    jsonObject = response
                                            .getJSONObject(i);

                                    jarakObj[i] = jarak(lokasiPengguna, lokObj);
                                    String tampilJarak = df.format(jarakObj[i]);
                                }
                                // temp item
                                ClassItem obj1;
                                // temp jarak
                                double obj2;
                                // temp latitude
                                double obj3;
                                // temp longitude
                                double obj4;
                                //temp nama
                                String obj5;
                                // temp id
                                String obj6;

                                // String obj7;

                                // mulai algoritma buble sorting
                                for (int k = 0; k < jarakObj.length; k++) {
                                    for (int l = 0; l < jarakObj.length - (k + 1); l++) {
                                        if (jarakObj[l] > jarakObj[l + 1]) {
//                                            obj1 = itemClass[l];
                                            obj2 = jarakObj[l];
                                            obj3 = latitudeObjek[l];
                                            obj4 = longitudeObjek[l];
                                            obj5 = namaRM[l];
                                            obj6 = Menu_Utama.idRM[l];


//                                            itemClass[l] = itemClass[l + 1];
                                            jarakObj[l] = jarakObj[l + 1];
                                            latitudeObjek[l] = latitudeObjek[l + 1];
                                            longitudeObjek[l] = longitudeObjek[l + 1];
                                            Menu_Utama.idRM[l] = Menu_Utama.idRM[l + 1];
                                            namaRM[l] = namaRM[l + 1];


                                         //   itemClass[l + 1] = obj1;
                                            jarakObj[l + 1] = obj2;
                                            latitudeObjek[l + 1] = obj3;
                                            longitudeObjek[l + 1] = obj4;
                                            namaRM[l + 1] = obj5;
                                            Menu_Utama.idRM[l + 1] = obj6;


                                        }

                                    }

                                }

                                for (int i = 0; i < Menu_Utama.idRM.length; i++) {
                                    LatLng latLng = new LatLng(latitudeObjek[i], longitudeObjek[i]);

                                    TextView text = new TextView(DetailRumahMakan.this);
                                    //text.setBackgroundResource(R.drawable.amu_bubble_mask);
                                    text.setText(namaRM[i]);
                                    text.setPadding(5, 5, 5, 5);
                                    //text.setTextColor(Color.BLACK);
                                    IconGenerator generator = new IconGenerator(DetailRumahMakan.this);
                                    //generator.setBackground(getDrawable(R.drawable.amu_bubble_mask));
                                    //generator.setColor(Color.BLUE);
                                    generator.setContentView(text);
                                    Bitmap icon = generator.makeIcon();


                                    String tampilJarak = df.format(jarakObj[i]);

                                    map.addMarker(new MarkerOptions().position(
                                            //new LatLng(latitudeObjek[i], longitudeObjek[i])).snippet(tampilJarak+" km").title(namaStudio[i])).setTag(idStudio[i]);
                                            new LatLng(latitudeObjek[i], longitudeObjek[i])).icon(BitmapDescriptorFactory.fromBitmap(icon))).setTag(Menu_Utama.idRM[i]);
                                    //marker.setTag(idStudio[i]);

                                    /*
                                    Marker marker = new Marker();
                                    marker.setTitle(namaStudio[i]);
                                    marker.setPosition(new LatLng(latitudeObjek[i], longitudeObjek[i]));
                                    marker.setTag(idStudio[i]);
                                    map.addMarker(new MarkerOptions(marker));
                                    */
                                }
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        DetailRumahMakan.idRM = marker.getTag().toString();
                                        startActivity(new Intent(DetailRumahMakan.this, DetailRumahMakan.class));
                                        return false;
                                    }
                                });

                                final double latitude = jsonObject.getDouble("latitude");
                                final double longitude = jsonObject.getDouble("longitude");
                                // final String noTlp = jsonObject.getString("kontak");
                                btnRute.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {
                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);
                                            }

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                btnKoment.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {
                                            List_Komentar.idRM = idRM;
                                            startActivity(new Intent(DetailRumahMakan.this, List_Komentar.class));
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                btnHarga.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {

                                            startActivity(new Intent(DetailRumahMakan.this, ListRating.class));
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                btnvote.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        try {

                                            startActivity(new Intent(DetailRumahMakan.this, RatingActivity.class));
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
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
                                    dataRM();
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
                                    dataRM();
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
                                    dataRM();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
    }

    public double jarak(Location dari, Location sampai) {
        double hasil = 0.0;
        double jarak = 0.0;
        jarak = dari.distanceTo(sampai);
        hasil = jarak / 1000;
        return hasil;
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLatitude();
        latitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //map.setMyLocationEnabled(true);
        //map.setIndoorEnabled(true);
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),DetailRumahMakan.this)) {
            getLocation();
            map.setMyLocationEnabled(true);
        }
        else
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),DetailRumahMakan.this);
        }
        try {
            dataRM();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public  boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }
    public  void requestPermission(String strPermission,int perCode,Context _c,Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Toast.makeText(this, "Gps Enabled", Toast.LENGTH_SHORT)
                                .show();
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            // cekKoneksi();
        }

        return location;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
