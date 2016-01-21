package at.fhooe.mc.fahrtenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
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

        mLoadingStatus = 0;

        mLoadingDialog = new ProgressDialog(TripsOverviewActivity.this, R.style.Base_Theme_AppCompat_Dialog);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setMessage("Loading...");
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
                if (e == null) {
                    for (Trip trip : trips) {
                        adapter.add(trip);
                    }
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
    public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
        if (_parent.getId() == R.id.list_view_trips) {
            ListView listView = (ListView) _parent;
            ListViewTripsAdapter adapter = (ListViewTripsAdapter) listView.getAdapter();
            App.trip = adapter.getItem(_position);

            Intent i = new Intent(TripsOverviewActivity.this, TripDetailsActivity.class);
            startActivity(i);
        }
    }
}
