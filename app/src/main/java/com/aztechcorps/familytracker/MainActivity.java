package com.aztechcorps.familytracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleMap mMap;
    private GoogleApiClient mClient;
    private boolean isFixed = true;
    LocationListener listener;

    public Boolean runGetUpdate = true;

    List<String> markers = new ArrayList<>();


    final CharSequence[] map_types = {"Normal", "Hybrid", "Satellite", "Terrain"};

    public AlertDialog.Builder selectMapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSharedPreferences("fence", Context.MODE_PRIVATE)
                .edit().clear().apply();
        /*LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        while(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            builder.setTitle("Error");
            builder.setCancelable(false);
            builder.setMessage("GPS not available. Turn on Location Services and reload.");
            builder.setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }*/

        selectMapType = new AlertDialog.Builder(MainActivity.this);
        selectMapType.setTitle("Select map title");
        selectMapType.setSingleChoiceItems(map_types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (map_types[which].equals("Normal")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (map_types[which].equals("Hybrid")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else if (map_types[which].equals("Satellite")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (map_types[which].equals("Terrain")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                dialog.dismiss();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        map_rad_grp = new RadioGroup(this);
        RadioButton mapNormal = new RadioButton(this);
        RadioButton mapHybrid = new RadioButton(this);
        RadioButton mapSatellite = new RadioButton(this);
        RadioButton mapTerrain = new RadioButton(this);

        mapNormal.setText("Normal");
        mapHybrid.setText("Hybrid");
        mapSatellite.setText("Satellite");
        mapTerrain.setText("Terrain");

        map_rad_grp.removeAllViews();

        map_rad_grp.addView(mapNormal);
        map_rad_grp.addView(mapHybrid);
        map_rad_grp.addView(mapSatellite);
        map_rad_grp.addView(mapTerrain);

        mapNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        mapHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        mapSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        mapTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected)
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });


        final SharedPreferences sp = getSharedPreferences("myLoc", MODE_PRIVATE);
        //Toast.makeText(MainActivity.this, sp.getBoolean("init", false)+"", Toast.LENGTH_SHORT).show();
        if (!sp.contains("phone")) {
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
            final EditText tvName = new EditText(MainActivity.this);
            tvName.setPadding(10, 10, 10, 10);
            tvName.setHint("Enter name");
            tvName.setLayoutParams(lp);
            final EditText tvPhone = new EditText(MainActivity.this);
            tvPhone.setPadding(10, 10, 10, 10);
            tvPhone.setHint("Enter phone");
            tvPhone.setLayoutParams(lp);
            ll.addView(tvName);
            ll.addView(tvPhone);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enter your name and phone")
                    .setView(ll)
                    .setCancelable(false)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!tvName.getText().toString().equals("") && !tvPhone.getText().toString().equals("")) {
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("phone", tvPhone.getText().toString());
                                ed.putString("name", tvName.getText().toString());
                                ed.putBoolean("init", true);
                                ed.apply();
                                setMember s = new setMember(MainActivity.this);
                                s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvPhone.getText().toString(), tvName.getText().toString());
                                if (isConnected) {
                                    getUpdate getUpdate = new getUpdate(MainActivity.this);
                                    getUpdate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    Intent intent = new Intent(MainActivity.this, FenceService.class);
                                    startService(intent);
                                    ranOnce = false;
                                }
                            }
                        }
                    })
                    .show();
        }

        namesList.add(0, "Show all");
        getSharedPreferences("currentCam", MODE_PRIVATE)
                .edit().putString("loc", "all").apply();
    }

    Boolean ranOnce = true;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    HashMap<String, Marker> markerList = new HashMap<>();
    ArrayList<String> namesList = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                showMyLoc();
                break;
            case R.id.menu_map_type:
                if (isConnected) {
                    selectMapType.show();
                }
                break;
            case R.id.menu_someone:
                if (isConnected && namesList.size() > 0) {
                    //Toast.makeText(MainActivity.this, namesList.get(0), Toast.LENGTH_SHORT).show();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, namesList);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Choose someone")
                            .setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (namesList.get(which).equals("Show all")) {
                                        getSharedPreferences("currentCam", MODE_PRIVATE)
                                                .edit().putString("loc", "all").apply();
                                        for (int i = 1; i < namesList.size(); i++) {
                                            String phone = namesList.get(i).split(" - ")[1];
                                            markerList.get(phone).setVisible(true);
                                        }
                                    } else {
                                        LatLng latLng = markerList.get(namesList.get(which).split(" - ")[1]).getPosition();
                                        getSharedPreferences("currentCam", MODE_PRIVATE)
                                                .edit().putString("loc", latLng.latitude + " " + latLng.longitude).apply();
                                        for (int i = 1; i < namesList.size(); i++) {
                                            String phone = namesList.get(i).split(" - ")[1];
                                            markerList.get(phone).setVisible(false);
                                        }
                                        markerList.get(namesList.get(which).split(" - ")[1]).setVisible(true);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
        }
        return true;
    }

    private void showMyLoc() {
        SharedPreferences sp = getSharedPreferences("myLoc", MODE_PRIVATE);
        String lastLoc = sp.getString("lastLoc", "");
        String[] arr = lastLoc.split(" ");
        String loc = getSharedPreferences("currentCam", MODE_PRIVATE).getString("loc", "");

        if (lastLoc.equals("") && loc.equals("")) {
            Toast.makeText(MainActivity.this, "Not ready yet", Toast.LENGTH_SHORT).show();
        } else {
            if (loc.equals("all")) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                        .target(new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1])))
                        .tilt(55)
                        .zoom(16)
                        .build()));
            } else {
                String[] loc_arr = loc.split(" ");
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                        .target(new LatLng(Double.parseDouble(loc_arr[0]), Double.parseDouble(loc_arr[1])))
                        .tilt(55)
                        .zoom(16)
                        .build()));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mClient.connect();
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setBuildingsEnabled(true);
    }

    RadioGroup map_rad_grp;
    Boolean isConnected = false;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            setLastKnownLoc();
        }
        else {

            startLocationUpdates();
            Intent intent = new Intent(MainActivity.this, getSetUpdateLocation.class);
            startService(intent);
        }
        if(!isThreadRunning) {
            Thread t = new Thread(new checkGPSThread(MainActivity.this));
            t.start();
            isThreadRunning = true;
        }

        isConnected = true;

       /* PaintMarkerAsync paintMarker = new PaintMarkerAsync(MainActivity.this);
        paintMarker.execute();*/
        if(getSharedPreferences("myLoc", MODE_PRIVATE).contains("init") && ranOnce) {
            //Toast.makeText(MainActivity.this, "here", Toast.LENGTH_SHORT).show();
            getUpdate getUpdate = new getUpdate(MainActivity.this);
            getUpdate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Intent intent = new Intent(MainActivity.this, FenceService.class);
            startService(intent);
        }
    }

    boolean isThreadRunning = false;
    private void setLastKnownLoc() {
        SharedPreferences sp = getSharedPreferences("myLoc", MODE_PRIVATE);
        String lastLoc = sp.getString("lastLoc", "");
        String[] arr = lastLoc.split(" ");
        if(lastLoc.equals("")) {
            markers.add("0.0|0.0" + " Me " + "-");
            //setFamilyMarker(new LatLng(0.0, 0.0), "Me", "");
        }
        else
            markers.add(Double.parseDouble(arr[0])+"|"+ Double.parseDouble(arr[1]) + " Me " + "-");
            //setFamilyMarker(new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1])), "Me", "");

    }

    public void startLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setFastestInterval(2000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Boolean f = false;
                for(int i = 0; i< markers.size(); i++){
                    if(markers.get(i).indexOf("Me -") > 0){
                        markers.remove(i);
                        markers.add(location.getLatitude()+"|"+ location.getLongitude() + " Me "+ "-");
                        f = true;
                    }
                }
                if(!f){
                    markers.add(location.getLatitude()+"|"+ location.getLongitude() + " Me "+ "-");
                }
                //setFamilyMarker(new LatLng(location.getLatitude(), location.getLongitude()), "Me", "");
            }
        });
        //setFamilyMarker(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()), "Me", "");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Me");
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker)));
        //mMap.clear();
        mMap.addMarker(markerOptions);
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCamera(latLng)));
    }


    /*public void setFamilyMarker(LatLng latLng, String name, String phone){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(name + " " + phone);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker)));
        mMap.clear();
        mMap.addMarker(markerOptions);
        if(isFirst) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCamera(latLng)));
            isFirst = false;
        }
    }

    private CameraPosition getCamera(LatLng latLng){
        return CameraPosition.builder()
                .target(latLng)
                .zoom(16)
                .build();
    }*/

    @Override
    protected void onDestroy() {
        getSharedPreferences("fence", Context.MODE_PRIVATE)
                .edit().clear().apply();
        runGetUpdate = false;
        super.onDestroy();
    }
}
