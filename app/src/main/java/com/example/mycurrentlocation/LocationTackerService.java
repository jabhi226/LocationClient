package com.example.mycurrentlocation;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class LocationTackerService extends Service implements LocationListener {

    Location gpsLocation = null;
    Location netLocation = null;
    Location passiveLocation = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "this is service", Toast.LENGTH_SHORT).show();

        boolean isGPSOn;
        boolean isNetworkOn;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"permission required",Toast.LENGTH_SHORT).show();
//            return null;
        }

        //this will access GPS settings
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //check if GPS is ON or OFF
        isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSOn) {
            Toast.makeText(this, "GPS is Disabled", Toast.LENGTH_SHORT).show();
            Location location1 = null;
//            return location1;
        }
        if (isGPSOn) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            //will get last known location and return in type of Location
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(isNetworkOn){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        //return location
        if (gpsLocation != null && netLocation != null) {
            String GPS = String.valueOf(gpsLocation.getTime() / 1000);
            String NW = String.valueOf(netLocation.getTime() / 1000);
            if (gpsLocation.getTime() < netLocation.getTime()) {
                Toast.makeText(this, "GPS is being used\ngps : " + GPS + "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
//                return gpsLocation;
            } else {
                Toast.makeText(this, "Network is being used\ngps : " + GPS + "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
//                return netLocation;
            }
        }
        else if(gpsLocation != null && netLocation == null) {
            String GPS = String.valueOf(gpsLocation.getTime() / 1000);
            Toast.makeText(this, "GPS is being used\ngps : " + GPS + "\nnw  : " + "NW", Toast.LENGTH_SHORT).show();
//            return gpsLocation;
        }
        else if(gpsLocation == null && netLocation != null) {
            String NW = String.valueOf(netLocation.getTime() / 1000);
            Toast.makeText(this, "Network is being used\ngps : " + "GPS "+ "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
//            return netLocation;
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
            passiveLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(passiveLocation != null){
                String passive = String.valueOf(passiveLocation.getTime()/1000);
                Toast.makeText(this,"Passive Location is Used\npassive : "+passive+"\nTry Outside",Toast.LENGTH_SHORT).show();
//                return passiveLocation;
            }
            else {
                Toast.makeText(this,"Unable to get Location",Toast.LENGTH_SHORT).show();
//                return null;
            }
        }
        return START_STICKY;
    }

    public static final String TAG = "Service1";

    @Override
    public void onLocationChanged(Location location) {
        if(gpsLocation != null)
        {
            Toast.makeText(this,"onLocationChanged" + gpsLocation.getLatitude() + ", " + gpsLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "onLocationChanged: ");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this,"onStatusChanged",Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this,"onProviderEnabled",Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,"onProviderDisabled",Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onProviderDisabled: ");
    }
}
