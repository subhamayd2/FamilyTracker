/*
package com.aztechcorps.familytracker;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class PaintMarkerAsync extends AsyncTask<Void, Void, Void> {
    private GoogleMap mMap;
    Context context;
    PaintMarkerAsync(Context ctx){
        this.context = ctx;
        mMap = ((MainActivity)context).mMap;
    }

    String[] marker;
    String[] latlng;
    @Override
    protected Void doInBackground(Void... params) {
        ((MainActivity) context).markers.add("0.0|0.0 Heya 987654");
        try {
            //while (true) {
                List<String> markers = ((MainActivity) context).markers;
                for (int i = 0; i < markers.size(); i++) {
                    //Toast.makeText(context, markers.get(i), Toast.LENGTH_SHORT).show();
                    marker = markers.get(i).split(" ");
                    latlng = marker[0].split("|");
                    this.publishProgress((Void) null);
                    //Sleep(1000);
                }
            //}
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        //Toast.makeText(context, marker[1], Toast.LENGTH_SHORT).show();
        if(!latlng[0].equals(""))
        setFamilyMarker(new LatLng(Double.parseDouble("0"), Double.parseDouble("0")),
                marker[1], marker[2]);
    }

    private Boolean isFirst = true;
    public void setFamilyMarker(LatLng latLng, String name, String phone){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(name + " " + phone);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.marker)));
        //mMap.clear();
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
    }
}
*/
