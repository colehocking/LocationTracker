package com.example.colehocking.locationtracker4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String tag = "MainActivity";
    private static final int FINE_LOCATION = 11;

    Location location;
    LocationManager lcMgr;
    Criteria cri;
    Button startBtn;
    Button finishBtn;
    EditText kMS; //To edit K ms update
    TextView prov; // provider display
    TextView sLat; //start latitude
    TextView sLon; //start longitude
    TextView eLat; //current/final latitude
    TextView eLon; //current/final longitude
    TextView distance; //distance traveled
    TextView refresh; //description for k ms update



    //Update Refresh time and distance
    private long k = 2000; //2000ms-> 2s
    private float min_dist = 1; //1 meter

    private int startCtr = 0;
    private int updateCtr = 0;


    private double latA = 0.0;
    private double lonA = 0.0;
    private double latUpdate = 0.0;
    private double lonUpdate = 0.0;
    private double latB = 0.0;
    private double lonB = 0.0;

    private String provider;

    private float uResults = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lcMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        cri = new Criteria();
        provider = lcMgr.getBestProvider(cri, true);
        //Log.i(tag, "provider: " + provider);


        // Create buttons and text views
        startBtn = (Button)(findViewById(R.id.startBtn));
        finishBtn = (Button)(findViewById(R.id.finishBtn));
        kMS = (EditText)(findViewById(R.id.kMS));
        prov = (TextView) (findViewById(R.id.provider));
        sLat = (TextView) (findViewById(R.id.startLat));
        sLon = (TextView) (findViewById(R.id.startLon));
        eLat = (TextView) (findViewById(R.id.updateLat));
        eLon = (TextView) (findViewById(R.id.updateLon));
        distance = (TextView) (findViewById(R.id.distance));
        refresh = (TextView) (findViewById(R.id.refresh));

        refresh.setText("Location will be updated every " + k + " ms");


        // Start Button is clicked

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Start button clicked");

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION); }

                if(provider != null && !provider.equals("")) {
                    updateCtr = 0; //reset distance for onLocationChanged(location);
                    uResults = 0;
                    location = lcMgr.getLastKnownLocation(provider);
                    lcMgr.requestLocationUpdates(provider, k, min_dist, MainActivity.this);
                    Log.i(tag, "K = " + k);
                    prov.setText("Location provider: " + provider);

                    if(location != null) {

                            latA = location.getLatitude();
                            lonA = location.getLongitude();
                            sLat.setText("Start Latitude: " + latA);
                            sLon.setText("Start Longitude: " + lonA);
                            startCtr ++;

                            //Start button is clicked again after onCreate; reset distance display
                            if(startCtr > 0) {
                                distance.setText("Distance Traveled: " + 0.0 + " meters");
                            }

                    }
                    else {
                        Log.i(tag, "location = null");

                            sLat.setText("Null");
                            sLon.setText("Null");

                    }
                }
                else {
                    //Log.i(tag, "provider = null");
                    prov.setText("Null provider");
                }
            }
        });

        // Finish button is clicked

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Finish button clicked");

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION); }


                if(location != null) {
                        lcMgr.removeUpdates(MainActivity.this);
                        latB = latUpdate;
                        lonB = lonUpdate;
                        eLat.setText("Final Latitude: " + latB);
                        eLon.setText("Final Longitude " + lonB);

                }
                else {
                        eLat.setText("null");
                        eLon.setText("null");
                        distance.setText("Cannot calculate distance. Location is null.");
                    }
            }
        });

        kMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if(s.length() != 0) {
                    String userIpt = kMS.getText().toString();
                    try {
                        long checkK = Long.parseLong(userIpt);
                        if (checkK >= 100) {
                            //code requires this block in every method that requests location info
                            if (ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(MainActivity.this, new
                                        String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        FINE_LOCATION); }
                            //------------------------------------------------------------------

                            if(provider != null && !provider.equals("")) {
                                k = checkK;
                                refresh.setText("Location will be updated every " + k + " ms");
                                lcMgr.requestLocationUpdates(provider, k, min_dist,
                                        MainActivity.this);
                                //Log.i(tag, "K = " + k);
                            }
                            else {
                                refresh.setText("Provider is currently null.");
                            }

                        }
                        else {
                            //To protect processor
                            refresh.setText("Use a value greater than 100. Maintaining " + k +
                                    " ms");
                        }
                    }
                    catch (NumberFormatException nfe) {
                            nfe.printStackTrace();

                    }

                }
                else{
                    k = 2000;

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case FINE_LOCATION: {

                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(tag, "permission granted");
                }
                else {
                    Log.i(tag, "permission denied");
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        float [] results1 = new float [1];
        float [] results2 = new float [1];
        double prevLat;
        double prevLon;

        if (updateCtr == 0) {
            latUpdate = location.getLatitude();
            lonUpdate = location.getLongitude();
            Location.distanceBetween(latA, lonA, latUpdate, lonUpdate, results1);
            distance.setText("Distance Traveled: " + results1[0] + " meters");
            uResults += results1[0];
            updateCtr++;
        }

        else {
            prevLat = latUpdate;
            prevLon = lonUpdate;
            //Log.i(tag, "prevLat = " + prevLat);
            //Log.i(tag, "prevLon = " + prevLon);
            latUpdate = location.getLatitude();
            lonUpdate = location.getLongitude();
            //Log.i(tag, "UpdateLat = " + latUpdate);
            //Log.i(tag, "UpdateLon = " + lonUpdate);
            Location.distanceBetween(prevLat, prevLon, latUpdate, lonUpdate, results2);
            uResults += results2[0];
            //Log.i(tag, "uResults = " + uResults);
            distance.setText("Distance Traveled: " + uResults + " meters");


        }


        eLat.setText("Current Latitude: " + latUpdate);
        eLon.setText("Current Longitude: " + lonUpdate);



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}