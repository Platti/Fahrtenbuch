package at.fhooe.mc.fahrtenbuch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import at.fhooe.mc.fahrtenbuch.database.Weather;
import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

/**
 * This activty shows the detail informations about a certain trip
 */
public class TripDetailsActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * The trip to go in detail
     */
    private Trip mTrip;

    /**
     * The main entry point for Google Play services integration.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Sets the title to start and stop city, initializes the mapFragement
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        mTrip = App.trip;
        setTitle(mTrip.getFirstCity(this) + " - " + mTrip.getLastCity(this));

        fillTextViews();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(this);

//        //create an instance of googleAPIClient
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

    }

    /**
     * Fills the textViews with the detail information about driver, distance, description, weather and feedback.
     */
    private void fillTextViews() {
        TextView driverText = (TextView) findViewById(R.id.textView_driver);
        driverText.setText(mTrip.getDriver());
        TextView distanceText = (TextView) findViewById(R.id.textView_distance);
        distanceText.setText(String.valueOf(mTrip.getDistance()) + getString(R.string.kilometer_short));
        TextView descriptionText = (TextView) findViewById(R.id.textView_description);
        descriptionText.setText(mTrip.getDescription());
        TextView weatherText = (TextView) findViewById(R.id.textView_weather);
        weatherText.setText(mTrip.getWeather().getDescription());
        TextView feedbackText = (TextView) findViewById(R.id.textView_feedback);
        feedbackText.setText(String.valueOf(mTrip.getFeedback()));
    }

    /**
     * creates the defined options menu
     *
     * @param menu menu
     * @return boolean true, if creating was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_details, menu);
        MenuItem mi = (MenuItem) menu.findItem(R.id.action_edit);
        if (!App.driver.getUsername().equals(App.car.getAdmin())) {
            mi.setVisible(false);
            menu.findItem(R.id.action_car_settings).setVisible(false);
        } else {
            mi.setVisible(true);
            menu.findItem(R.id.action_car_settings).setVisible(true);
        }
        return true;
    }

    /**
     * set listener to the option menu
     *
     * @param item choosen item
     * @return boolean true, if action was successful
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i = null;
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_logout:
                App.driver = null;
                // Delete last login in shared preferences
                SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(App.SP_LAST_LOGIN_USERNAME, null);
                editor.putString(App.SP_LAST_LOGIN_PASSWORD, null);
                editor.commit();
                // close activity and show login activity
                i = new Intent(TripDetailsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_user_settings:
                i = new Intent(TripDetailsActivity.this, UserSettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_car_settings:
                i = new Intent(TripDetailsActivity.this, CarAddActivity.class);
                startActivity(i);
                break;
            case R.id.action_edit:
                if (App.car.isAdmin(App.driver)) {
                    openEditDialog();
                } else {
                    Toast.makeText(this, getString(R.string.only_admin), Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * open a dialog to edit the trip
     */
    private void openEditDialog() {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        String s = df.format(mTrip.getStartTime());

        AlertDialog.Builder editBuilder = new AlertDialog.Builder(this);
        editBuilder.setTitle(getString(R.string.edit_trip) + s);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_trip, null);
        editBuilder.setView(dialogView);
        final EditText etDistance = (EditText) dialogView.findViewById(R.id.edit_trip_distance);
        etDistance.setText(String.valueOf(mTrip.getDistance()));
        final EditText etWeather = (EditText) dialogView.findViewById(R.id.edit_trip_weather);
        etWeather.setText(mTrip.getWeather().getDescription());
        final EditText etDescription = (EditText) dialogView.findViewById(R.id.edit_trip_description);
        etDescription.setText(mTrip.getDescription());


        editBuilder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mTrip.setDistance(Integer.parseInt(etDistance.getText().toString()));
                mTrip.setWeather(etWeather.getText().toString());
                mTrip.setDescription(etDescription.getText().toString());
                mTrip.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        fillTextViews();
                    }
                });
            }
        });
        editBuilder.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        editBuilder.create().show();

    }

    /**
     * Is called when the map is ready to show information.
     * Draws the points of the trip as a polyline in the map to show the trip.
     *
     * @param googleMap mapFragment to show the polyline
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;
        List<ParseGeoPoint> pointList;
        pointList = mTrip.getGeoPoints();
        if (pointList.size() != 0) {
            LatLng lastPoint = new LatLng(pointList.get(pointList.size() - 1).getLatitude(), pointList.get(pointList.size() - 1).getLongitude());

            map.addMarker(new MarkerOptions().position(lastPoint));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 10));

            for (int i = 0; i < pointList.size() - 1; i++) {
                ParseGeoPoint src = pointList.get(i);
                ParseGeoPoint dest = pointList.get(i + 1);

                Polyline line = map.addPolyline(
                        new PolylineOptions().add(
                                new LatLng(src.getLatitude(), src.getLongitude()),
                                new LatLng(dest.getLatitude(), dest.getLongitude())
                        )
                );
            }
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
