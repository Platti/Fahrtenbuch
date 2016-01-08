package at.fhooe.mc.fahrtenbuch.database;

import android.app.Application;

import com.parse.GetCallback;

import at.fhooe.mc.fahrtenbuch.database.parse.Driver;

public interface Connection {
    public void init(Application app);
    public void test();

    public Driver loginUser(String username, String password);
    public void loginUser(String username, String password, GetCallback<Driver> callback);

}
