package com.driveembetter.proevolutionsoftware.driveembetter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class SaveMe extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private LocationUpdater locationUpdater;
    private LocationManager locationManager;
    double latitude, longitude;
    private TextView locationTxt, rangeText;
    private SeekBar seekBar;
    private int radius;
    private Circle circle;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private LocationListener locationListener;

    private int progressToMeters(int progress) {
        int meters;
        if (progress == 5)
            meters = 200;
        else if (progress == 4)
            meters = 500;
        else if (progress == 3)
            meters = 1000;
        else if (progress == 2)
            meters = 2000;
        else if (progress == 1)
            meters = 5000;
        else
            meters = 10000;
        return meters;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        // For showing a move to my location button
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        View rootView = inflater.inflate(R.layout.fragment_save_me, container, false);

        locationUpdater = ((MainActivity)getActivity()).getLocationUpdater();
        locationManager = locationUpdater.getLocationManager();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        locationTxt = (TextView) rootView.findViewById(R.id.positionTxt);
        rangeText = (TextView) rootView.findViewById(R.id.mapRange);
        seekBar = (SeekBar) rootView.findViewById(R.id.zoomBar);
        radius = progressToMeters(seekBar.getProgress());
        rangeText.setText("Selected Range: " + radius + "m");
        mMapView.onResume(); // needed to get the map to display immediately


        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.e("latitude", "latitude--" + latitude);

                    try {
                        Log.e("latitude", "inside latitude--" + latitude);
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);


                        if (addresses != null && addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0);
                            locationTxt.setText("Your Position:" + " " + address);

                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // Create a LatLng object for the current location
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Show the current location in Google Map
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    circle = googleMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(radius).strokeColor(Color.DKGRAY));
                    circle.setVisible(false);
                    int zoom = getZoomLevel(circle);
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
                    Log.e("animate", "camera animated at: " + String.valueOf(zoom));

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

                /*
                //create circle with a certain radius
                circle = googleMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(radius).strokeColor(Color.DKGRAY));
                circle.setVisible(false);
                int zoom = getZoomLevel(circle);
                // Get latitude of the current location
                latitude = myLocation.getLatitude();

                // Get longitude of the current location
                longitude = myLocation.getLongitude();

                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Show the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));

                */

        //SEEK BAR LISTENER
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                radius = progressToMeters(progress);
                rangeText.setText("Selected Range: " + radius + " m");

                //create circle with a certain radius
                circle = googleMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(radius).strokeColor(Color.DKGRAY));
                circle.setVisible(false);
                int zoom = getZoomLevel(circle);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // For showing a move to my location button
               /* if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }*/
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    Log.e("DB", "PERMISSION GRANTED");
                }
                googleMap.setMyLocationEnabled(true);
            }
        });

        return rootView;
    }

    //convert the circle radius to zoom level
    private int getZoomLevel(Circle circle) {
        int zoomLevel = 0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
