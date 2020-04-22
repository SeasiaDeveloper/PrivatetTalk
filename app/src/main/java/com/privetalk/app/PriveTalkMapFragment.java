package com.privetalk.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.LocationUtilities;

/**
 * Created by zeniosagapiou on 21/03/16.
 */
public class PriveTalkMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    //    private SharedPreferences sharedPreferences;
    private LatLng userCurrentLatLang;
    private Marker marker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userCurrentLatLang = new LatLng(LocationUtilities.getSelectedLatitude(), LocationUtilities.getSelectedLongitude());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        rootView.addView(inflater.inflate(com.privetalk.app.R.layout.map_header, container, false));
        View done;
        rootView.addView(done = inflater.inflate(com.privetalk.app.R.layout.done_buttton, container, false));
        done.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

//        if (userCurrentLatLang.latitude != -1 || userCurrentLatLang.longitude != -1) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.752, 12.657f), 3.5f));
//        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                LocationUtilities.saveSelectedLocation((float) latLng.latitude, (float) latLng.longitude);

                if (marker == null) {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng));
                }
                marker.setPosition(latLng);
            }
        });
    }
}
