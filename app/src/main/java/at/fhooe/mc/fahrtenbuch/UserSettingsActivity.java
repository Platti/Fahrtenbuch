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

    /**
     * Driver Object to store the current user locally
     */
    Driver mDriver;

    /**
     * textfield for the username - disabled
     */
    EditText mUsername;
    /**
     * editable textfield for the first name
     */
    EditText mFirstName;
    /**
     * editable textfield for the last name
     */
    EditText mLastName;
    /**
     * editabel textfield for the birthday
     */
    TextView mBirthday;
    /**
     * Calender to choose the birthday
     */
    Calendar mBirthdayCalendar;
    /**
     * editable textfields for the current password
     */
    EditText mCurrentPwd;
    /**
     * editable textfield to change the password
     */
    EditText mChangedPwd;
    /**
     * editable textfield to confirm the changed password
     */
    EditText mConfirmedPwd;

    /**
     * string to strore password locally
     */
    String password;

    /**
     * progress dialog to show the user, when something is loading
     */
    ProgressDialog mLoadingDialog;

    /**
     * onCreate()
     * fill editable textfields with the current data of the user
     * set click listener to change password
     * @param savedInstanceState
     */
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
        mUsername.setEnabled(false);
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

        password = mDriver.getPassword();
    }

    /**
     * creates the defined options menu
     * @param menu menu
     * @return boolean true, if creating was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
        return true;
    }

    /**
     * set listener to the option menu
     * @param item choosen item
     * @return boolean true, if action was successful
     */
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

    /**
     * onclick listener
     * 1. to change password
     *      dialog to enter current password
     *      if ok, dialog to enter new password and confirm it
     * 2. to change birthday
     * @param view choosen view, with on click listener
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_changepwd:
                AlertDialog.Builder currentPwd = new AlertDialog.Builder(this);
                currentPwd.setTitle(R.string.change_pwd);
                currentPwd.setMessage(getString(R.string.current_pwd_text));
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_password_textfield, null);
                currentPwd.setView(dialogView);
                mCurrentPwd = (EditText) dialogView.findViewById(R.id.dialog_pwd_textfield);
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

    /**
     * checks if user entered the correct password
     */
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

    /**
     * method to change the current password
     */
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
        changePwd.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        changePwd.create().show();
    }

    /**
     * methode to check if the new password is valid
     * @return boolean true, if the password and confirm password are the same, and if it's like specified conditions
     *              false otherwise
     */
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

    /**
     * method to change birthday
     */
    private void changeBirthday(){
        DialogFragment birthdayPicker = new BirthdayPickerDialog();
        birthdayPicker.show(getFragmentManager(), "birthdayPicker");
        if(!birthdayPicker.isVisible()) {
            if (validBirthday()) {
                mDriver.setBirthday(mBirthdayCalendar.getTime());
            } else {
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                String s = df.format(App.driver.getBirthday());
                mBirthday.setText(s);
            }
        }

    }

    /**
     * checks if entered birthday is valid
     * @return boolean true, if it is valid (between 01.01.1900 - present-date)
     *                  false otherwise
     */
    private boolean validBirthday(){
        boolean valid = true;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Log.e("UserSettings", year + "/" + month + "/" + day);
        if (mBirthdayCalendar != null) {
            Log.e("UserSettings", mBirthdayCalendar.get(Calendar.YEAR) + "/" + mBirthdayCalendar.get(Calendar.MONTH) + "/" + mBirthdayCalendar.get(Calendar.DAY_OF_MONTH));
        }



        if (mBirthdayCalendar == null || Calendar.getInstance().compareTo(mBirthdayCalendar) < 0) {
//            Toast.makeText(getBaseContext(), R.string.impossible_birthday, Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            mBirthday.setError(null);
        }
        return valid;
    }

    /**
     * onBackPressed()
     * has to save changes before return
     */
    @Override
    public void onBackPressed() {
        saveChanges();
    }

    /**
     * method to save changes
     */
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

    /**
     * stores the changes in database and returns to the parent-activity
     */
    private void saveAndReturn(){
        String username = mUsername.getText().toString();
        String firstname = mFirstName.getText().toString();
        String lastname = mLastName.getText().toString();
        String birthday = mBirthday.getText().toString();

        if(username != null && firstname != null && lastname != null && birthday != null &&
                !username.equals("") && !firstname.equals("") && !lastname.equals("") && !birthday.equals("")){

            mDriver.setUsername(username);
            mDriver.setFirstName(firstname);
            mDriver.setLastName(lastname);


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
