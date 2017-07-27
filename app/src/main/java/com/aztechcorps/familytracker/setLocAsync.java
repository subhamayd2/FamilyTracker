package com.aztechcorps.familytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class setLocAsync extends AsyncTask<String, Void, Void> {
    Context context;
    String lastLoc;
    setLocAsync(Context ctx) { this.context = ctx; }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(context, "Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String phone = params[0];
        String loc = params[1];
        lastLoc = loc;

        String login_url = "http://location.aztechcorps.com/update-location.php";
        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")+"&"
                    +URLEncoder.encode("loc", "UTF-8")+"="+URLEncoder.encode(loc, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
            String result = "";
            String line;
            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = context.getSharedPreferences("myLoc", Context.MODE_PRIVATE);
        sp.edit().putString("lastLoc", lastLoc).apply();
    }
}
