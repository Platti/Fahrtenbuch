package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;

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
                alertbox.setTitle(R.string.del_usr);
                alertbox.setMessage(mActivity.getString(R.string.del_usr_sure));
                alertbox.setPositiveButton(R.string.del, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(driver);
                        final ProgressDialog mLoadingDialog = new ProgressDialog(mActivity);
                        mLoadingDialog.setIndeterminate(true);
                        mLoadingDialog.setCanceledOnTouchOutside(false);
                        mLoadingDialog.setMessage(mActivity.getString(R.string.del_dots));
                        mLoadingDialog.show();
                        App.database.deleteMapping(driver.getUsername(), App.car.getLicensePlate(), new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                               mLoadingDialog.dismiss();
                                if(e == null){
                                    Toast.makeText(mActivity, mActivity.getString(R.string.del_usr_success), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(mActivity, mActivity.getString(R.string.error_dots) + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                alertbox.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

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
