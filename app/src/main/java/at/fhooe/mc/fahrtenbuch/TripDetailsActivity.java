package at.fhooe.mc.fahrtenbuch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.Weather;
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

        fillTextViews();

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

    private void fillTextViews(){
        TextView driverText = (TextView) findViewById(R.id.textView_driver);
        driverText.setText(mTrip.getDriver());
        TextView distanceText = (TextView) findViewById(R.id.textView_distance);
        distanceText.setText(String.valueOf(mTrip.getDistance()) + R.string.kilometer_short);
        TextView descriptionText = (TextView) findViewById(R.id.textView_description);
        descriptionText.setText(mTrip.getDescription());
        TextView weatherText = (TextView) findViewById(R.id.textView_weather);
        weatherText.setText(mTrip.getWeather().getDescription());
        TextView feedbackText = (TextView) findViewById(R.id.textView_feedback);
        feedbackText.setText(String.valueOf(mTrip.getFeedback()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void openEditDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_trip);
        dialog.setTitle(R.string.edit_trip + mTrip.getStartTime().toLocaleString().split(" ")[0]);

        final EditText etDistance = (EditText) dialog.findViewById(R.id.edit_trip_distance);
        etDistance.setText(String.valueOf(mTrip.getDistance()));
        final EditText etWeather = (EditText) dialog.findViewById(R.id.edit_trip_weather);
        etWeather.setText(mTrip.getWeather().getDescription());
        final EditText etDescription = (EditText) dialog.findViewById(R.id.edit_trip_description);
        etDescription.setText(mTrip.getDescription());

        Button b = (Button) dialog.findViewById(R.id.edit_trip_save);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrip.setDistance(Integer.parseInt(etDistance.getText().toString()));
                mTrip.setWeather(etWeather.getText().toString());
                mTrip.setDescription(etDescription.getText().toString());
                mTrip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        fillTextViews();
                        dialog.dismiss();
                    }
                });
            }
        });

        b = (Button) dialog.findViewById(R.id.edit_trip_cancel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;

        LatLng lastPoint = new LatLng(mPointList.get(mPointList.size() - 1).getLatitude(), mPointList.get(mPointList.size() - 1).getLongitude());

        map.addMarker(new MarkerOptions().position(lastPoint));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 10));


        for (int i = 0; i < mPointList.size() - 1; i++) {
            ParseGeoPoint src = mPointList.get(i);
            ParseGeoPoint dest = mPointList.get(i + 1);

            // map is the Map Object
            Polyline line = map.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.getLatitude(), src.getLongitude()),
                            new LatLng(dest.getLatitude(), dest.getLongitude())
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
