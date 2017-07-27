package com.aztechcorps.familytracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class checkGPSThread implements Runnable {
    Context context;
    GoogleApiClient mClient;
    boolean isNotified = false;
    boolean isConnected = false;


    checkGPSThread(Context ctx) {
        this.context = ctx;
    }

    @Override
    public void run() {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            while (true) {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!isNotified) {
                                Toast.makeText(context, "GPS not enabled", Toast.LENGTH_LONG).show();
                                isNotified = true;
                            }
                        }
                    });
                }
                else {
                    if (check()){
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Activity a = (Activity)context;
                                ((MainActivity)a).startLocationUpdates();
                                Intent intent = new Intent(context, getSetUpdateLocation.class);
                                context.startService(intent);
                            }
                        });
                    }
                }
                Thread.sleep(2000);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private boolean check(){
        mClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location myLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
                        if (myLoc != null){
                            isConnected = true;
                        }
                        else
                            isConnected = false;
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
        return isConnected;
    }
}
