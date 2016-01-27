package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;

import at.fhooe.mc.fahrtenbuch.database.MD5;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener, View.OnKeyListener {

    public static final String REGISTER_USERNAME = "register_username";
    public static final String REGISTER_PASSWORD = "register_password";

    public Calendar mBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tv = (TextView) findViewById(R.id.register_birthday);
        tv.setOnClickListener(this);

        EditText et = (EditText) findViewById(R.id.register_last_name);
        et.setOnKeyListener(this);

        Button b = (Button) findViewById(R.id.button_register);
        b.setOnClickListener(this);
    }

    public boolean validate() {
        boolean valid = true;

        EditText username = ((EditText) findViewById(R.id.register_username));
        EditText password1 = ((EditText) findViewById(R.id.register_password1));
        EditText password2 = ((EditText) findViewById(R.id.register_password2));
        EditText firstName = ((EditText) findViewById(R.id.register_first_name));
        EditText lastName = ((EditText) findViewById(R.id.register_last_name));
        TextView birthday = ((TextView) findViewById(R.id.register_birthday));


        if (username.getText().toString().isEmpty() || username.getText().toString().length() < 6) {
            username.setError(getString(R.string.at_least_6_characters));
            valid = false;
        } else if (username.getText().toString().contains(" ")) {
            username.setError(getString(R.string.must_not_contain_spaces));
            valid = false;
        } else {
            username.setError(null);
        }

        if (password1.getText().toString().isEmpty() || password1.getText().toString().length() < 6) {
            password1.setError(getString(R.string.at_least_6_characters));
            valid = false;
        } else {
            password1.setError(null);
        }

        if (!password1.getText().toString().equals(password2.getText().toString())) {
            password2.setError(getString(R.string.password_not_confirmed));
            valid = false;
        } else {
            password2.setError(null);
        }

        if (firstName.getText().toString().isEmpty()) {
            firstName.setError(getString(R.string.please_enter_your_first_name));
            valid = false;
        } else {
            firstName.setError(null);
        }

        if (lastName.getText().toString().isEmpty()) {
            lastName.setError(getString(R.string.please_enter_your_last_name));
            valid = false;
        } else {
            lastName.setError(null);
        }

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Log.e("RegisterUser", year + "/" + month + "/" + day);
        if (mBirthday != null) {
            Log.e("RegisterUser", mBirthday.get(Calendar.YEAR) + "/" + mBirthday.get(Calendar.MONTH) + "/" + mBirthday.get(Calendar.DAY_OF_MONTH));
        }

        if (mBirthday == null || Calendar.getInstance().compareTo(mBirthday) < 0) {
            birthday.setError(getString(R.string.invalid_input));
            valid = false;
        } else {
            birthday.setError(null);
        }
        return valid;
    }

    void selectBirthday() {
        DialogFragment birthdayPicker = new BirthdayPickerDialog();
        birthdayPicker.show(getFragmentManager(), "birthdayPicker");
    }

    @Override
    public void onClick(View _v) {
        if (_v.getId() == R.id.register_birthday) {
            selectBirthday();
        } else if (_v.getId() == R.id.button_register) {
            if (validate()) {
                Log.e("RegisterUser", mBirthday.getTime().toLocaleString());
                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.Base_Theme_AppCompat_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.creating_new_account));
                progressDialog.show();

                final Driver newUser = buildDriverObject();
                App.database.registerUser(newUser, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if (e == null) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(REGISTER_USERNAME, newUser.getUsername());
                            returnIntent.putExtra(REGISTER_PASSWORD, newUser.getPassword());
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                            ((EditText) findViewById(R.id.register_username)).setError(e.getMessage());
                        } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                            Toast.makeText(getBaseContext(), getString(R.string.no_connection_to_the_server), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.error) + " " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    Driver buildDriverObject() {
        String username = ((EditText) findViewById(R.id.register_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.register_password1)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.register_first_name)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.register_last_name)).getText().toString();
        Date birthday = mBirthday.getTime();
        birthday.setHours(12);

        Driver driver = new Driver();
        driver.setUsername(username);
        driver.setPassword(MD5.encrypt(password));
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setBirthday(birthday);

        return driver;
    }

    @Override
    public boolean onKey(View _v, int _keyCode, KeyEvent _event) {
        if (_v.getId() == R.id.register_last_name) {
            if ((_event.getAction() == KeyEvent.ACTION_DOWN) && (_keyCode == KeyEvent.KEYCODE_ENTER)) {
                selectBirthday();
                return true;
            }
        }
        return false;
    }
}
