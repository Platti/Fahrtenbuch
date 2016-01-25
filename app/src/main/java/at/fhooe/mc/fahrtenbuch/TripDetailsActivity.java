package at.fhooe.mc.fahrtenbuch;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Trip;


public class TripDetailsActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Trip mTrip;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private List<ParseGeoPoint> mPointList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        mTrip = App.trip;
        setTitle(mTrip.getFirstCity(this) + " - " + mTrip.getLastCity(this));

        TextView driverText = (TextView)findViewById(R.id.textView_driver);
        driverText.setText(mTrip.getDriver());
        TextView distanceText = (TextView)findViewById(R.id.textView_distance);
        distanceText.setText(String.valueOf(mTrip.getDistance()) + " km");
        TextView descriptionText = (TextView)findViewById(R.id.textView_description);
        descriptionText.setText(mTrip.getDescription());
        TextView weatherText = (TextView)findViewById(R.id.textView_weather);
        weatherText.setText(mTrip.getWeather().getDescription());
        TextView feedbackText = (TextView)findViewById(R.id.textView_feedback);
        feedbackText.setText(String.valueOf(mTrip.getFeedback()));

        mPointList = mTrip.getGeoPoints();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);

        //create an instance of googleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng lastPoint = new LatLng(mPointList.get(mPointList.size() - 1).getLatitude(), mPointList.get(mPointList.size() - 1).getLongitude());

        mMap.addMarker(new MarkerOptions().position(lastPoint));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 10));


        for (int i = 0; i < mPointList.size() - 1; i++) {
            ParseGeoPoint src = mPointList.get(i);
            ParseGeoPoint dest = mPointList.get(i + 1);

            // mMap is the Map Object
            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.getLatitude(), src.getLongitude()),
                            new LatLng(dest.getLatitude(),dest.getLongitude())
                    )
            );
        }


    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
