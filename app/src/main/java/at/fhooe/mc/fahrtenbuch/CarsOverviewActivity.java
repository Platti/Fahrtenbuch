
package at.fhooe.mc.fahrtenbuch;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;

public class CarsOverviewActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int NEWCAR_REQUEST_CODE = 1;
    public static List<Car> mCarList;
    public static ProgressDialog mLoadingDialog;


    NfcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_overview);
        if (App.driver != null) {
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
                        Car car = findCarWithNFCId(App.nfcId);
                        if (car != null) {
                            App.car = car;

                            Intent i = new Intent(CarsOverviewActivity.this, CarActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(CarsOverviewActivity.this, "Nfc doesn't work " + App.nfcId, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);


            mAdapter = NfcAdapter.getDefaultAdapter(this);

            if (!mAdapter.isEnabled()) {

                AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                alertbox.setTitle("Info");
                alertbox.setMessage(getString(R.string.msg_nfcon));
                alertbox.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    }
                });
                alertbox.setNegativeButton("Close", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertbox.show();

            }

            App.car = null;
            Button button = (Button) findViewById(R.id.add_new_car_button);
            button.setOnClickListener(this);
        } else if (App.driver == null) {
            Intent i = new Intent(CarsOverviewActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        readNFC(getIntent());

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
            App.car = App.database.getCars(App.driver).get(0);
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


    @Override
    public void onNewIntent(Intent _intent) {
        readNFC(_intent);
    }

    public void readNFC(Intent _intent){
        if(_intent != null) {
            if (_intent.getAction() != null && (_intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED) || _intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || _intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))) {
                if (_intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
                    Log.i(this.getClass().toString(), "Found Tech TAG");
                } else if (_intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                    Log.i(this.getClass().toString(), "Found NDEF TAG");
                } else if (_intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                    Log.i(this.getClass().toString(), "Found tag TAG");
                }
                Tag tag = _intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] bID = tag.getId();
                String id = convertToHexString(bID); // custom method to convert ID

                App.nfcId = id;
                Log.i(this.getClass().toString(), "Id: " + id);
                Car car = findCarWithNFCId(id);
                if (car != null) {
                    App.car = car;

                    Intent i = new Intent(CarsOverviewActivity.this, CarActivity.class);
                    startActivity(i);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.dialog_title_noNFC);
                    builder.setMessage(getString(R.string.dialog_text_noNFC));
                    builder.setNeutralButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            } else {
                App.car = null;
            }
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String convertToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            initForegroundDispatchMode();
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }

    private void initForegroundDispatchMode() throws IntentFilter.MalformedMimeTypeException {
        Intent i = new Intent(this, getClass());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        ndef.addDataType("*/*");
        IntentFilter[] filters = new IntentFilter[]{ndef, tech, tag};
        String[][] techList = new String[][]{
                new String[]{Ndef.class.getName()},
                new String[]{NfcA.class.getName()},
                new String[]{NfcB.class.getName()}}; // extend if necessary

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, pi, filters, techList);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_car_button:
                Intent i = new Intent(CarsOverviewActivity.this, CarAddActivity.class);
                startActivityForResult(i, NEWCAR_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        if (_requestCode == NEWCAR_REQUEST_CODE) {
            if (_resultCode == RESULT_OK) {
                Intent i = new Intent(CarsOverviewActivity.this, CarActivity.class);
                startActivity(i);
            }
        }
    }

    private Car findCarWithNFCId(String _id) {
        if (_id != null && mCarList != null) {
            for (Car car : mCarList) {
                if (car.getNFC() != null && car.getNFC().equals(_id)) {
                    Log.i(this.getLocalClassName(), "NFC-ID: " + App.nfcId + " Car: " + car.toString());
                    App.nfcId = null;

                    return car;
                }
            }
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
