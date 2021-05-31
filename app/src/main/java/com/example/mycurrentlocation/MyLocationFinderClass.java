package com.example.mycurrentlocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class MyLocationFinderClass implements LocationListener {

    Context context;

    public MyLocationFinderClass(Context applicationContext) {
        context = applicationContext;
    }

    //(Main hero)with return type Location
    public Location getLocation() {

        boolean isGPSOn;
        boolean isNetworkOn;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context,"permission required",Toast.LENGTH_SHORT).show();
            return null;
        }

        //this will access GPS settings
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //check if GPS is ON or OFF
        isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location gpsLocation = null;
        Location netLocation = null;
        Location passiveLocation = null;

        if(!isGPSOn){
            Toast.makeText(context,"GPS is Disabled",Toast.LENGTH_SHORT).show();
            Location location1 = null;
            return location1;
        }
        if(isGPSOn){
            //request to update the location
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
            if (gpsLocation.getTime() < netLocation.getTime()) {
                    String GPS = String.valueOf(gpsLocation.getTime() / 1000);
                    String NW = String.valueOf(netLocation.getTime() / 1000);
                    Toast.makeText(context, "GPS is being used\ngps : " + GPS + "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
                    return gpsLocation;
            } else {
                    String GPS = String.valueOf(gpsLocation.getTime() / 1000);
                    String NW = String.valueOf(netLocation.getTime() / 1000);
                    Toast.makeText(context, "Network is being used\ngps : " + GPS + "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
                    return netLocation;
            }
        }
        else if(gpsLocation != null && netLocation == null)
        {
            String GPS = String.valueOf(gpsLocation.getTime() / 1000);
            //String NW = String.valueOf(netLocation.getTime() / 1000);
            Toast.makeText(context, "GPS is being used\ngps : " + GPS + "\nnw  : " + "NW", Toast.LENGTH_SHORT).show();
            return gpsLocation;
        }
        else if(gpsLocation == null && netLocation != null)
        {
            //String GPS = String.valueOf(gpsLocation.getTime() / 1000);
            String NW = String.valueOf(netLocation.getTime() / 1000);
            Toast.makeText(context, "Network is being used\ngps : " + "GPS "+ "\nnw  : " + NW, Toast.LENGTH_SHORT).show();
            return netLocation;
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
            passiveLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(passiveLocation != null){
                String passive = String.valueOf(passiveLocation.getTime()/1000);
                Toast.makeText(context,"Passive Location is Used\npassive : "+passive+"\nTry Outside",Toast.LENGTH_SHORT).show();
                return passiveLocation;
            }
            else {
                Toast.makeText(context,"Unable to get Location",Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
