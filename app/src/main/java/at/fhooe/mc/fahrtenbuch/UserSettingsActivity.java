package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import at.fhooe.mc.fahrtenbuch.database.MD5;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;

public class UserSettingsActivity extends ActionBarActivity implements View.OnClickListener {

    Driver mDriver;

    EditText mUsername;
    EditText mFirstName;
    EditText mLastName;
    TextView mBirthday;
    Calendar mBirthdayCalendar;
    EditText mCurrentPwd;
    EditText mChangedPwd;
    EditText mConfirmedPwd;

    String password;

    ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        TextView tv = (TextView) findViewById(R.id.header_UserSettings_currentUser).findViewById(R.id.layout_header_txt);
        tv.setText(R.string.user_settings_header);
        tv = (TextView) findViewById(R.id.header_UserSettings_password).findViewById(R.id.layout_header_txt);
        tv.setText(R.string.password);

        mUsername = (EditText) findViewById(R.id.usersettings_username);
        mUsername.setText(App.driver.getUsername());
        mFirstName = (EditText) findViewById(R.id.usersettings_first_name);
        mFirstName.setText(App.driver.getFirstName());
        mLastName = (EditText) findViewById(R.id.usersettings_last_name);
        mLastName.setText(App.driver.getLastName());

        mBirthday = (TextView) findViewById(R.id.usersettings_birthday);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        String s = df.format(App.driver.getBirthday());
//        String s = App.driver.getBirthday().getDay() + "." + App.driver.getBirthday().getMonth() + "." + App.driver.getBirthday().getYear();
        mBirthday.setText(s);
        mBirthday.setOnClickListener(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.action_changepwd);
        layout.setOnClickListener(this);

        mDriver = App.driver;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean r = true;
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_done:
                saveAndReturn();
                r = super.onOptionsItemSelected(item);
                break;
        }
        return r;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_changepwd:
                AlertDialog.Builder currentPwd = new AlertDialog.Builder(this);
                currentPwd.setTitle(R.string.change_pwd);
                currentPwd.setMessage(getString(R.string.current_pwd_text));
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_textfield, null);
                currentPwd.setView(dialogView);
                mCurrentPwd = (EditText) dialogView.findViewById(R.id.dialog_textfield);
                mCurrentPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                currentPwd.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        testCurrentPassword();
                    }
                });
                currentPwd.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
                currentPwd.create().show();
                break;
            case R.id.usersettings_birthday:
                changeBirthday();
                break;
        }
    }

    private void testCurrentPassword(){
        mLoadingDialog = new ProgressDialog(UserSettingsActivity.this);
        mLoadingDialog.setIndeterminate(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setMessage(getString(R.string.authenticating));
        mLoadingDialog.show();
        password = MD5.encrypt(mCurrentPwd.getText().toString());

        App.database.loginUser(App.driver.getUsername(), password, new GetCallback<Driver>() {
            @Override
            public void done(Driver driver, ParseException e) {
                if (e == null) {
                    // Login user
                    mLoadingDialog.dismiss();
                    changePassword();
                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    Toast.makeText(getBaseContext(), getString(R.string.wrong_password), Toast.LENGTH_LONG).show();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(getBaseContext(), getString(R.string.no_connection_to_the_server), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.error) + " " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mLoadingDialog.dismiss();
            }
        });
    }

    private void changePassword(){
        final AlertDialog.Builder changePwd = new AlertDialog.Builder(this);
        changePwd.setTitle(R.string.change_pwd);
        changePwd.setMessage(getString(R.string.change_pwd_text));
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        changePwd.setView(dialogView);
        mChangedPwd = (EditText) dialogView.findViewById(R.id.dialog_change_password1);
        mConfirmedPwd = (EditText) dialogView.findViewById(R.id.dialog_change_password2);
        changePwd.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (validatePassword()) {
                    password = MD5.encrypt(mChangedPwd.getText().toString());
                    mDriver.setPassword(password);
                } else {
                    changePassword();
                }
            }
        });
        changePwd.setNegativeButton(R.id.cancel_action, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        changePwd.create().show();
    }

    public boolean validatePassword() {
        boolean valid = true;

        if (mChangedPwd.getText().toString().isEmpty() || mChangedPwd.getText().toString().length() < 6) {
            mChangedPwd.setError(getString(R.string.at_least_6_characters));
            Toast.makeText(getBaseContext(), R.string.at_least_6_characters, Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            mChangedPwd.setError(null);
        }

        if (!mChangedPwd.getText().toString().equals(mConfirmedPwd.getText().toString())) {
            mConfirmedPwd.setError(getString(R.string.password_not_confirmed));
            Toast.makeText(getBaseContext(), R.string.password_not_confirmed, Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            mConfirmedPwd.setError(null);
        }

        return valid;
    }

    private void changeBirthday(){
        DialogFragment birthdayPicker = new BirthdayPickerDialog();
        birthdayPicker.show(getFragmentManager(), "birthdayPicker");
        if(validBirthday()){
            mDriver.setBirthday(mBirthdayCalendar.getTime());
        } else {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
            String s = df.format(App.driver.getBirthday());
            mBirthday.setText(s);
        }

    }

    private boolean validBirthday(){
        boolean valid = true;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (mBirthdayCalendar != null) {
            Log.e("RegisterUser", mBirthdayCalendar.get(Calendar.YEAR) + "/" + mBirthdayCalendar.get(Calendar.MONTH) + "/" + mBirthdayCalendar.get(Calendar.DAY_OF_MONTH));
        }

        if (mBirthdayCalendar == null || Calendar.getInstance().compareTo(mBirthdayCalendar) < 0) {
            mBirthday.setError(getString(R.string.invalid_input));
            valid = false;
        } else {
            mBirthday.setError(null);
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        saveChanges();
    }
    public void saveChanges(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_exitWithoutSaving);
        builder.setMessage(getString(R.string.dialog_text_exitWithoutSaving));
        builder.setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAndReturn();
            }
        });
        builder.setNegativeButton(R.string.dialog_saveMissing_drop, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void saveAndReturn(){
        String username = mUsername.getText().toString();
        String firstname = mFirstName.getText().toString();
        String lastname = mLastName.getText().toString();
        String birthday = mBirthday.getText().toString();

        if(username != null && firstname != null && lastname != null && birthday != null &&
                !username.equals("") && !firstname.equals("") && !lastname.equals("") && !birthday.equals("")){

            mDriver.setUsername(username);
            mDriver.setFirstName(firstname);
            mDriver.setLastName(firstname);


            mLoadingDialog = new ProgressDialog(UserSettingsActivity.this);
            mLoadingDialog.setIndeterminate(true);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setMessage(getString(R.string.saving));
            mLoadingDialog.show();

            mDriver.saveInBackground(new SaveCallback() {
                @Override
                public void done(final ParseException e) {
                    mLoadingDialog.dismiss();
                    if (e == null) {
                        App.driver = mDriver;
                        SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(App.SP_LAST_LOGIN_USERNAME, App.driver.getUsername());
                        editor.putString(App.SP_LAST_LOGIN_PASSWORD, password);
                        editor.apply();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        Toast.makeText(getBaseContext(), getString(R.string.user_change), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), getString(R.string.saving_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title_savaMissing);
            builder.setMessage(getString(R.string.dialog_text_saveMissing));
            builder.setPositiveButton(R.string.dialog_saveMissing_fillout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton(R.string.dialog_saveMissing_drop, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDriver = null;
                    finish();
                }
            });
            builder.show();
        }

    }
}
