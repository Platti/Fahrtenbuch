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

    public static final String SHARED_PREFERENCES = "at.fhooe.mc.fahrtenbuch";
    public static final String SP_LAST_LOGIN_USERNAME = "at.fhooe.mc.fahrtenbuch.username";
    public static final String SP_LAST_LOGIN_PASSWORD = "at.fhooe.mc.fahrtenbuch.password";
    public static Connection database;
    public static Driver driver;
    public static Car car;
    public static Trip trip;
    public static String nfcId;


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
