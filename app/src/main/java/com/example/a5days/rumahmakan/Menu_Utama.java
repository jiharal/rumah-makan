package com.example.a5days.rumahmakan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a5days.rumahmakan.adapter.ListImageAdapter;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static com.example.a5days.rumahmakan.Login.my_shared_preferences;
import static com.example.a5days.rumahmakan.Login.session_status;
import static com.example.a5days.rumahmakan.MainActivity.TAG_ID;
import static com.example.a5days.rumahmakan.MainActivity.TAG_USERNAME;

/**
 * Created by 5Days on 20/11/2017.
 */

public class Menu_Utama extends AppCompatActivity  implements LocationListener, OnMapReadyCallback {



    //public static String ipServer = "192.168.100.7";
    private GoogleMap map;
    private Spinner filter;
    private EditText pencarian;
    private ListView rumahMakan;
    //private GridAdapter gridAdapter;
    private ArrayAdapter papanFilter;
    private String[] jenisFilter = {"Rumah Makan", "Menu"};
    //{"Nama Rumah Makan", "Menu" };

    private ListImageAdapter listImageAdapter;
    private ClassItem[] itemClass;

    static String[] idRM;
    static String[] namaRM;


    private Dialog loading, gagal;
    private Button btnCoba, btn_logout, btn_daftarrm, btnrec;
    private TextView statusLoading;
    private ImageButton btnInfo;
    private double latUser;
    private double longUser;
    public static double[] latitudeObjek;
    public static double[] longitudeObjek;
    public static double[] jarakObj;
    public static Location lokasiPengguna;
    // GPS
    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    public static  Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    private static final long MAX_DISATANCE_CHANGE_FOR_UPDATES = 50000;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = (1000 * 60 * 1) / 60; // 1
    // second

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    DecimalFormat df = new DecimalFormat("#.##");

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;



    Boolean session = false;
    public static String id, username;
    public static final String my_shared_preferences = "usr";
    public static final String session_status = "session_status";

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_utama);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(this);




        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);

//        if (session) {
//           Intent intent = new Intent(Menu_Utama.this, DetailRumahMakan.class);
//            intent.putExtra(TAG_ID, id);
//            intent.putExtra(TAG_USERNAME, username);
//            finish();
//            startActivity(intent);
//        }
        btnrec = (Button) findViewById(R.id.btn_rec);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_daftarrm = (Button) findViewById(R.id.btn_daftarrm);
        pencarian = (EditText) findViewById(R.id.pencarian);
        filter = (Spinner) findViewById(R.id.filter);
        rumahMakan = (ListView) findViewById(R.id.list_rm);
        papanFilter = new ArrayAdapter(this, R.layout.list_item, jenisFilter);
        filter.setAdapter(papanFilter);
        //gridAdapter = new GridAdapter(this);
        //studio.setAdapter(gridAdapter);
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
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
        btnCoba = (Button) findViewById(R.id.btnCoba);

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(Menu_Utama.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
        btn_daftarrm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                Intent intent = new Intent(Menu_Utama.this, Daftar_Rm.class);
                finish();
                startActivity(intent);


            }
        });
        btnrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                //String id_user =  getIntent().getStringExtra(id);
