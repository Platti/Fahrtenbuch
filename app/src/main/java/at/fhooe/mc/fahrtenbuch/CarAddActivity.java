
package at.fhooe.mc.fahrtenbuch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;
import at.fhooe.mc.fahrtenbuch.database.parse.DriverCarMapping;

public class CarAddActivity extends ActionBarActivity implements View.OnClickListener {

    Context mContext = this;

    public Car mNewCar;

    EditText mTextFieldMake;
    EditText mTextFieldModel;
    EditText mTextFieldNumber;
    EditText mTextFieldKilometers;
    Button mNFCButton;
    TextView mAddUser;

    NfcAdapter mAdapter;
    AlertDialog mNfcReadInDialog;
    ProgressDialog mLoadingDialog;

    EditText mTextFieldUser;

    public static List<Driver> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);
        TextView currentUser = (TextView) findViewById(R.id.header_currentUser).findViewById(R.id.layout_header_txt);
        currentUser.setText(App.driver.getFirstName() + " " + App.driver.getLastName());
        TextView entitledUser = (TextView) findViewById(R.id.header_entitledUser).findViewById(R.id.layout_header_txt);
        entitledUser.setText(R.string.header_entitledUser);


        mTextFieldMake = (EditText) findViewById(R.id.textfield_make);
        mTextFieldModel = (EditText) findViewById(R.id.textfield_model);
        mTextFieldNumber = (EditText) findViewById(R.id.textfield_number);
        mTextFieldKilometers = (EditText) findViewById(R.id.textfield_kilometers);


        mNFCButton = (Button) findViewById(R.id.button_nfc);
        mNFCButton.setOnClickListener(this);



        if(App.car == null){
            mNewCar = new Car();
        } else {
            mNewCar = App.car;
            setTitle(R.string.title_activity_car_settings);

            initUserList();

            if(!App.car.getMake().equals("")){
                mTextFieldMake.setText(App.car.getMake());
            }

            if(!App.car.getModel().equals("")){
                mTextFieldModel.setText(App.car.getModel());
            }
            if(!App.car.getLicensePlate().equals("")){
                mTextFieldNumber.setText(App.car.getLicensePlate());
                mTextFieldNumber.setEnabled(false);
            }
            if(App.car.getMileage() != 0){
                mTextFieldKilometers.setText(String.valueOf(App.car.getMileage()));
            }
            if(App.car.getNFC() != null && !App.car.getNFC().equals("")){
                mNFCButton.setText(R.string.button_nfc_change);
            }
        }

        mAddUser = (TextView) findViewById(R.id.action_add_new_user);
        mAddUser.setOnClickListener(this);

        if (mAdapter!= null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    private void initUserList(){
        mUserList = new ArrayList<>();
        final ListView listView = (ListView) findViewById(R.id.listEnabledUser);
        final ListViewUserAdapter adapter = new ListViewUserAdapter(getBaseContext());
        adapter.mActivity = this;

        App.database.getDrivers(App.car, new FindCallback<Driver>() {
            @Override
            public void done(List<Driver> drivers, ParseException e) {
                if (e == null) {
                    for (Driver d : drivers) {
                        if(!d.getUsername().equals(mNewCar.getAdmin())){
                            adapter.add(d);
                            mUserList.add(d);
                        }

                    }
                }
            }
        });
        listView.setAdapter(adapter);
    }

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
        boolean var = true;
        switch (item.getItemId()){
            case R.id.action_done:
                saveAndReturn();
                var = super.onOptionsItemSelected(item);
                break;
            case android.R.id.home:
                onBackPressed();

        }
        return var;
    }

    private void saveAndReturn(){
        String make = mTextFieldMake.getText().toString();
        String modell = mTextFieldModel.getText().toString();
        String number = mTextFieldNumber.getText().toString();
        String km = mTextFieldKilometers.getText().toString();

        if(make != null && modell != null && number != null && km != null &&
                !make.equals("") && !modell.equals("") && !number.equals("") && !km.equals("")){

            mNewCar.setAdmin(App.driver);
            mNewCar.setMake(make);
            mNewCar.setModel(modell);
            mNewCar.setLicensePlate(number);
            int milage = Integer.parseInt(km);
            mNewCar.setMileage(milage);
            mLoadingDialog = new ProgressDialog(CarAddActivity.this);
            mLoadingDialog.setIndeterminate(true);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setMessage(getString(R.string.saving));
            mLoadingDialog.show();
            if(App.car == null) {
                App.database.addCar(mNewCar, new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        mLoadingDialog.dismiss();
                        if (e == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.new_car_saved), Toast.LENGTH_LONG).show();
                                }
                            });
                            App.car = mNewCar;
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
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
            }else {
                mNewCar.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        mLoadingDialog.dismiss();
                        if(e==null){
                            App.car = mNewCar;
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
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
            }

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
                    mNewCar = null;
                    finish();
                }
            });
            builder.show();
        }

    }

    private void save(){
        String make = mTextFieldMake.getText().toString();
        String modell = mTextFieldModel.getText().toString();
        String number = mTextFieldNumber.getText().toString();
        String km = mTextFieldKilometers.getText().toString();

        if(make != null && modell != null && number != null && km != null &&
                !make.equals("") && !modell.equals("") && !number.equals("") && !km.equals("")){

            mNewCar.setAdmin(App.driver);
            mNewCar.setMake(make);
            mNewCar.setModel(modell);
            mNewCar.setLicensePlate(number);
            int milage = Integer.parseInt(km);
            mNewCar.setMileage(milage);
            mLoadingDialog = new ProgressDialog(CarAddActivity.this);
            mLoadingDialog.setIndeterminate(true);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setMessage(getString(R.string.saving));
            mLoadingDialog.show();

                App.database.addCar(mNewCar, new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        mLoadingDialog.dismiss();
                        if (e == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.new_car_saved), Toast.LENGTH_LONG).show();
                                }
                            });
                            App.car = mNewCar;
                            AlertDialog.Builder userBuilder = new AlertDialog.Builder(CarAddActivity.this);
                            userBuilder.setTitle(R.string.dialog_title_addUser);
                            LayoutInflater inflater = CarAddActivity.this.getLayoutInflater();
                            userBuilder.setMessage(getString(R.string._dialog_text_addUser));
                            View dialogView = inflater.inflate(R.layout.dialog_textfield, null);
                            userBuilder.setView(dialogView);
                            EditText et = (EditText) dialogView.findViewById(R.id.dialog_textfield);
                            et.setHint(R.string.username);
                            userBuilder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mLoadingDialog = new ProgressDialog(CarAddActivity.this);
                                    mLoadingDialog.setIndeterminate(true);
                                    mLoadingDialog.setCanceledOnTouchOutside(false);
                                    mLoadingDialog.setMessage(getString(R.string.try_user));
                                    mLoadingDialog.show();
                                    mTextFieldUser = (EditText) ((Dialog) dialogInterface).findViewById(R.id.dialog_textfield);
                                    if (mTextFieldUser != null) {
                                        App.database.linkDriverToCar(mTextFieldUser.getText().toString(), mNewCar.getLicensePlate(), new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                mLoadingDialog.dismiss();
                                                if (e == null) {
                                                    initUserList();
                                                    Toast.makeText(CarAddActivity.this, getString(R.string.add_usr) + mTextFieldUser.getText().toString() + getString(R.string.success), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(CarAddActivity.this, getString(R.string.error_dots) + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        mLoadingDialog.dismiss();
                                        Toast.makeText(CarAddActivity.this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            userBuilder.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            userBuilder.create().show();
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
                    mNewCar = null;
                    finish();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_nfc:
                mAdapter = NfcAdapter.getDefaultAdapter(this);
                if (!mAdapter.isEnabled()) {

                    AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                    alertbox.setTitle(R.string.info);
                    alertbox.setMessage(getString(R.string.msg_nfcon));
                    alertbox.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
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
                    alertbox.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertbox.show();

                }
                if (mAdapter.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.dialog_title_nfcReadIn);
                    builder.setMessage(getString(R.string.dialog_text_nfcReadIn));
                    builder.setNeutralButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    try {
                        initForegroundDispatchMode();
                    } catch (IntentFilter.MalformedMimeTypeException e) {
                        e.printStackTrace();
                    }
                    mNfcReadInDialog = builder.create();

                    mNfcReadInDialog.show();
                }

                break;
            case R.id.action_add_new_user:
                if (App.car == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.dialog_title_saveBeforeAddUser);
                    builder.setMessage(getString(R.string.dialog_text_saveBeforeAddUser));

                    builder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            save();
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(CarAddActivity.this, R.string.impossible_to_add_user, Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.create().show();
                } else {

                    AlertDialog.Builder userBuilder = new AlertDialog.Builder(this);
                    userBuilder.setTitle(R.string.dialog_title_addUser);
                    LayoutInflater inflater = this.getLayoutInflater();
                    userBuilder.setMessage(getString(R.string._dialog_text_addUser));
                    View dialogView = inflater.inflate(R.layout.dialog_textfield, null);
                    userBuilder.setView(dialogView);
                    EditText et = (EditText) dialogView.findViewById(R.id.dialog_textfield);
                    et.setHint(R.string.username);
                    userBuilder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mLoadingDialog = new ProgressDialog(CarAddActivity.this);
                            mLoadingDialog.setIndeterminate(true);
                            mLoadingDialog.setCanceledOnTouchOutside(false);
                            mLoadingDialog.setMessage(getString(R.string.try_user));
                            mLoadingDialog.show();
                            mTextFieldUser = (EditText) ((Dialog) dialogInterface).findViewById(R.id.dialog_textfield);
                            if (mTextFieldUser != null) {
                                App.database.linkDriverToCar(mTextFieldUser.getText().toString(), mNewCar.getLicensePlate(), new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        mLoadingDialog.dismiss();
                                        if (e == null) {
                                            initUserList();
                                            Toast.makeText(CarAddActivity.this, getString(R.string.add_usr) + mTextFieldUser.getText().toString() + getString(R.string.success), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(CarAddActivity.this, getString(R.string.error_dots) + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                mLoadingDialog.dismiss();
                                Toast.makeText(CarAddActivity.this, getString(R.string.error_input), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    userBuilder.setNegativeButton(R.string.dialog_cancle_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    userBuilder.create().show();
                }
                break;


        }
    }

    @Override
    public void onNewIntent(Intent _intent) {
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
        mNewCar.setNFC(id);
        Toast.makeText(CarAddActivity.this, getString(R.string.toast_message_add_new_nfc), Toast.LENGTH_LONG).show();
        mNfcReadInDialog.dismiss();

        Toast.makeText(CarAddActivity.this, getString(R.string.toast_message_add_new_nfc), Toast.LENGTH_LONG).show();

        Log.i(this.getClass().toString(), "Id: " + id + "yeayea");
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String convertToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter!= null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter!= null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        saveChanges();
    }

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
                mNewCar = null;
                finish();
            }
        });
        builder.show();
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

        if (mAdapter!= null) {
            mAdapter.enableForegroundDispatch(this, pi, filters, techList);
        }
    }

    public interface Callback {
        public void done(Exception e);
    }

}
