package at.fhooe.mc.fahrtenbuch.database;

import android.app.Application;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.SaveCallback;

import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.parse.Car;
import at.fhooe.mc.fahrtenbuch.database.parse.Driver;
import at.fhooe.mc.fahrtenbuch.database.parse.Trip;

public interface Connection {
    public void init(Application app);
    public void test();

    public Driver loginUser(String username, String password);
    public void loginUser(String username, String password, GetCallback<Driver> callback);

    public List<Car> getCars(Driver driver);
    public void getCars(Driver driver, FindCallback<Car> callback);

    public List<Trip> getTrips(Car car);
    public void getTrips(Car car, FindCallback<Trip> callback);

    public boolean store(Trip trip);
    public void store(Trip trip, SaveCallback callback);

    public boolean registerUser(Driver driver);
    public void registerUser(Driver driver, SaveCallback callback);

    public void addCar(Car car, SaveCallback callback);

}
