package at.fhooe.mc.fahrtenbuch;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import at.fhooe.mc.fahrtenbuch.R;

public class CarActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        setTitle(App.car.getMake() + " " + App.car.getModel());

        TextView tv = (TextView) findViewById(R.id.menu_start_ride).findViewById(R.id.textfield_menu_item);
        tv.setText(R.string.button_start_ride);
        tv = (TextView) findViewById(R.id.menu_show_rides).findViewById(R.id.textfield_menu_item);
        tv.setText(R.string.button_show_rides);
        tv = (TextView) findViewById(R.id.menu_car_settings).findViewById(R.id.textfield_menu_item);
        tv.setText(R.string.action_settings);

        ImageView iv = (ImageView) findViewById(R.id.menu_start_ride).findViewById(R.id.image_menu_item);
        iv.setImageResource(R.drawable.ic_action_action_room);
        iv = (ImageView) findViewById(R.id.menu_show_rides).findViewById(R.id.image_menu_item);
        iv.setImageResource(R.drawable.ic_action_maps_directions);
        iv = (ImageView) findViewById(R.id.menu_car_settings).findViewById(R.id.image_menu_item);
        iv.setImageResource(R.drawable.ic_action_action_settings);

//        Button button = (Button) findViewById(R.id.button_start_ride);
//        button.setOnClickListener(this);
//        button = (Button) findViewById(R.id.button_show_rides);
//        button.setOnClickListener(this);
//        button = (Button) findViewById(R.id.button_car_settings);
        findViewById(R.id.menu_car_settings).setOnClickListener(this);
        findViewById(R.id.menu_start_ride).setOnClickListener(this);
        findViewById(R.id.menu_show_rides).setOnClickListener(this);

        if(!App.driver.getUsername().equals(App.car.getAdmin())){
            findViewById(R.id.menu_car_settings).setVisibility(View.GONE);
        } else {
            findViewById(R.id.menu_car_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.menu_car_settings).setOnClickListener(this);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i = null;
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_logout:
                App.driver = null;
                // Delete last login in shared preferences
                SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(App.SP_LAST_LOGIN_USERNAME, null);
                editor.putString(App.SP_LAST_LOGIN_PASSWORD, null);
                editor.commit();
                // close activity and show login activity
                i = new Intent(CarActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_user_settings:
                i = new Intent(CarActivity.this, UserSettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_car_settings:
                i = new Intent(CarActivity.this, CarAddActivity.class);
                startActivity(i);
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.menu_start_ride:
                i = new Intent(CarActivity.this, MapsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_show_rides:
                i = new Intent(CarActivity.this, TripsOverviewActivity.class);
                startActivity(i);
                break;
            case R.id.menu_car_settings:
                i = new Intent(CarActivity.this, CarAddActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        App.car = null;
        super.onBackPressed();
    }
}