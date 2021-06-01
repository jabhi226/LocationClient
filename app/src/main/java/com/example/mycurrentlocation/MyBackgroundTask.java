package com.example.mycurrentlocation;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MyBackgroundTask extends AsyncTask<String,Void,String> {

    Context context;
    public MyBackgroundTask(Context ctx){
        context = ctx;
    }


    @Override
    protected String doInBackground(String... parameter) {
        String lat = parameter[0];
        String lon = parameter[1];
        String driver_UN = parameter[2];

//        String link = "http://192.168.43.17:8080/location/driver_location.php";
        String link = "http://192.168.0.103:8080/location/driver_location.php";
//        String link = "http://10.0.2.2:8080/location/driver_location.php";

        try{
            String data = URLEncoder.encode("latitude","UTF-8")+"="
                    +URLEncoder.encode(lat,"UTF-8")
                    +"&"
                    +URLEncoder.encode("longitude","UTF-8")+"="
                    +URLEncoder.encode(lon,"UTF-8")
                    +"&"
                    +URLEncoder.encode("driver_username","UTF-8")+"="
                    +URLEncoder.encode(driver_UN,"UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            //conn.setDoInput(true);

            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            }
            catch (IOException e) {
                return "Invalid IP Adddress";
            }

            outputStreamWriter.write(data);
            outputStreamWriter.flush();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                break;
            }

            return stringBuilder.toString();
        }
        catch (MalformedURLException e) {
            return "MalformedURLException";
        }
        catch (UnsupportedEncodingException e) {
            return "UnsupportedEncodingException";
        }
        catch (IOException e) {
            return "IOException";
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        //Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
