package at.fhooe.mc.fahrtenbuch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BirthdayPickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle _savedInstanceState) {
        DatePickerDialog dialog;
        Calendar birthday = ((RegisterActivity) getActivity()).mBirthday;
        if (birthday == null) {
            dialog = new DatePickerDialog(getActivity(), this, 1990, 0, 1);
        } else {
            Log.e("BirthdayPicker-create", birthday.toString());
            Log.e("BirthdayPicker-create", birthday.get(Calendar.YEAR) + "/" + birthday.get(Calendar.MONTH) + "/" + birthday.get(Calendar.DAY_OF_MONTH));
            dialog = new DatePickerDialog(getActivity(), this, birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH));
        }
        dialog.setTitle("Birthday");
        return dialog;
    }

    public void onDateSet(DatePicker _view, int _year, int _month, int _day) {
        TextView tv = (TextView) getActivity().findViewById(R.id.register_birthday);
        Log.e("BirthdayPicker-dateset1", _year + "/" + _month + "/" + _day);
        Calendar date = new GregorianCalendar(_year, _month, _day);
        Log.e("BirthdayPicker-dateset2", date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH));
        ((RegisterActivity) getActivity()).mBirthday = date;
        String str = String.format("%02d.%02d.%04d", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1, date.get(Calendar.YEAR));
        tv.setText(str);
        tv.setTextColor(Color.BLACK);
    }
}