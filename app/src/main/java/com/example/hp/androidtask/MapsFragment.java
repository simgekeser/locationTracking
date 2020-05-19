package com.example.hp.androidtask;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener mLocationListener;
    private static GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 1001;
    protected Activity mActivity;
    private String profilPicUrl;
    SeekBar seekbar;
    private int customDistance=3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(getActivity());
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        profilPicUrl = mainActivity.getProfilPicUrl();
        seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        seekbar.setProgress(customDistance);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if(mGoogleApiClient==null){
            initGoogleApiClient(getContext());
            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();
        }
        fetchLastLocation();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                customDistance=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;

        //  user.setText(profilPicUrl);
        //  Button logout = (Button) findViewById(R.id.button_logout);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //Toast.makeText(getApplicationContext(),currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_LONG).show();
                    if (!isAdded()) return;
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsFragment.this);
                    // Log.i("initialdistance",currentLocation);
                }
            }
        });
    }

    private void initGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(500);
                setLocationListener();
            }

            @Override
            public void onConnectionSuspended(int i) {
               // Log.i("LOG_TAG", "onConnectionSuspended");
            }
        }).build();
    }

    private void setLocationListener() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location != null && currentLocation != null) {
                    // Log.i("LOG_TAG", "Latitude = " + lat + " Longitude = " + lon);
                    mMap.clear();
                    addMarker(location);
                    if (currentLocation.distanceTo(location) > customDistance) {
                        // Toast.makeText(mActivity,"2'yi geçti",Toast.LENGTH_LONG).show();
                        new AlertDialog.Builder(mActivity)
                                .setTitle("WARNING")
                                .setMessage("Please get back to original position!")
                                .setCancelable(true)
                                .show();

                         //Log.i("distance", Float.toString(currentLocation.distanceTo(location)));
                        // Log.i("current", currentLocation.getLatitude() + "  " + currentLocation.getLongitude());
                        //Log.i("updatedLocation", location.getLatitude() + "  " + location.getLongitude());
                    }
                    Log.i("distanceCustom",customDistance+"");
                    Log.i("distance", Float.toString(location.distanceTo(currentLocation)));
                }
            }

        };
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        } catch (Exception IO) {

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (!MainActivity.thema) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle_night);
            mMap.setMapStyle(style);
            MainActivity.thema = false;
        }
        addMarker(currentLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 16));

    }

    public void addMarker(Location location) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("My position")
                .snippet(location.getLatitude() + " , " + location.getLongitude())
                .icon(BitmapDescriptorFactory.fromBitmap(new CustomMarker(getContext(), profilPicUrl).createUserBitmap()))
                .anchor(0.5f, 1));
        marker.showInfoWindow();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected())
            removeLocationListener();
    }
    private void removeLocationListener() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

}
