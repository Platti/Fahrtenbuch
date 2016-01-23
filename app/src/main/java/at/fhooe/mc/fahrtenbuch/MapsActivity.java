package at.fhooe.mc.fahrtenbuch;import android.app.AlertDialog;import android.content.DialogInterface;import android.content.Intent;import android.location.Address;import android.location.Geocoder;import android.location.Location;import android.location.LocationManager;import android.support.v4.app.FragmentActivity;import android.os.Bundle;import android.support.v7.app.ActionBarActivity;import android.util.Log;import android.view.View;import android.widget.Button;import android.widget.TextView;import android.widget.Toast;import com.google.android.gms.common.ConnectionResult;import com.google.android.gms.common.api.GoogleApiClient;import com.google.android.gms.location.LocationListener;import com.google.android.gms.location.LocationRequest;import com.google.android.gms.location.LocationServices;import com.google.android.gms.maps.CameraUpdateFactory;import com.google.android.gms.maps.GoogleMap;import com.google.android.gms.maps.OnMapReadyCallback;import com.google.android.gms.maps.SupportMapFragment;import com.google.android.gms.maps.model.LatLng;import com.google.android.gms.maps.model.MarkerOptions;import com.google.android.gms.maps.model.Polyline;import com.google.android.gms.maps.model.PolylineOptions;import com.parse.ParseException;import com.parse.ParseGeoPoint;import com.parse.SaveCallback;import org.json.JSONArray;import java.io.IOException;import java.util.ArrayList;import java.util.Date;import java.util.List;import java.util.Locale;import at.fhooe.mc.fahrtenbuch.database.Weather;import at.fhooe.mc.fahrtenbuch.database.parse.Trip;public class MapsActivity extends ActionBarActivity implements        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {    private static final String TAG = "MyActivity";    private GoogleMap mMap;    private GoogleApiClient mGoogleApiClient;    private Location mLastLocation;    private LocationRequest mLocationRequest;    private Location mCurrentLocation;    private List<LatLng> mPointList = new ArrayList<>();    private boolean mTripStarted = false;    private Trip mTrip = new Trip();    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_maps);        Button b = null;        b = (Button) findViewById(R.id.stop_button);        b.setOnClickListener(this);        b.setText("Start");        // Obtain the SupportMapFragment and get notified when the map is ready to be used.        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()                .findFragmentById(R.id.map);        mapFragment.getMapAsync(this);        //create an instance of googleAPIClient        if (mGoogleApiClient == null) {            mGoogleApiClient = new GoogleApiClient.Builder(this)                    .addConnectionCallbacks(this)                    .addOnConnectionFailedListener(this)                    .addApi(LocationServices.API)                    .build();        }        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();        }else {            showGPSDisabledAlertToUser();        }        createLocationRequest();    }    @Override    public void onBackPressed() {        Button b = (Button)findViewById(R.id.stop_button);        if (b.getText() == "Start") {            super.onBackPressed();        } else {            //disable back button        }    }    @Override    protected void onStart() {        mGoogleApiClient.connect();        super.onStart();    }    @Override    protected void onStop() {        mGoogleApiClient.disconnect();        super.onStop();    }    /**     * Manipulates the map once available.     * This callback is triggered when the map is ready to be used.     * This is where we can add markers or lines, add listeners or move the camera. In this case,     * we just add a marker near Sydney, Australia.     * If Google Play services is not installed on the device, the user will be prompted to install     * it inside the SupportMapFragment. This method will only be triggered once the user has     * installed Google Play services and returned to the app.     */    @Override    public void onMapReady(GoogleMap googleMap) {        mMap = googleMap;        // Add a marker in Innsbruck and move the camera        LatLng innsbruck = new LatLng(47.259659, 11.400375);        mMap.addMarker(new MarkerOptions().position(innsbruck).title("Capital of the alps!"));        mMap.moveCamera(CameraUpdateFactory.newLatLng(innsbruck));        mPointList.add(new LatLng(47.273466, 11.241875));        mPointList.add(new LatLng(47.270464, 11.256268));        mPointList.add(new LatLng(47.265146, 11.274732));        mPointList.add(new LatLng(47.263839, 11.315825));        mPointList.add(new LatLng(47.254158, 11.359128));        mPointList.add(new LatLng(47.256115, 11.381890));        mPointList.add(new LatLng(47.262558, 11.384723));//        mMap.addPolyline(new PolylineOptions().geodesic(true)//                        .add(new LatLng(47.273466, 11.241875))//                        .add(new LatLng(47.270464, 11.256268))//                        .add(new LatLng(47.265146, 11.274732))//                        .add(new LatLng(47.263839, 11.315825))//                        .add(new LatLng(47.254158, 11.359128))//                        .add(new LatLng(47.256115, 11.381890))//                        .add(new LatLng(47.262558, 11.384723))//        );    }    @Override    public void onConnected(Bundle bundle) {        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);        if (mLastLocation != null) {            Log.v(TAG, "Long: " + String.valueOf(mLastLocation.getLongitude()) + " Lat: " + String.valueOf(mLastLocation.getLatitude()));        }//        startLocationUpdates();    }    protected void startLocationUpdates() {        LocationServices.FusedLocationApi.requestLocationUpdates(                mGoogleApiClient, mLocationRequest, this);    }    protected void createLocationRequest() {        mLocationRequest = new LocationRequest();        mLocationRequest.setInterval(1000);        mLocationRequest.setFastestInterval(500);        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);    }    @Override    public void onConnectionSuspended(int i) {    }    @Override    public void onConnectionFailed(ConnectionResult connectionResult) {    }    @Override    public void onLocationChanged(Location location) {        mCurrentLocation = location;        updateUI();    }    private void updateUI() {        Log.v(TAG, "Long: " + String.valueOf(mCurrentLocation.getLongitude()) + " Lat: " + String.valueOf(mCurrentLocation.getLatitude()));        LatLng currentPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint, 10));        mMap.clear();        mMap.addMarker(new MarkerOptions().position(currentPoint));        mPointList.add(currentPoint);        int distance = 0;        Location loc1 = new Location("loc1");        Location loc2 = new Location("loc2");        for (int i = 0; i < mPointList.size() - 1; i++) {            LatLng src = mPointList.get(i);            LatLng dest = mPointList.get(i + 1);            loc1.setLatitude(mPointList.get(i).latitude);            loc1.setLongitude(mPointList.get(i).longitude);            loc2.setLatitude(mPointList.get(i + 1).latitude);            loc2.setLongitude(mPointList.get(i + 1).longitude);            distance += loc1.distanceTo(loc2);            Log.d("Fahrtenbuch", String.valueOf(distance));            // mMap is the Map Object            Polyline line = mMap.addPolyline(                    new PolylineOptions().add(                            new LatLng(src.latitude, src.longitude),                            new LatLng(dest.latitude,dest.longitude)                    )            );        }        distance = distance/1000;        mTrip.setDistance(distance);        TextView dist = (TextView)findViewById(R.id.textView_distance);        dist.setText(String.valueOf(distance) + " km");    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.stop_button: {                if (!mTripStarted) {                    Button b = (Button) findViewById(R.id.stop_button);                    b.setText("Stop");                    mTrip.setStartTime(new Date());                    Log.d("Fahrtenbuch", String.valueOf(mTrip.getStartTime()));                    mTripStarted = true;                    startLocationUpdates();                } else {                    saveTrip();                }                break;            }        }    }    private void saveTrip() {        Log.d("Fahrtenbuch", "SAVE!");        mTrip.setStopTime(new Date());        mTrip.setCar(App.car);        mTrip.setDriver(App.driver);        JSONArray points = new JSONArray();        for (int i = 0; i < mPointList.size(); i++) {            points.put(new ParseGeoPoint(mPointList.get(i).latitude, mPointList.get(i).longitude));        }        mTrip.setGeoPoints(points);        Weather.get(new ParseGeoPoint(mPointList.get(mPointList.size() / 2).latitude, mPointList.get(mPointList.size() / 2).longitude), new Weather.Callback() {            @Override            public void done(Weather weather, Exception e) {                if (e == null) {                    // Use weather here                    mTrip.setWeather(weather);                } else {                    e.printStackTrace();                }            }        });        //feedback        App.trip = mTrip;        Intent i = new Intent(MapsActivity.this, FeedbackActivity.class);        startActivity(i);    }    private void showGPSDisabledAlertToUser(){        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")                .setCancelable(false)                .setPositiveButton("Enable GPS",                        new DialogInterface.OnClickListener(){                            public void onClick(DialogInterface dialog, int id){                                Intent callGPSSettingIntent = new Intent(                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);                                startActivity(callGPSSettingIntent);                            }                        });        alertDialogBuilder.setNegativeButton("Cancel",                new DialogInterface.OnClickListener(){                    public void onClick(DialogInterface dialog, int id){                        dialog.cancel();                    }                });        AlertDialog alert = alertDialogBuilder.create();        alert.show();    }}