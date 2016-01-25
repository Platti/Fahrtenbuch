
package at.fhooe.mc.fahrtenbuch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;

public class CarAddActivity extends ActionBarActivity implements View.OnClickListener {

    Context mContext = this;

    Car mNewCar;

    EditText mTextFieldMake;
    EditText mTextFieldModel;
    EditText mTextFieldNumber;
    EditText mTextFieldKilometers;
    Button mNFCButton;
    TextView mAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);

        TextView currentUser = (TextView) findViewById(R.id.header_currentUser).findViewById(R.id.layout_header_txt);
        currentUser.setText(App.driver.getFirstName() + " " + App.driver.getLastName());
        TextView entitledUser = (TextView) findViewById(R.id.header_entitledUser).findViewById(R.id.layout_header_txt);
        entitledUser.setText(R.string.header_entitledUser);

        mNewCar = new Car();

        mTextFieldMake = (EditText) findViewById(R.id.textfield_make);
        mTextFieldModel = (EditText) findViewById(R.id.textfield_model);
        mTextFieldNumber = (EditText) findViewById(R.id.textfield_number);
        mTextFieldKilometers = (EditText) findViewById(R.id.textfield_kilometers);

        mNFCButton = (Button) findViewById(R.id.button_nfc);
        mNFCButton.setOnClickListener(this);

        mAddUser = (TextView) findViewById(R.id.action_add_new_user);
        mAddUser.setOnClickListener(this);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        View actionBarButtons = getLayoutInflater().inflate(R.layout.actionbar_save_button,
//                new LinearLayout(mContext), false);
//
//        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
//        doneActionView.setOnClickListener(this);
//
//        ActionBar actionBar = this.getSupportActionBar();
//
//        if (actionBar != null) {
//            actionBar.setCustomView(actionBarButtons);
//            actionBar.setDisplayShowCustomEnabled(true);
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_done:
                String make = mTextFieldMake.getText().toString();
                String modell = mTextFieldModel.getText().toString();
                String number = mTextFieldNumber.getText().toString();
                String km = mTextFieldKilometers.getText().toString();

                if(make != null && modell != null && number != null && km != null &&
                        !make.equals("") && !modell.equals("") && !number.equals("") && !km.equals("")){

                    mNewCar.setAdmin(App.driver);
                    mNewCar.setMake(make);
                    mNewCar.setLicensePlate(number);
                    int milage = Integer.parseInt(km);
                    mNewCar.setMileage(milage);
                    mNewCar.saveInBackground();
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.button_nfc:

                break;
            case R.id.action_add_new_user:

                break;
        }
    }

}
