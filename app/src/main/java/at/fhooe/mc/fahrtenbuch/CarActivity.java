package at.fhooe.mc.fahrtenbuch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import at.fhooe.mc.fahrtenbuch.R;

public class CarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        setTitle(App.car.getMake() + " " + App.car.getModel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cars_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            // Logout
            App.driver = null;
            // Delete last login in shared preferences
            SharedPreferences sp = getSharedPreferences(App.SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(App.SP_LAST_LOGIN_USERNAME, null);
            editor.putString(App.SP_LAST_LOGIN_PASSWORD, null);
            editor.commit();
            // close activity and show login activity
            Intent i = new Intent(CarActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.action_test1) {
            App.database.test();
        } else if (id == R.id.action_test2) {
            App.car = App.database.getCars(App.driver).get(0); // TODO: zum testen synchron, daher verzögerung bei click auf menu
            Intent i = new Intent(CarActivity.this, TripsOverviewActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


}
