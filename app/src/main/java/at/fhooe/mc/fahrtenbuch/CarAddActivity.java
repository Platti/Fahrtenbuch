
package at.fhooe.mc.fahrtenbuch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class CarAddActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);

        TextView currentUser = (TextView) findViewById(R.id.header_currentUser).findViewById(R.id.layout_header_txt);
        currentUser.setText(App.driver.getFirstName() + " " + App.driver.getLastName());
        TextView entitledUser = (TextView) findViewById(R.id.header_entitledUser).findViewById(R.id.layout_header_txt);
        entitledUser.setText(R.string.header_entitledUser);

    }

}
