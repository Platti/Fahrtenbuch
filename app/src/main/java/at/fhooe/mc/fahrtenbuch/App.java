package at.fhooe.mc.fahrtenbuch;

import android.app.Application;
import at.fhooe.mc.fahrtenbuch.database.Connection;
import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;

public class App extends Application {

    public static final String SHARED_PREFERENCES = "at.fhooe.mc.fahrtenbuch";
    public static final String SP_LAST_LOGIN_USERNAME = "at.fhooe.mc.fahrtenbuch.username";
    public static final String SP_LAST_LOGIN_PASSWORD = "at.fhooe.mc.fahrtenbuch.password";
    public static Connection database;
    public static Driver driver;
    public static Car car;


    @Override
    public void onCreate() {
        super.onCreate();

        database = new at.fhooe.mc.fahrtenbuch.database.parse.Connection();
        database.init(this);
    }
}
