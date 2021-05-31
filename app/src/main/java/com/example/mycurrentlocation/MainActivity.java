package com.example.mycurrentlocation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Timer timer;
    Button stopButton,startButton;
    TextView textView,infotext;
    String driver_username;
    Boolean counter = false;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);

        textView = (TextView) findViewById(R.id.textView4);
        infotext = (TextView) findViewById(R.id.info_textview);
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);
        stopButton.setVisibility(View.INVISIBLE);

        // this will pop-up alert box to allow permission (this will run only at the first launch)
        ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);

        //retriveing username of the driver
        driver_username = getIntent().getExtras().getString("driver_username_key","defaultKey");

        textView.setText("Uesename : "+driver_username);
        infotext.setText("Tap below button to Start");

        //try : start and stop service on touch listener
        final Drawable dr = getResources().getDrawable(R.drawable.login_btn);
        final Drawable dr1 = getResources().getDrawable(R.drawable.login_btn2);

        //start location service
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

                if(MotionEvent.ACTION_DOWN == event.getAction())
                {
                    counter = true;

                    Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator1.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator1.vibrate(30);
                    }

                    startButton.setTextSize(23);
                    layoutParams.width = width + 30;
                    layoutParams.height = height + 5;
                    startButton.setLayoutParams(layoutParams);
                    startButton.setBackground(dr);


                    infotext.setText("Location is beeing tracked");

                    /*
                    startButton.setTextSize(20);
                    layoutParams.width = width;
                    layoutParams.height = height;
                    startButton.setLayoutParams(layoutParams);

                    startButton.setTextSize(23);
                    layoutParams.width = width + 30;
                    layoutParams.height = height + 5;
                    startButton.setLayoutParams(layoutParams);

                     */
                }
                if(MotionEvent.ACTION_UP == event.getAction())
                {
                    startButton.setTextSize(20);
                    layoutParams.width = width;
                    layoutParams.height = height;
                    startButton.setLayoutParams(layoutParams);
                    startButton.setBackground(dr1);

                    /*startButton.setTextSize(23);
                    layoutParams.width = width + 30;
                    layoutParams.height = height + 5;
                    startButton.setLayoutParams(layoutParams);

                    startButton.setTextSize(20);
                    layoutParams.width = width;
                    layoutParams.height = height;
                    startButton.setLayoutParams(layoutParams);

                     */

                    stopButton.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.INVISIBLE);

                    timer = new Timer();
                    timer.schedule(new RepeaterClass(getApplicationContext()),0,5000);
                }
                return true;
            }
        });

        //stop location service
        stopButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

                if(MotionEvent.ACTION_DOWN == event.getAction())
                {
                    stopButton.setTextSize(23);
                    layoutParams.width = width + 30;
                    layoutParams.height = height + 5;
                    stopButton.setLayoutParams(layoutParams);
                    stopButton.setBackground(dr);

                    Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator1.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator1.vibrate(30);
                    }
                }
                if(MotionEvent.ACTION_UP == event.getAction())
                {
                    stopButton.setTextSize(20);
                    layoutParams.width = width;
                    layoutParams.height = height;
                    stopButton.setLayoutParams(layoutParams);
                    stopButton.setBackground(dr1);

                    stopButton.setVisibility(View.INVISIBLE);
                    startButton.setVisibility(View.VISIBLE);

                    infotext.setText("Tap below button to Start");

                    if(timer!=null){
                        timer.cancel();
                        timer=null;
                    }
                    if(counter)
                    {
                        counter = false;
                        Toast.makeText(getApplicationContext(),"Location Service Has Been Stoped",Toast.LENGTH_SHORT).show();

                        String lat = "0";
                        String lon = "0";

                        Context context1 = MainActivity.this;
                        MyBackgroundTask myBackgroundTask = new MyBackgroundTask(context1);
                        myBackgroundTask.execute(lat,lon,driver_username);
                    }
                }
                return true;
            }
        });
    }

    //onBackClick
    /*
    @Override
public void onBackPressed() {
    new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Closing Activity")
        .setMessage("Are you sure you want to close this activity?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }

    })
    .setNegativeButton("No", null)
    .show();
}
     */

    //.setIcon(android.R.drawable.ic_dialog_alert)

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //.setIcon(R.drawable.ic_icon_svg1)
                .setTitle("Location Tracking")
                .setMessage("Are you sure you want to Sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(timer!=null){
                            timer.cancel();
                            timer=null;
                        }

                        if(counter) {
                            counter = false;
                            Toast.makeText(getApplicationContext(), "Location Service Has Been Stoped", Toast.LENGTH_SHORT).show();

                            String lat = "0";
                            String lon = "0";

                            Context context1 = MainActivity.this;
                            MyBackgroundTask myBackgroundTask = new MyBackgroundTask(context1);
                            myBackgroundTask.execute(lat, lon, driver_username);
                        }
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    //this function will start the service
    /*
    public void findMyLocation(View view) {
        stopButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.INVISIBLE);

        timer = new Timer();
        timer.schedule(new RepeaterClass(getApplicationContext()),0,5000);

        android:onClick="findMyLocation"
    }


    //this function will stop service
    public void stopGettingLocation(View view) {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        Toast.makeText(getApplicationContext(),"Location Service Has Been Stoped",Toast.LENGTH_SHORT).show();

        String lat = "0";
        String lon = "0";

        Context context1 = MainActivity.this;
        MyBackgroundTask myBackgroundTask = new MyBackgroundTask(context1);
        myBackgroundTask.execute(lat,lon,driver_username);

        android:onClick="stopGettingLocation"
    }
*/

    //this will repeat the service
    class RepeaterClass extends TimerTask{

        EditText editText;
        Context context;
        Context context1 = MainActivity.this;

        public RepeaterClass(Context applicationContext) {
            context = applicationContext;
        }

        //MyLocationFinderClass will access the current location
        MyLocationFinderClass myLocationFinderClass = new MyLocationFinderClass(getApplicationContext());

        Handler handler = new Handler();
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //location will contain the current location attribute
                    Location location = myLocationFinderClass.getLocation();

                    if(location != null){
                        String lat = String.valueOf(location.getLatitude());
                        String lon = String.valueOf(location.getLongitude());

                        MyBackgroundTask myBackgroundTask = new MyBackgroundTask(context1);
                        myBackgroundTask.execute(lat,lon,driver_username);

                        editText = (EditText) findViewById(R.id.editText);
                        editText.setText(lat+","+lon);
                        editText.setVisibility(View.INVISIBLE);

                        //Toast.makeText(getApplicationContext(),"New Location Set",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),"Location Successfully Accessed",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"location null",Toast.LENGTH_SHORT).show();
                        //stopGettingLocation(null);
                    }
                }
            });

        }
    }
}