//                Intent intent = new Intent(Menu_Utama.this, Activity_Rec.class);
//                finish();
//                startActivity(intent);

                try {
                    Menu_Utama.id = id;
                    Intent intent = new Intent(Menu_Utama.this, Activity_Rec.class);
                    finish();
                    startActivity(intent);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });



        pencarian.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    switch (filter.getSelectedItemPosition()) {
                        case 0:
                            HasilPencarian.kolom = "nama rmakan";
                            HasilPencarian.isi = pencarian.getText().toString();
                            break;
                        case 1:
                            HasilPencarian.kolom = "menu";
                            HasilPencarian.isi = pencarian.getText().toString();
                            break;
                        default:
                    }
                    startActivity(new Intent(Menu_Utama.this, HasilPencarian.class));
                    return true;
                }
                return false;
            }
        });

    }

    public void dataRM() throws Exception {
        getLocation();
        //Toast.makeText(this, getLatitude()+", "+getLongitude(), Toast.LENGTH_LONG).show();
        lokasiPengguna = new Location("");
        lokasiPengguna.setLatitude(getLatitude());
        lokasiPengguna.setLongitude(getLongitude());
        //LatLng lokasiUserAktif = new LatLng(-7.797068, 110.370529);
        LatLng lokasiUserAktif = new LatLng(getLatitude(), getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiUserAktif, 14));


        RequestParams params = new RequestParams();
        params.put("aksi", "data makan");

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
                                Toast.makeText(Menu_Utama.this, "belum ada data rumah makan ", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                idRM = new String[response.length()];
                                namaRM = new String[response.length()];



                                itemClass = new ClassItem[response.length()];
                                latitudeObjek = new double[response.length()];
                                longitudeObjek = new double[response.length()];
                                jarakObj = new double[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response
                                            .getJSONObject(i);
                                    idRM[i] = jsonObject.getString("id_rm");

                                    namaRM[i] = jsonObject.getString("nama_rm");



                                    latitudeObjek[i] = jsonObject.getDouble("latitude");
                                    longitudeObjek[i] = jsonObject.getDouble("longitude");
                                    Location lokObj = new Location("");
                                    lokObj.setLatitude(jsonObject.getDouble("latitude"));
                                    lokObj.setLongitude(jsonObject.getDouble("longitude"));
                                    jarakObj[i] = jarak(lokasiPengguna, lokObj);

                                    String tampilJarak = df.format(jarakObj[i]);
                                    itemClass[i] = new ClassItem(ServerClass.getUrl("rm/" + jsonObject.getString("gambar")), jsonObject.getString("nama_rm") + "\n" +  tampilJarak + " km" + "\n" ) ;

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
                                            obj1 = itemClass[l];
                                            obj2 = jarakObj[l];
                                            obj3 = latitudeObjek[l];
                                            obj4 = longitudeObjek[l];
                                            obj5 = namaRM[l];
                                            obj6 = idRM[l];


                                            itemClass[l] = itemClass[l + 1];
                                            jarakObj[l] = jarakObj[l + 1];
                                            latitudeObjek[l] = latitudeObjek[l + 1];
                                            longitudeObjek[l] = longitudeObjek[l + 1];
                                            idRM[l] = idRM[l + 1];
                                            namaRM[l] = namaRM[l + 1];

                                            itemClass[l + 1] = obj1;
                                            jarakObj[l + 1] = obj2;
                                            latitudeObjek[l + 1] = obj3;
                                            longitudeObjek[l + 1] = obj4;
                                            namaRM[l + 1] = obj5;
                                            idRM[l + 1] = obj6;


                                        }

                                    }

                                }

                                for (int i = 0; i < idRM.length; i++) {
                                    LatLng latLng = new LatLng(latitudeObjek[i], longitudeObjek[i]);

                                    TextView text = new TextView(Menu_Utama.this);
                                    //text.setBackgroundResource(R.drawable.amu_bubble_mask);
                                    text.setText(namaRM[i]);
                                    text.setPadding(5, 5, 5, 5);
                                    //text.setTextColor(Color.BLACK);
                                    IconGenerator generator = new IconGenerator(Menu_Utama.this);
                                    //generator.setBackground(getDrawable(R.drawable.amu_bubble_mask));
                                    //generator.setColor(Color.BLUE);
                                    generator.setContentView(text);
                                    Bitmap icon = generator.makeIcon();


                                    String tampilJarak = df.format(jarakObj[i]);

                                    map.addMarker(new MarkerOptions().position(
                                            //new LatLng(latitudeObjek[i], longitudeObjek[i])).snippet(tampilJarak+" km").title(namaStudio[i])).setTag(idStudio[i]);
                                            new LatLng(latitudeObjek[i], longitudeObjek[i])).icon(BitmapDescriptorFactory.fromBitmap(icon))).setTag(idRM[i]);
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
                                        startActivity(new Intent(Menu_Utama.this, DetailRumahMakan.class));
                                        return false;
                                    }
                                });
                                /*
                                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        DetailStudio.idStudio = marker.getTag().toString();
                                        startActivity(new Intent(MenuUtama.this, DetailStudio.class));
                                    }
                                });
                                */



                                listImageAdapter = new ListImageAdapter(Menu_Utama.this, R.layout.grid_list, itemClass);
                                rumahMakan.setAdapter(listImageAdapter);
                                rumahMakan.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(
                                            AdapterView<?> parent, View view,
                                            int position, long id) {
                                        // TODO Auto-generated method stub
                                        DetailRumahMakan.idRM = idRM[position];
                                        startActivity(new Intent(Menu_Utama.this, DetailRumahMakan.class));
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
    /*
     * GPS
     */
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setMessage("Apa anda ingin Exit?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        MainActivity.this.finish();
//                    }
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
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

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Function to get latitude
     */
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

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Menu_Utama.this.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }




    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }


    @Override
    public void onProviderDisabled(String provider) {
        showSettingsAlert();

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //map.setMyLocationEnabled(true);
        //map.setIndoorEnabled(true);
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),Menu_Utama.this)) {
            getLocation();
            map.setMyLocationEnabled(true);
        }
        else
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),Menu_Utama.this);
        }
        try {
            dataRM();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public  void requestPermission(String strPermission,int perCode,Context _c,Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }




    // Show All Content




}