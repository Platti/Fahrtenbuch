package at.fhooe.mc.fahrtenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

public class CarsOverviewActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public static List<Car> mCarList;
    public static ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_overview);
        App.car = null;


        mCarList = new ArrayList<>();

        final ListView listView = (ListView) findViewById(R.id.list_view_cars);
        final ListViewCarsAdapter adapter = new ListViewCarsAdapter(getBaseContext());
        adapter.mActivity = this;
        App.database.getCars(App.driver, new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e == null) {
                    for (Car car : cars) {
                        adapter.add(car);
                        mCarList.add(car);
                    }
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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
            editor.apply();
            // close activity and show login activity
            Intent i = new Intent(CarsOverviewActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.action_test1) {
            App.database.test();
        } else if (id == R.id.action_test2) {
            App.car = App.database.getCars(App.driver).get(0); // TODO: zum testen synchron, daher verzögerung bei click auf menu
            Intent i = new Intent(CarsOverviewActivity.this, TripsOverviewActivity.class);
            startActivity(i);
        } else if (id == R.id.action_testMap) {
            Intent i = new Intent(CarsOverviewActivity.this, MapsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        App.car = (Car) parent.getAdapter().getItem(pos);
        Intent i = new Intent(CarsOverviewActivity.this, CarActivity.class);
        startActivity(i);
    }
}
