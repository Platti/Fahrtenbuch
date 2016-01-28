package at.fhooe.mc.fahrtenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Trip;


public class TripsOverviewActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public static int mLoadingStatus;
    public static ListView mListView;
    //    public static ProgressBar mLoadingSpinner;
    public static ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_overview);

        setTitle(App.car.getMake() + " " + App.car.getModel());

        mLoadingStatus = 0;

        mLoadingDialog = new ProgressDialog(TripsOverviewActivity.this, R.style.Base_Theme_AppCompat_Dialog);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setMessage(getString(R.string.loading));
        mLoadingDialog.show();

//        mLoadingSpinner = (ProgressBar) findViewById(R.id.progressBar_loading_trips);
//        mLoadingSpinner.setVisibility(View.VISIBLE);

        final ListView listView = (ListView) findViewById(R.id.list_view_trips);
        mListView = listView;
        final ListViewTripsAdapter adapter = new ListViewTripsAdapter(getBaseContext());
        adapter.mActivity = this;
        App.database.getTrips(App.car, new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> trips, ParseException e) {
                if (e == null && trips.size() > 0) {
                    for (Trip trip : trips) {
                        adapter.add(trip);
                    }
                } else {
                    mLoadingDialog.dismiss();
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trips_overview, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i = null;
        switch (id){
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
                i = new Intent(TripsOverviewActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_user_settings:
                i = new Intent(TripsOverviewActivity.this, UserSettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_car_settings:
                i = new Intent(TripsOverviewActivity.this, CarAddActivity.class);
                startActivity(i);
                break;
            case R.id.action_export:

                mLoadingDialog = new ProgressDialog(TripsOverviewActivity.this, R.style.Base_Theme_AppCompat_Dialog);
                mLoadingDialog.setIndeterminate(true);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setMessage(getString(R.string.exporting));
                mLoadingDialog.show();

                exportTripsToCSV(new Callback() {
                    @Override
                    public void done(Exception e) {
                        mLoadingDialog.dismiss();
                        if (e == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.exporting_successful), Toast.LENGTH_SHORT).show();
                                }
                            });



                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.exporting_failed), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
        if (_parent.getId() == R.id.list_view_trips) {
            ListView listView = (ListView) _parent;
            ListViewTripsAdapter adapter = (ListViewTripsAdapter) listView.getAdapter();
            App.trip = adapter.getItem(_position);

            Intent i = new Intent(TripsOverviewActivity.this, TripDetailsActivity.class);
            startActivity(i);
        }
    }

    private void exportTripsToCSV(final Callback callback) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    File folder = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fahrtenbuch");

                    if (!folder.exists()) {
                        folder.mkdir();
                        Log.e("ExportCSV", "Folder created!");
                    } else {
                        Log.e("ExportCSV", "Folder exists!");
                    }

                    String filename = folder.toString() + "/" + App.car.getLicensePlate() + " - " + new Date().toLocaleString() + ".csv";
                    CSVWriter writer = new CSVWriter(new FileWriter(filename));

                    Log.e("ExportCSV", "Start to write csv");

                    List<String[]> data = new ArrayList<String[]>();
                    data.add(new String[]{"Date", "Time of departure", "Place of departure", "Time of arrival", "Place of Arrival", "Distance", "Driver", "Description"});

                    ListViewTripsAdapter adapter = (ListViewTripsAdapter) mListView.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        String[] strArray = new String[8];
                        strArray[0] = adapter.getItem(i).getStartTime().toLocaleString().split(" ")[0];
                        strArray[1] = adapter.getItem(i).getStartTime().toLocaleString().split(" ")[1];
                        strArray[2] = adapter.getItem(i).getFirstCity(getBaseContext());
                        strArray[3] = adapter.getItem(i).getStopTime().toLocaleString().split(" ")[1];
                        strArray[4] = adapter.getItem(i).getLastCity(getBaseContext());
                        strArray[5] = adapter.getItem(i).getDistance() + " km";
                        strArray[6] = adapter.getItem(i).getDriver();
                        strArray[7] = adapter.getItem(i).getDescription();

                        Log.e("ExportCSV", "Add trip number " + i);
                        data.add(strArray);
                    }

                    writer.writeAll(data);

                    writer.close();

                    Log.e("ExportCSV", "Finished export successfully");

                    callback.done(null);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fahrtenbuch_export) + App.car.getLicensePlate());
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.an_overview_of_the_trips_of_the_car) + " " + App.car.getLicensePlate() + " " + getString(R.string.is_attached_to_this_mail));
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
                    Intent mailer = Intent.createChooser(intent, null);
                    startActivity(mailer);

                } catch (IOException e) {
                    callback.done(e);
                }
                return null;
            }
        };
        task.execute();
    }


    public interface Callback {
        public void done(Exception e);
    }
}
