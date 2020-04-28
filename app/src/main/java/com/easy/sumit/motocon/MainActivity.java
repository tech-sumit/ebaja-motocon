package com.easy.sumit.motocon;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity{
    private static final String TAG = MainActivity.class.getSimpleName();

    private double latitude = 0;
    private double longitude = 0;
    private boolean isLocationUpdateStarted=false;
    private TextView latitude_text, longitude_text;
    private GPSTracker gps;
    private Handler handler;
    private Runnable runnableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude_text = (TextView) findViewById(R.id.textViewLatitude);
        longitude_text = (TextView) findViewById(R.id.textViewLongitude);

        gps = new GPSTracker(MainActivity.this);
    }
    private void startLocationUpdates(){
        isLocationUpdateStarted=true;
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                if(gps.canGetLocation()){
                    double lat= gps.getLatitude();
                    double lon= gps.getLongitude();
                    Log.i("LocationUpdates","Data:\nlongitude:"+lon+"\nlatitude:"+lat);
                    if(lat!=latitude||lon!=longitude){
                        longitude=lon;
                        latitude=lat;
                        latitude_text.setText("Lat:"+latitude);
                        longitude_text.setText("Lon:"+longitude);
                        sendToServer();
                        Toast.makeText(getApplication(),
                                "Your Location is - \nLat: " + latitude + "\nLong: " + longitude,
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    gps.showSettingsAlert();
                }
                handler.postDelayed(runnableCode, 1500);
            }
        };
        handler.post(runnableCode);
    }
    private void sendToServer(){

        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                "http://lifelinebloodbank.esy.es/gps/setlocation.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("***RESPONCE***",""+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> stringMap=new HashMap<>();
                stringMap.put("lat",""+latitude);
                stringMap.put("lon",""+longitude);
                return stringMap;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!isLocationUpdateStarted){
            startLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
