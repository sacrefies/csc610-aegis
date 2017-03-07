package net.teamc.aegis.mapservice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CurrentLocation implements LocationListener {
    private GoogleMap mMap;
    private LatLng location;
    private LocationManager locationManager;
    private Context context;
    private Activity activity;
    private double latitude;
    private double longitude;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CurrentLocation(GoogleMap googleMap, LocationManager locationManager, Context context, Activity activity) {
        this.mMap = googleMap;
        this.locationManager = locationManager;
        this.context = context;
        this.activity = activity;
    }

    public LatLng getLocation() {

        //location permission check
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //enable myLocation layer of google map
        mMap.setMyLocationEnabled(true);


        //create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        //get name of best provider
        String provider = locationManager.getBestProvider(criteria, true);

        //get location from emulator
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        //get current location
        Location myLocation = locationManager.getLastKnownLocation(provider);
        if (myLocation != null) {
            //get last location
            location = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        } else {
            //get location from gps
            location = new LatLng(latitude, longitude);
        }
        return location;
    }

    public void updatesLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 18);
                mMap.animateCamera(cameraUpdate);
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
        });
    }
    @Override
    public void onLocationChanged(Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
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
