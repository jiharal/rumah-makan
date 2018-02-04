package com.example.a5days.rumahmakan.vote;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.a5days.rumahmakan.R;

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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.a5days.rumahmakan.Menu_Utama.id;
import static com.example.a5days.rumahmakan.app.AppController.TAG;


/**
 * Created by DELL Inspiron on 11/17/2016.
 */

public class ListRating extends Activity {

    public static final int DIALOG_DOWNLOAD_JSON_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
Button rating;
    ArrayList<HashMap<String, Object>> MyArrList;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harga);

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Download JSON File
        new DownloadJSONFileAsync().execute();
        rating = (Button) findViewById(R.id.btnrating);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_JSON_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading.....");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    // Show All Content
    public void ShowAllContent()
    {
        // listView1
        final ListView lstView1 = (ListView)findViewById(R.id.listView1);
        lstView1.setAdapter(new ImageAdapter(ListRating.this,MyArrList));

    }



    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = new ArrayList<HashMap<String, Object>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, Object>> myArrList)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = myArrList;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub



            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_column, null);
            }

            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
//            imageView.getLayoutParams().height = 80;
//            imageView.getLayoutParams().width = 80;
//            imageView.setPadding(10, 10, 10, 10);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {
                //imageView.setImageBitmap((Bitmap)MyArr.get(position).get("ImageThumBitmap"));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
            // Click on Image
            rating.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                  //  String id = MyArr.get(position).get("id_user").toString();
//                    String username  = MyArr.get(position).get("username").toString();
                    String nama_rm = MyArr.get(position).get("nama_rm").toString();

//                    String id_rating = MyArr.get(position).get("id_rating").toString();
                    String rating = MyArr.get(position).get("rating").toString();

                   // String strImagePathFull = MyArr.get(position).get("ImagePathFull").toString();

                    Intent newActivity = new Intent(ListRating.this,RatingActivity.class);
//                    newActivity.putExtra("10",id);
//                    newActivity.putExtra("username", username);
                   newActivity.putExtra("nama_rm", nama_rm);

//                    newActivity.putExtra("id_rating", id_rating);
                    newActivity.putExtra("rating", rating);
                   // newActivity.putExtra("ImagePathFull", strImagePathFull);
                    startActivity(newActivity);
                }
            });



            // ColImgID
//            TextView txtImgID = (TextView) convertView.findViewById(R.id.usrnm);
          //  txtImgID.setPadding(5, 0, 0, 0);
//            txtImgID.setText(MyArr.get(position).get("10").toString()+":");

            // ColImgName
            TextView txtPicName = (TextView) convertView.findViewById(R.id.kmntar);
           // txtPicName.setPadding(5, 0, 0, 0);
            txtPicName.setText(MyArr.get(position).get("nama_rm").toString());



            // ColratingBar
            RatingBar Rating = (RatingBar) convertView.findViewById(R.id.ColratingBar);
         //   RatingActivity.setPadding(10, 0, 0, 0);
            Rating.setEnabled(false);
            Rating.setMax(5);
            Rating.setRating(Float.valueOf(MyArr.get(position).get("rating").toString()));


            return convertView;

        }

    }



    // Download JSON in Background
    public class DownloadJSONFileAsync extends AsyncTask<String, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub

            String url = "http://iddota.hol.es/rm/android/method.php";

            JSONArray data;
            try {
                data = new JSONArray(getJSONUrl(url));

                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for(int i = 0; i < data.length(); i++){
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, Object>();
//                   map.put("10", (String)c.getString("10"));
//                    map.put("username", (String)c.getString("username"));
                    //map.put("id_komentar", (String)c.getString("id_komentar"));
                    map.put("nama_rm", (String)c.getString("nama_rm"));
//                    map.put("id_rating", (String)c.getString("id_rating"));

                    map.put("rating", (String)c.getString("rating"));

                    //MyArrList.add(map);
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            ShowAllContent(); // When Finish Show Content
            dismissDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
            removeDialog(DIALOG_DOWNLOAD_JSON_PROGRESS);
        }


    }


    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    /***** Get Image Resource from URL (Start) *****/
//    private static final String TAG = "Image";
//    private static final int IO_BUFFER_SIZE = 4 * 1024;
//    public static Bitmap loadBitmap(String url) {
//        Bitmap bitmap = null;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//
//        try {
//            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
//
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
//            copy(in, out);
//            out.flush();
//
//            final byte[] data = dataStream.toByteArray();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            //options.inSampleSize = 1;
//
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
//        } catch (IOException e) {
//            Log.e(TAG, "Could not load Bitmap from: " + url);
//        } finally {
//            closeStream(in);
//            closeStream(out);
//        }
//
//        return bitmap;
//    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not close stream", e);
            }
        }
    }

//    private static void copy(InputStream in, OutputStream out) throws IOException {
//        byte[] b = new byte[IO_BUFFER_SIZE];
//        int read;
//        while ((read = in.read(b)) != -1) {
//            out.write(b, 0, read);
//        }
//    }
    /***** Get Image Resource from URL (End) *****/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}