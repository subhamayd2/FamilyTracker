package com.aztechcorps.familytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by User on 30-Jul-16.
 */
public class setMember extends AsyncTask<String, Void, String> {
    Context context;
    Dialog load;

    setMember(Context ctx){ this.context = ctx; }

    @Override
    protected String doInBackground(String... params) {
        String phone = params[0];
        String name = params[1];

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
                    +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8") + "&"
                    +URLEncoder.encode("flag", "UTF-8")+"="+URLEncoder.encode("setMember", "UTF-8");
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
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        load = ProgressDialog.show(context, "", "Adding your info...");
    }

    @Override
    protected void onPostExecute(String s) {
        load.dismiss();
        if(s != null){
            new AlertDialog.Builder(context)
                    .setMessage(s)
                    .show();
        }
    }
}
