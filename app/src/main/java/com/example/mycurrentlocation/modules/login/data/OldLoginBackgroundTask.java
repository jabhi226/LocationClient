package com.example.mycurrentlocation.modules.login.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mycurrentlocation.modules.home.ui.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class OldLoginBackgroundTask extends AsyncTask<String,Void,String> {

    String username;
    Activity activity;
    Context context;
    AlertDialog alertDialog;

    public OldLoginBackgroundTask(Context ctx, Activity activity){
        context = ctx;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... parameters) {
        username = parameters[0];
        String password = parameters[1];

//        String link = "http://192.168.43.17:8080/location/Login.php";
        String link = "http://192.168.0.103:8080/location/Login.php";
//        String link = "http://10.0.2.2:8080/location/Login.php";

        try {
            String data = URLEncoder.encode("username","UTF-8")+"="
                    +URLEncoder.encode(username,"UTF-8")+"&"
                    +URLEncoder.encode("password","UTF-8")+"="
                    +URLEncoder.encode(password,"UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            }
            catch (IOException e) {
                return "Server is Off";
            }
            outputStreamWriter.write(data);
            outputStreamWriter.flush();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                Log.e("doInBackground", "stringBuilder.append(line): "+line);
            }
            return stringBuilder.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        alertDialog.dismiss();
        if (s.equals(username)){
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("driver_username_key",username);
            activity.startActivity(intent);
        }
        else{
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
        }
    }
}