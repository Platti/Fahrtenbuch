package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;

import at.fhooe.mc.fahrtenbuch.database.MD5;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;

public class LoginActivity extends Activity implements View.OnClickListener, View.OnKeyListener {

    public static final int REGISTER_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_login);

        Button b = (Button) findViewById(R.id.button_login);
        b.setOnClickListener(this);

        EditText et = (EditText) findViewById(R.id.login_password);
        et.setOnKeyListener(this);

        TextView tv = (TextView) findViewById(R.id.link_register);
        tv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
        String username = sp.getString(App.SP_LAST_LOGIN_USERNAME, null);
        String password = sp.getString(App.SP_LAST_LOGIN_PASSWORD, null);
        if (username != null && password != null) {
            login(username, password);
        }
    }

    public void login(final String _username, final String _password) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.authenticating));
        progressDialog.show();

        App.database.loginUser(_username, _password, new GetCallback<Driver>() {
            @Override
            public void done(Driver driver, ParseException e) {
                if (e == null) {
                    // Login user
                    App.driver = driver;
                    // Store last login in shared preferences
                    SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(App.SP_LAST_LOGIN_USERNAME, _username);
                    editor.putString(App.SP_LAST_LOGIN_PASSWORD, _password);
                    editor.apply();
                    // Open Cars Overview
                    Toast.makeText(getBaseContext(), getString(R.string.hello) + " " + driver.toString() + "!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginActivity.this, CarsOverviewActivity.class);
                    startActivity(i);
                    finish();
                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    Toast.makeText(getBaseContext(), getString(R.string.wrong_username_or_password), Toast.LENGTH_LONG).show();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(getBaseContext(), getString(R.string.no_connection_to_the_server), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.error) + " " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                EditText et = (EditText) findViewById(R.id.login_password);
                et.setText("");
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View _v) {
        if (_v.getId() == R.id.button_login) {
            EditText et = (EditText) findViewById(R.id.login_username);
            String username = et.getText().toString();
            et = (EditText) findViewById(R.id.login_password);
            String password = et.getText().toString();
            login(username, MD5.encrypt(password));
        } else if (_v.getId() == R.id.link_register) {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(i, REGISTER_REQUEST_CODE);
        }
    }

    @Override
    public boolean onKey(View _v, int _keyCode, KeyEvent _event) {
        if (_v.getId() == R.id.login_password) {
            if ((_event.getAction() == KeyEvent.ACTION_DOWN) && (_keyCode == KeyEvent.KEYCODE_ENTER)) {
                EditText et = (EditText) findViewById(R.id.login_username);
                String username = et.getText().toString();
                et = (EditText) findViewById(R.id.login_password);
                String password = et.getText().toString();
                login(username, MD5.encrypt(password));
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        if (_requestCode == REGISTER_REQUEST_CODE) {
            if (_resultCode == RESULT_OK) {
                String username = _data.getStringExtra(RegisterActivity.REGISTER_USERNAME);
                String password = _data.getStringExtra(RegisterActivity.REGISTER_PASSWORD);
                EditText et = (EditText) findViewById(R.id.login_username);
                et.setText(username);
                et = (EditText) findViewById(R.id.login_password);
                et.setText(password);
                login(username, password);
            }
        }
    }
}
