package at.fhooe.mc.fahrtenbuch;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import at.fhooe.mc.fahrtenbuch.database.Connection;
import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;
import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

public class App extends Application {

    /**
     * Tag of the shared preferences
     */
    public static final String SHARED_PREFERENCES = "at.fhooe.mc.fahrtenbuch";

    /**
     * Tag of the last logged in username in the shared preferences
     */
    public static final String SP_LAST_LOGIN_USERNAME = "at.fhooe.mc.fahrtenbuch.username";

    /**
     * Tag of the last logged in encrypted password in the shared preferences
     */
    public static final String SP_LAST_LOGIN_PASSWORD = "at.fhooe.mc.fahrtenbuch.password";

    /**
     * global variable of the database connection
     */
    public static Connection database;

    /**
     * global variable of the logged in driver
     */
    public static Driver driver;

    /**
     * global variable of the chosen car
     */
    public static Car car;

    /**
     * global variable of the chosen trip
     */
    public static Trip trip;

    /**
     * global variable of the last read nfc tag
     */
    public static String nfcId;

    /**
     * initialize database connection
     */
    @Override
    public void onCreate() {
        super.onCreate();

        database = new at.fhooe.mc.fahrtenbuch.database.parse.Connection();
        database.init(this);
    }

//    public static void activateNFC(Context _context){
//        String packageName = _context.getPackageName();
//        ComponentName componentNFC = new ComponentName(packageName, packageName + ".nfc_filter");
//        _context.getPackageManager().setComponentEnabledSetting(componentNFC, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
//    }
//
//    public static void deactivateNFC(Context _context){
//        String packageName = _context.getPackageName();
//        ComponentName componentNFC = new ComponentName(packageName, packageName + ".nfc_filter");
//        _context.getPackageManager().setComponentEnabledSetting(componentNFC, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
//    }
}
