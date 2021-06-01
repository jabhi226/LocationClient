package com.example.mycurrentlocation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    Timer timer;
    Button stopButton,startButton;
    TextView textView,infotext;
    String driver_username;
    Boolean counter = false;
    EditText editText;
    RelativeLayout.LayoutParams layoutParams;
    int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // this will pop-up alert box to allow permission (this will run only at the first launch)
        ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);

        driver_username = getIntent().getExtras().getString("driver_username_key","defaultKey");
        textView.setText("Uesename : "+driver_username);
        infotext.setText("Tap below button to Start");
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);
        textView = (TextView) findViewById(R.id.textView4);
        infotext = (TextView) findViewById(R.id.info_textview);
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setOnTouchListener(this);
        stopButton.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics());
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

        if(v.getId() == startButton.getId()){
            startLocationService(event);
        }
        else if(v.getId() == stopButton.getId()){
            stopLocationService(event);
        }
        return true;
    }

    private void startLocationService(MotionEvent event) {

        if(MotionEvent.ACTION_DOWN == event.getAction())
        {
            counter = true;

            Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator1.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator1.vibrate(30);
            }

            startButton.setTextSize(23);
            layoutParams.width = width + 30;
            layoutParams.height = height + 5;
            startButton.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                final Drawable dr = getResources().getDrawable(R.drawable.login_btn);
                startButton.setBackground(dr);
            }

            infotext.setText("Location is beeing tracked");
        }
        if(MotionEvent.ACTION_UP == event.getAction())
        {
            startButton.setTextSize(20);
            layoutParams.width = width;
            layoutParams.height = height;
            startButton.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                final Drawable dr1 = getResources().getDrawable(R.drawable.login_btn2);
                startButton.setBackground(dr1);
            }

            stopButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);

            timer = new Timer();
            timer.schedule(new RepeaterClass(getApplicationContext()),0,5000);
            //Todo (start service here and in service start timer)
        }
    }

    private void stopLocationService(MotionEvent event) {

        if(MotionEvent.ACTION_DOWN == event.getAction())
        {
            stopButton.setTextSize(23);
            layoutParams.width = width + 30;
            layoutParams.height = height + 5;
            stopButton.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                final Drawable dr = getResources().getDrawable(R.drawable.login_btn);
                stopButton.setBackground(dr);
            }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                final Drawable dr1 = getResources().getDrawable(R.drawable.login_btn2);
                stopButton.setBackground(dr1);
            }

            stopButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);

            infotext.setText("Tap below button to Start");

            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(counter)
            {
                counter = false;
                Toast.makeText(getApplicationContext(),"Location Service Has Stop",Toast.LENGTH_SHORT).show();

                String lat = "0";
                String lon = "0";

                Context context1 = MainActivity.this;
                MyBackgroundTask myBackgroundTask = new MyBackgroundTask(context1);
                myBackgroundTask.execute(lat,lon,driver_username);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
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
