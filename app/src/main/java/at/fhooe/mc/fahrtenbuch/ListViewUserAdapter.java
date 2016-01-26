package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;

/**
 * Created by caroline on 26.01.2016.
 */
public class ListViewUserAdapter extends ArrayAdapter<Driver> {
    public Activity mActivity;

    public ListViewUserAdapter(Context context) {
        super(context, -1);
    }

    @Override
    public View getView(int _pos, View _view, ViewGroup _parent) {
        if (_view == null) {
            Context c = getContext();
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _view = inflater.inflate(R.layout.list_user, null);
        }

        final Driver driver = getItem(_pos);

        TextView tv = (TextView) _view.findViewById(R.id.user_name);
        tv.setText(driver.getFirstName() + " " + driver.getLastName());

        ImageView iv = (ImageView) _view.findViewById(R.id.delet_user);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(mActivity);
                alertbox.setTitle("Delete user");
                alertbox.setMessage("Do you really want to delete the user?");
                alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(driver);
                    }
                });
                alertbox.setNegativeButton("Cance", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertbox.show();
            }
        });

        return _view;
    }
}
