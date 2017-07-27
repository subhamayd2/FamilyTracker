package com.aztechcorps.familytracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getUpdate extends AsyncTask<Void, Void, Void> {
    Context context;
    private Boolean running = true;
    List<String> markers;
    GoogleMap mMap;

    String mobile;
    Map<String, Marker> markerList;
    List<String> namesList;
    getUpdate(Context ctx){
        this.context = ctx;
        this.mMap = ((MainActivity)context).mMap;
        this.markerList = ((MainActivity)context).markerList;
        this.namesList = ((MainActivity)context).namesList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(((MainActivity)context).runGetUpdate) {
            SharedPreferences sp = context.getSharedPreferences("myLoc", Context.MODE_PRIVATE);
            String uphone = sp.getString("phone", "");
            String login_url = "http://location.aztechcorps.com/get-location.php";
            try {
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("sphone", "UTF-8") + "=" + URLEncoder.encode(uphone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                SharedPreferences spData = context.getSharedPreferences("fdata", Context.MODE_PRIVATE);
                spData.edit().putString("data", result).apply();
                this.publishProgress((Void)null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        isFirst = true;
        markers = ((MainActivity)context).markers;
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        Notification n = new Notification.Builder(context).setTicker("Family Tracker is on")
                .setContentTitle("Family Tracker")
                .setContentText("Click to open app")
                .setSmallIcon(R.drawable.logo)
                //.addAction(R.mipmap.ic_launcher, "Action1", pi)
                .setContentIntent(pi).getNotification();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, n);

        SharedPreferences sp = context.getSharedPreferences("myLoc", Context.MODE_PRIVATE);
        mobile = sp.getString("phone", "");
    }

    int markerCount, markerNum = 0;

    @Override
    protected void onProgressUpdate(Void... values) {
        //Toast.makeText(context, "Hey", Toast.LENGTH_SHORT).show();
        SharedPreferences spData = context.getSharedPreferences("fdata", Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = new JSONObject(spData.getString("data", ""));
            JSONArray jsonArray = jsonObject.getJSONArray("loc");
            markerCount = jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray loc = jsonArray.optJSONArray(i);
                String phone = loc.optString(1);
                String name = loc.optString(2);
                //Toast.makeText(context, name + " " + phone, Toast.LENGTH_SHORT).show();
                String loc_string = loc.optString(3);
                String[] arr = loc_string.split(" ");
                Boolean f = false;
                /*for(int j = 0; j < markers.size(); j++){
                    if(markers.get(j).indexOf(phone) > 0){
                        ((MainActivity)context).markers.remove(i);
                        ((MainActivity)context).markers.add(Double.parseDouble(arr[0])+"|"+ Double.parseDouble(arr[1])+" "+ name+" "+phone);
                        f = true;
                    }
                }
                if(!f){
                    ((MainActivity)context).markers.add(Double.parseDouble(arr[0])+"|"+ Double.parseDouble(arr[1])+" "+ name+" "+phone);
                }*/
                //((MainActivity)context).markers.add(Double.parseDouble(arr[0])+"|"+ Double.parseDouble(arr[1])+" "+ name+" "+phone);
                if(!arr[0].equals(""))
                setFamilyMarker(new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1])),
                        name, phone);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void s) {
        running = false;
        isFirst = true;
    }

    private Boolean isFirst = true;
    public void setFamilyMarker(LatLng latLng, String name, String phone){
        Marker mMarker;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(name);
        markerOptions.snippet(phone);
        markerOptions.position(latLng);
        //Toast.makeText(context, phone + " " + mobile, Toast.LENGTH_SHORT).show();
        if(phone.equals(mobile))
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.marker)));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_red)));

        if(markerList.size() > 0 && markerList.containsKey(phone)){
            //Toast.makeText(context, markerList.size(), Toast.LENGTH_SHORT).show();
            markerList.get(phone).setPosition(latLng);
        }
        else {
            mMarker = mMap.addMarker(markerOptions);
            markerList.put(phone, mMarker);
            namesList.add(name + " - " + phone);
        }
        //mMap.addMarker(markerOptions);
        if(isFirst) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCamera(latLng)));
            isFirst = false;
        }
    }

    private CameraPosition getCamera(LatLng latLng){
        return CameraPosition.builder()
                .target(latLng)
                .tilt(55)
                .zoom(16)
                .build();
    }

    @Override
    protected void onCancelled() {
        isFirst = true;
        super.onCancelled();
    }
}
