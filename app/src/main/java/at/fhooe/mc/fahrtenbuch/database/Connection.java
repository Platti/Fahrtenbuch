package at.fhooe.mc.fahrtenbuch.database;

import android.app.Application;

public interface Connection {
    public void init(Application app);
    public void test();
}
