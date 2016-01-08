package at.fhooe.mc.fahrtenbuch;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = null;

        b = (Button) findViewById(R.id.karteButton);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.buttonTestDB);
        b.setOnClickListener(this);
        // Add Test-Trip to database
//        App.database.test();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.karteButton: {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                break;
            }
            case R.id.buttonTestDB: {
                Toast.makeText(this, "Testing database stuff...", Toast.LENGTH_SHORT).show();
                App.database.test();
                break;
            }
        }
    }
}
