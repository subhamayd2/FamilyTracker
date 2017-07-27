package com.aztechcorps.familytracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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


public class fenceAsync extends AsyncTask<Void, Void, String> {
    Context context;
    fenceAsync(Context ctx){ this.context = ctx; }
    @Override
    protected String doInBackground(Void... params) {
            String login_url = "http://location.aztechcorps.com/get-location.php";
            try {
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("sphone", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
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
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    Double myLat;
    Double myLng;

    @Override
    protected void onPostExecute(String s) {
        String mobile = context.getSharedPreferences("myLoc", Context.MODE_PRIVATE).getString("phone", "");
        String myloc_str = context.getSharedPreferences("myLoc", Context.MODE_PRIVATE).getString("lastLoc", "");
        //Toast.makeText(context, myloc_str, Toast.LENGTH_SHORT).show();
        myLat = Double.parseDouble(myloc_str.split(" ")[0]);
        myLng = Double.parseDouble(myloc_str.split(" ")[1]);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("loc");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray loc = jsonArray.optJSONArray(i);
                String phone = loc.optString(1);
                String name = loc.optString(2);
                String loc_string = loc.optString(3);
                if(!phone.equals(mobile)){
                    SharedPreferences sp = context.getSharedPreferences("fence", Context.MODE_PRIVATE);
                    Double lat = Double.parseDouble(loc_string.split(" ")[0]);
                    Double lng = Double.parseDouble(loc_string.split(" ")[1]);
                    Double dist = Math.sqrt((Math.pow(myLat-lat, 2)) + (Math.pow(myLng-lng,2)));
                    //Toast.makeText(context, dist + "", Toast.LENGTH_SHORT).show();
                    if(dist < 0.0006 && !sp.contains(phone)){
                        sp.edit().putBoolean(phone, true).apply();
                        Intent intent = new Intent(context, MainActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
                        Notification n = new Notification.Builder(context).setTicker(name + " is nearby")
                                .setContentTitle(name + " is somewhere nearby")
                                .setContentText("Open family tracker")
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                                .setSmallIcon(R.drawable.logo)
                                //.addAction(R.mipmap.ic_launcher, "Action1", pi)
                                .setContentIntent(pi).getNotification();
                        n.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(i+1, n);
                    }
                    else if(dist > 0.0006 && sp.contains(phone)){
                        sp.edit().remove(phone).apply();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
