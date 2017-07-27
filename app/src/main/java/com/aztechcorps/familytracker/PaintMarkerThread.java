/*
package com.aztechcorps.familytracker;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PaintMarkerThread implements Runnable {
    Context context;
    PaintMarkerThread(Context ctx){
        this.context = ctx;
    }
    @Override
    public void run() {
        try {
            while (true) {
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> markers = ((MainActivity) context).markers;
                        for (int i = 0; i < markers.size(); i++) {
                            Toast.makeText(context, markers.get(i), Toast.LENGTH_SHORT).show();
                            String[] marker = markers.get(i).split(" ");
                            String[] latlng = marker[0].split("|");
                            if(!latlng[0].equals(""))
                            ((MainActivity) context).setFamilyMarker(new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1])),
                                    marker[1], marker[2]);
                        }
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
*/
