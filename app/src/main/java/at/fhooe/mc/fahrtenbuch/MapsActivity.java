package at.fhooe.mc.fahrtenbuch;import android.app.AlertDialog;import android.content.BroadcastReceiver;import android.content.DialogInterface;import android.content.Intent;import android.content.IntentFilter;import android.location.Location;import android.location.LocationManager;import android.os.Bundle;import android.support.v7.app.ActionBarActivity;import android.util.Log;import android.view.View;import android.widget.Button;import android.widget.TextView;import android.widget.Toast;import com.google.android.gms.common.ConnectionResult;import com.google.android.gms.common.api.GoogleApiClient;import com.google.android.gms.location.LocationListener;import com.google.android.gms.location.LocationRequest;import com.google.android.gms.location.LocationServices;import com.google.android.gms.maps.CameraUpdateFactory;import com.google.android.gms.maps.GoogleMap;import com.google.android.gms.maps.OnMapReadyCallback;import com.google.android.gms.maps.SupportMapFragment;import com.google.android.gms.maps.model.LatLng;import com.google.android.gms.maps.model.MarkerOptions;import com.google.android.gms.maps.model.Polyline;import com.google.android.gms.maps.model.PolylineOptions;import com.parse.ParseGeoPoint;import android.content.Context;import org.json.JSONArray;import java.util.ArrayList;import java.util.Date;import java.util.List;import at.fhooe.mc.fahrtenbuch.database.Weather;import at.fhooe.mc.fahrtenbuch.database.parse.Trip;public class MapsActivity extends ActionBarActivity implements        OnMapReadyCallback, View.OnClickListener {    private static final String TAG = "MyActivity";    private GoogleMap mMap;    private Location mCurrentLocation;    private List<LatLng> mPointList = new ArrayList<>();    private boolean mTripStarted = false;    private Trip mTrip = new Trip();    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_maps);        Button b = null;        b = (Button) findViewById(R.id.stop_button);        b.setOnClickListener(this);        b.setText("Start");        // Obtain the SupportMapFragment and get notified when the map is ready to be used.        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()                .findFragmentById(R.id.map);        mapFragment.getMapAsync(this);        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();        } else {            showGPSDisabledAlertToUser();        }    }    @Override    public void onMapReady(GoogleMap googleMap) {        mMap = googleMap;//        // Add a marker in Innsbruck and move the camera//        LatLng innsbruck = new LatLng(47.259659, 11.400375);//        mMap.addMarker(new MarkerOptions().position(innsbruck).title("Capital of the alps!"));//        mMap.moveCamera(CameraUpdateFactory.newLatLng(innsbruck));//        mPointList.add(new LatLng(47.273466, 11.241875));//        mPointList.add(new LatLng(47.270464, 11.256268));//        mPointList.add(new LatLng(47.265146, 11.274732));//        mPointList.add(new LatLng(47.263839, 11.315825));//        mPointList.add(new LatLng(47.254158, 11.359128));//        mPointList.add(new LatLng(47.256115, 11.381890));//        mPointList.add(new LatLng(47.262558, 11.384723));    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.stop_button: {                if (!mTripStarted) {                    Button b = (Button) findViewById(R.id.stop_button);                    b.setText("Stop");                    mTrip.setStartTime(new Date());                    Log.d("Fahrtenbuch", String.valueOf(mTrip.getStartTime()));                    mTripStarted = true;                    startService(this);                    //startLocationUpdates();                } else {                    saveTrip();                }                break;            }        }    }    @Override    public void onBackPressed() {        Button b = (Button)findViewById(R.id.stop_button);        if (b.getText() == "Start") {            super.onBackPressed();        } else {            //disable back button        }    }    // Method to start the service    public void startService(MapsActivity view) {        //Register BroadcastReceiver        //to receive event from our service        Receiver rec = new Receiver();        IntentFilter intentFilter = new IntentFilter();        intentFilter.addAction(LocationBackgroundService.MY_ACTION);        registerReceiver(rec, intentFilter);        Intent i= new Intent(getBaseContext(), LocationBackgroundService.class);        startService(i);    }    // Method to stop the service    public void stopService(MapsActivity view) {        stopService(new Intent(getBaseContext(), LocationBackgroundService.class));    }    /**     * Manipulates the map once available.     * This callback is triggered when the map is ready to be used.     * This is where we can add markers or lines, add listeners or move the camera. In this case,     * we just add a marker near Sydney, Australia.     * If Google Play services is not installed on the device, the user will be prompted to install     * it inside the SupportMapFragment. This method will only be triggered once the user has     * installed Google Play services and returned to the app.     */    //update the map with new location in polyline etc.    public void updateUI() {        Log.v(TAG, "Long: " + String.valueOf(mCurrentLocation.getLongitude()) + " Lat: " + String.valueOf(mCurrentLocation.getLatitude()));        LatLng currentPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPoint, 10));        mMap.clear();        mMap.addMarker(new MarkerOptions().position(currentPoint));        mPointList.add(currentPoint);        int distance = 0;        Location loc1 = new Location("loc1");        Location loc2 = new Location("loc2");        for (int i = 0; i < mPointList.size() - 1; i++) {            LatLng src = mPointList.get(i);            LatLng dest = mPointList.get(i + 1);            loc1.setLatitude(mPointList.get(i).latitude);            loc1.setLongitude(mPointList.get(i).longitude);            loc2.setLatitude(mPointList.get(i + 1).latitude);            loc2.setLongitude(mPointList.get(i + 1).longitude);            distance += loc1.distanceTo(loc2);            Log.d("Fahrtenbuch", String.valueOf(distance));            // mMap is the Map Object            Polyline line = mMap.addPolyline(                    new PolylineOptions().add(                            new LatLng(src.latitude, src.longitude),                            new LatLng(dest.latitude,dest.longitude)                    )            );        }        distance = distance/1000;        mTrip.setDistance(distance);        TextView dist = (TextView)findViewById(R.id.textView_distance);        dist.setText(String.valueOf(distance) + " km");    }    private void saveTrip() {        Log.d("Fahrtenbuch", "SAVE!");        mTrip.setStopTime(new Date());        mTrip.setCar(App.car);        mTrip.setDriver(App.driver);        JSONArray points = new JSONArray();        for (int i = 0; i < mPointList.size(); i++) {            points.put(new ParseGeoPoint(mPointList.get(i).latitude, mPointList.get(i).longitude));        }        mTrip.setGeoPoints(points);        if (mPointList.size() != 0) {            Weather.get(new ParseGeoPoint(mPointList.get(mPointList.size() / 2).latitude, mPointList.get(mPointList.size() / 2).longitude), new Weather.Callback() {                @Override                public void done(Weather weather, Exception e) {                    if (e == null) {                        // Use weather here                        mTrip.setWeather(weather);                    } else {                        e.printStackTrace();                    }                }            });        }        stopService(this);        App.trip = mTrip;        Intent i = new Intent(MapsActivity.this, FeedbackActivity.class);        startActivity(i);        finish();    }    private void showGPSDisabledAlertToUser(){        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")                .setCancelable(false)                .setPositiveButton("Enable GPS",                        new DialogInterface.OnClickListener(){                            public void onClick(DialogInterface dialog, int id){                                Intent callGPSSettingIntent = new Intent(                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);                                startActivity(callGPSSettingIntent);                            }                        });                alertDialogBuilder.setNegativeButton("Cancel",                new DialogInterface.OnClickListener(){                    public void onClick(DialogInterface dialog, int id){                        dialog.cancel();                    }                });        AlertDialog alert = alertDialogBuilder.create();        alert.show();    }    //Braofcast receiver to receive location    private class Receiver extends BroadcastReceiver {        @Override        public void onReceive(Context arg0, Intent arg1) {            // TODO Auto-generated method stub            Bundle bundle = arg1.getExtras();            Location loc = (Location)bundle.get("LOCATION");            mCurrentLocation = loc;            updateUI();        }    }}