package com.driveembetter.proevolutionsoftware.driveembetter.boundary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.utils.FragmentState;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.NetworkConnectionUtil;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.StringParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SaveMe extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private Context context;
    private PositionManager positionManager;
    private LocationManager locationManager;
    double latitude, longitude;
    private TextView locationTxt, rangeText;
    private SeekBar seekBar;
    private int radius;
    private Circle circle;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private LocationListener locationListener;
    private Map<String, Marker> markerPool;
    private GoogleMap.OnMarkerClickListener onMarkerClickListener;
    private PopupWindow driverInfo;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private TextView driverUsername, driverLocation, driverFeedback;
    private String android_id;
    private String userSelectedLocation, userSelectedFeedback, userSelectedEmail, userSelectedUid, userSelectedToken;

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

        //CHECK INTERNET CONNECTION
        if (!NetworkConnectionUtil.isConnectedToInternet(context))
            Toast.makeText(context, "Please, check you Internet connection!", Toast.LENGTH_SHORT).show();
        final View rootView = inflater.inflate(R.layout.fragment_save_me, container, false);

        //TODO I'll get latitude and longitude from singleton
        positionManager = ((MainFragmentActivity)getActivity()).getPositionManager();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        locationTxt = (TextView) rootView.findViewById(R.id.positionTxt);
        rangeText = (TextView) rootView.findViewById(R.id.mapRange);
        seekBar = (SeekBar) rootView.findViewById(R.id.zoomBar);
        radius = progressToMeters(seekBar.getProgress());
        rangeText.setText(radius + "m");
        mMapView.onResume(); // needed to get the map to display immediately

        database = FirebaseDatabase.getInstance();
        //TODO: TO CHANGE
        myRef = database.getReference("Italy/Lazio");

        android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        markerPool = new HashMap<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                for (String user : data.keySet()) {
                    if (!user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        //Map<String, Object> coordinates = data.get(user);
                        String coordinates = (String) data.get(user).get("currentUserPosition");
                        StringParser stringParser = new StringParser(coordinates);
                        String[] latLon = stringParser.getCoordinates();
                        //LatLng userPos = new LatLng(Double.valueOf(coordinates.get("lat").toString()), Double.valueOf(coordinates.get("lon").toString()));
                        LatLng userPos = new LatLng(Double.valueOf(latLon[0]), Double.valueOf(latLon[1]));
                        if ( markerPool.containsKey(user)) {
                            markerPool.get(user).setPosition(userPos);
                        } else {
                            String markerTitle = "user@drivembetter.com";
                            if (data.get(user).get("email")!=null)
                                markerTitle =(String) data.get(user).get("email");

                            Marker userMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(userPos)
                                    .snippet(user)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                                    .title(markerTitle));
                            markerPool.put(user, userMarker);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        UpdatePosition updatePosition = new UpdatePosition();
        updatePosition.execute();
        /*
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
                            locationTxt.setText(address);

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
            */

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
                rangeText.setText(radius + " m");

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
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

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

                onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        userSelectedLocation = "NA";
                        userSelectedFeedback = "NA";
                        userSelectedEmail = marker.getTitle();
                        userSelectedToken = "";
                        userSelectedUid = marker.getSnippet();



                        List<Address> addresses;
                        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                        LatLng latLng = marker.getPosition();
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addresses != null && addresses.size() > 0) {
                                userSelectedLocation = addresses.get(0).getAddressLine(0);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative);
                        layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.driver_info, null);
                        driverInfo = new PopupWindow(container, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
                        driverInfo.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
                        driverUsername = (TextView) container.findViewById(R.id.driverUsernameContent);
                        driverLocation = (TextView) container.findViewById(R.id.driverPositionContent);
                        driverFeedback = (TextView) container.findViewById(R.id.driverFeedbackContent);
                        driverLocation.setMovementMethod(new ScrollingMovementMethod());
                        driverUsername.setText(marker.getTitle());
                        driverLocation.setText(userSelectedLocation);
                        driverFeedback.setText(userSelectedFeedback);
                        Button message = container.findViewById(R.id.messageBtn);
                        message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(), "How could I inform this user that I'm in trouble?", Toast.LENGTH_SHORT).show();
                                //TODO start chat activity

                                //catch user token
                                DatabaseReference userRef = database.getReference("users/" + userSelectedUid);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        userSelectedToken = (String) dataSnapshot.child("firebaseToken").getValue();
                                        //TODO: TO CHANGE
                                        ChatActivity.startActivity(getActivity(),
                                        userSelectedEmail,
                                        userSelectedUid,
                                        userSelectedToken);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });
                        container.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                driverInfo.dismiss();
                                return true;
                            }
                        });
                        return false;
                    }
                };

                googleMap.setOnMarkerClickListener(onMarkerClickListener);
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
        FragmentState.setSaveMeIsOpen(true);

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

    @Override
    public void onStop() {
        super.onStop();
        FragmentState.setSaveMeIsOpen(false);
    }


    private class UpdatePosition extends AsyncTask<String, String, String> {

        @Override
        protected void onProgressUpdate(String... params) {
            super.onProgressUpdate(params);
            locationTxt.setText(params[0]);
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(Double.parseDouble(params[1]), Double.parseDouble(params[2]));

            // Show the current location in Google Map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            circle = googleMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(radius).strokeColor(Color.DKGRAY));
            circle.setVisible(false);
            int zoom = getZoomLevel(circle);
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            Log.e("animate", "camera animated at: " + String.valueOf(zoom));
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                double latitude, longitude;
                while (true) {
                    latitude = positionManager.getLatitude();
                    longitude = positionManager.getLongitude();
                    if (latitude != 0 && longitude != 0) {
                        try {
                            Log.e("latitude", "inside latitude--" + latitude);
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);


                            if (addresses != null && addresses.size() > 0) {
                                String address = addresses.get(0).getAddressLine(0);
                                publishProgress(address, ""+latitude, ""+longitude);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
