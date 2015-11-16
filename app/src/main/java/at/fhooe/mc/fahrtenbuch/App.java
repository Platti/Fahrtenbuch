package at.fhooe.mc.fahrtenbuch;

import android.app.Application;
import at.fhooe.mc.fahrtenbuch.database.Connection;

public class App extends Application {

    public static Connection database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = new at.fhooe.mc.fahrtenbuch.database.parse.Connection();
        database.init(this);
    }
}
