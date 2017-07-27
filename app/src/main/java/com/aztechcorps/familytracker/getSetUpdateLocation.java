package com.aztechcorps.familytracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class getSetUpdateLocation extends Service {
    private GoogleApiClient mClient;
    String loc;

    public getSetUpdateLocation() {
    }

    /*public class updateThread implements Runnable {
        @Override
        public void run() {
            boolean err = false;
            SharedPreferences sp = getSharedPreferences("myLoc", MODE_PRIVATE);
            //while (!err) {
                try {
                    //Thread.sleep(5000);
                    URL url = new URL("http://location.aztechcorps.com/update-location.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    String data = String.format("phone=%s&loc=%s",
                            URLEncoder.encode("9830274433", "utf-8"),
                            URLEncoder.encode("a", "utf-8"));
                    OutputStream o = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(o));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    o.close();
                    conn.disconnect();
                } catch (Exception e) {
                    err = true;
                    e.printStackTrace();
                }
            //}
        }
    }*/


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //super.onCreate();

        final SharedPreferences sp = getSharedPreferences("myLoc", MODE_PRIVATE);
        //final String phone = sp.getString("phone", "");
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest request = new LocationRequest();
                        request.setInterval(6000);
                        request.setFastestInterval(3000);
                        if (!(ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    loc = location.getLatitude() + " " + location.getLongitude();
                                    String phone = sp.getString("phone", "");
                                    //Toast.makeText(getSetUpdateLocation.this, loc + " " + phone, Toast.LENGTH_SHORT).show();
                                    setLocAsync setLocAsync = new setLocAsync(getSetUpdateLocation.this);
                                    //if(setLocAsync.getStatus() != AsyncTask.Status.RUNNING || setLocAsync.getStatus() == AsyncTask.Status.FINISHED)
                                    setLocAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,phone, loc);
                                }
                            });
                        }
                        /*Thread t = new Thread(new updateThread());
                        t.start();*/


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        mClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
