package at.fhooe.mc.fahrtenbuch;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Background service to get location updates also when screen is inactive or app is in background
 */
public class LocationBackgroundService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    /**
     * The main entry point for Google Play services integration.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Data object with the request information
     */
    private LocationRequest mLocationRequest;

    final static String LOCATION = "LOCATION";

    public LocationBackgroundService() {
    }

    /**
     * Initializes the googleApiClient, calls location request method and connects with the google service
     */
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

        createLocationRequest();
        mGoogleApiClient.connect();
    }

    /**
     * stops the location update on destroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fahrtenbuch", "onDestroy service!!");
        stopLocationUpdates();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Fahrtenbuch", "onStartCommand");

        return START_STICKY;
    }

    /**
     * When google service is connected the method starts the location update
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Fahrtenbuch", "onConnected service");

        startLocationUpdates();
    }

    /**
     * Starts the location update
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Creates location request with some parameters (updates only if the distance is higher then 35 meters or every 5 seconds).
     */
    protected void createLocationRequest() {
        Log.d("Fahrtenbuch", "createLocationRequest service");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(35);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Is called when the location has changed.
     * Sends the information about the current location to mapsactivity.
     * @param location the current location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("Fahrtenbuch", "onLocationChanged service!");
        Intent intent = new Intent();
        intent.setAction(LOCATION);

        intent.putExtra("LOCATION", location);

        sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Stops location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

}
