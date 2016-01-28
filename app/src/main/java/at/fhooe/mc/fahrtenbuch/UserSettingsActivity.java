package at.fhooe.mc.fahrtenbuch;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserSettingsActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        TextView tv = (TextView) findViewById(R.id.header_UserSettingsA_currentUser).findViewById(R.id.layout_header_txt);
        tv.setText(R.string.user_settings_header);
        tv = (TextView) findViewById(R.id.header_UserSettingsA_password).findViewById(R.id.layout_header_txt);
        tv.setText(R.string.password);
        EditText et = (EditText) findViewById(R.id.usersettings_username);
        et.setText(App.driver.getUsername());
        et = (EditText) findViewById(R.id.usersettings_first_name);
        et.setText(App.driver.getFirstName());
        et = (EditText) findViewById(R.id.usersettings_last_name);
        et.setText(App.driver.getLastName());
        tv = (TextView) findViewById(R.id.usersettings_birthday);
        SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yyyy", Locale.GERMAN);
        String s = df.format(App.driver.getBirthday());
//        String s = App.driver.getBirthday().getDay() + "." + App.driver.getBirthday().getMonth() + "." + App.driver.getBirthday().getYear();
        tv.setText(s);
        tv.setOnClickListener(this);
        LinearLayout layout = (LinearLayout) findViewById(R.id.action_changepwd);
        layout.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_changepwd:

                break;
            case R.id.usersettings_birthday:
//                DialogFragment birthdayPicker = new BirthdayPickerDialog();
//                birthdayPicker.show(getFragmentManager(), "birthdayPicker");
                break;
        }
    }
}
