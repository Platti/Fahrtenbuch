package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import at.fhooe.mc.fahrtenbuch.database.Weather;
import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

/**
 * array adapter for trips
 */
public class ListViewTripsAdapter extends ArrayAdapter<Trip> {
    /**
     * parent activity
     */
    public Activity mActivity;

    /**
     * constructor
     *
     * @param context context
     */
    public ListViewTripsAdapter(Context context) {
        super(context, -1);
    }

    /**
     * create list element
     *
     * @param _pos    position
     * @param _view   view
     * @param _parent parent
     * @return new view
     */
    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {
        if (_view == null) {
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.list_trips, null);
        }

        final Trip trip = getItem(_pos);

        TextView tv = (TextView) _view.findViewById(R.id.trip_date);
        tv.setText(trip.getCreatedAt().toLocaleString().split(" ")[0]); // TODO: change to startTime date

        tv = (TextView) _view.findViewById(R.id.trip_driver);
        tv.setText(trip.getDriver());

        tv = (TextView) _view.findViewById(R.id.trip_distance);
        tv.setText(trip.getDistance() + R.string.kilometer_short);


        ImageView iv = (ImageView) _view.findViewById(R.id.trip_weather);
        if (trip.getWeather().getCode() != null) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageDrawable(_view.getResources().getDrawable(trip.getWeather().getIconID()));
        } else {
            iv.setVisibility(View.GONE);
        }

        final View view = _view;
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                final String firstCity = trip.getFirstCity(getContext());
                final String lastCity = trip.getLastCity(getContext());

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) view.findViewById(R.id.trip_start);
                        tv.setText(firstCity);
                        tv = (TextView) view.findViewById(R.id.trip_stop);
                        tv.setText(lastCity);
//                        TripsOverviewActivity.mLoadingStatus.remove(trip);
//                        Log.e("LoadingStatus", "LoadingStatus: " + TripsOverviewActivity.mLoadingStatus.remove(trip) + " " + TripsOverviewActivity.mLoadingStatus.size());
                        ListView listView = TripsOverviewActivity.mListView;
                        int visible = listView.getLastVisiblePosition() - listView.getFirstVisiblePosition() + 1;
                        if (TripsOverviewActivity.mLoadingStatus < visible) {
                            TripsOverviewActivity.mLoadingStatus++;
                            Log.e("LoadingStatus", "LoadingStatus: " + TripsOverviewActivity.mLoadingStatus + " of " + visible);

                            if (TripsOverviewActivity.mLoadingStatus == visible) {
//                            TripsOverviewActivity.mLoadingSpinner.setVisibility(View.GONE);
                                TripsOverviewActivity.mLoadingDialog.dismiss();
                            }
                        }
                    }
                });
                return null;
            }
        };
        task.execute();
        return _view;
    }

}
