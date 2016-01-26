package at.fhooe.mc.fahrtenbuch;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

public class LocationBackgroundService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    final static String MY_ACTION = "MY_ACTION";

    public LocationBackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Fahrtenbuch", "Oncreate service!!");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        createLocationRequest();
        mGoogleApiClient.connect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Fahrtenbuch", "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d("Fahrtenbuch", "onConnected service");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Log.v("Fahrtenbuch", "Long: " + String.valueOf(mLastLocation.getLongitude()) + " Lat: " + String.valueOf(mLastLocation.getLatitude()));
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest() {
        Log.d("Fahrtenbuch", "createLocationRequest service");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(35);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Fahrtenbuch", "onLocationChanged service!");
        Intent intent = new Intent();
        intent.setAction(MY_ACTION);

        intent.putExtra("LOCATION", location);

        sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
