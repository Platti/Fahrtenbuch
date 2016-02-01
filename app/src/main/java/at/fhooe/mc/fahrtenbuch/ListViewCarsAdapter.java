package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseGeoPoint;

import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;

/**
 * Created by caroline on 20.01.2016.
 */
public class ListViewCarsAdapter extends ArrayAdapter<Car> {
    /**
     * parent activity
     */
    public Activity mActivity;

    /**
     * constructor
     * @param context context
     */
    public ListViewCarsAdapter(Context context) {
        super(context, -1);
    }

    /**
     * create list element
     * @param _pos position
     * @param _view view
     * @param _parent parent
     * @return new view
     */
    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {
        if (_view == null) {
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.list_cars, null);
        }

        final Car car = getItem(_pos);

        TextView tv = (TextView) _view.findViewById(R.id.car_name);
        tv.setText(car.getMake() + " " + car.getModel());

        tv = (TextView) _view.findViewById(R.id.licence_number);
        tv.setText(car.getLicensePlate());

        return _view;
    }

}
