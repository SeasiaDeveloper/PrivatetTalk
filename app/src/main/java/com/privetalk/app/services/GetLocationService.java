package com.privetalk.app.services;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.privetalk.app.utilities.LocationUtilities;
import com.privetalk.app.utilities.PriveTalkUtilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by zeniosagapiou on 05/01/16.
 */
public class GetLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Handler mHandler;
    private AsyncTask<Void, Void, Void> getLocationAdditionalInfo;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mGoogleApiClient.isConnected()) {

            mGoogleApiClient.connect();
        } else {
            startLocationUpdates();
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mCurrentLocation != null) {
                LocationUtilities.saveLocation((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude());
                getAdditionalInfo();
            }
        }

        sendLocation();

        return START_STICKY;
    }

    //method to send location sto servpoer every 30 minutes
    private void sendLocation() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PriveTalkUtilities.sendLocationToServer();
                sendLocation();
            }
        }, 1800000); //1800000
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);

        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            LocationUtilities.saveLocation((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude());
            getAdditionalInfo();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            LocationUtilities.saveLocation((float) mCurrentLocation.getLatitude(), (float) mCurrentLocation.getLongitude());
            getAdditionalInfo();
//            try {
//                if (Pushbots.sharedInstance().mContext == null)
//                    Pushbots.sharedInstance().mContext = this.getApplicationContext();
//                Pushbots.sharedInstance().sendLocation(this, location);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
        }
    }

    protected LocationRequest createLocationRequest() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(600000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return mLocationRequest;
    }


    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
    }


    private void getAdditionalInfo() {

        getLocationAdditionalInfo = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                float lat = LocationUtilities.getLatitude();
                float lng = LocationUtilities.getLongitude();

                Geocoder geocoder = new Geocoder(GetLocationService.this, Locale.getDefault());

                // lat,lng, your current location
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        LocationUtilities.setPostalCode(addresses.get(0).getPostalCode());
                        LocationUtilities.setAdminArea(addresses.get(0).getAdminArea() == null ? addresses.get(0).getLocality() : addresses.get(0).getAdminArea());
                        LocationUtilities.setCountryCode(addresses.get(0).getCountryCode());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }


}