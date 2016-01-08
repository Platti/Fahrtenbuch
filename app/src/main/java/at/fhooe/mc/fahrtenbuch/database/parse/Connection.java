package at.fhooe.mc.fahrtenbuch.database.parse;

import android.app.Application;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.MD5;

public class Connection implements at.fhooe.mc.fahrtenbuch.database.Connection {
    @Override
    public void init(Application app) {
        try {
            // Enable Local Datastore.
            Parse.enableLocalDatastore(app);
            ParseObject.registerSubclass(Car.class);
            ParseObject.registerSubclass(Driver.class);
            Parse.initialize(app, "U1UjN1fYUQhft7FVuLly4G4k0RGNY6oRfYhSqNhC", "4msv2gumPHXxQ2HmT8AqHMrXLQaONvHChw2eU951");
        } catch (IllegalStateException e) {
            // already initialized
        }
    }

    @Override
    public void test() {
        final String TAG = "TestingDatabase";

        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.findInBackground(new FindCallback<Car>() {
            @Override
            public void done(List<Car> results, ParseException e) {
                for (Car car : results) {
                    Log.e(TAG, car.toString());
                }
            }
        });

        Log.e(TAG, "Decrypted: password --- Encrypted: " + MD5.encrypt("password"));

        // -----------------------------------------------------------------------------------------
        // Beispiel User Login:

        // --> Synchrone Suche des Users: UI wird blockiert, gefundenes Driver-Objekt wird zurückgeliefert
        Log.e(TAG, "User Login: " + loginUser("jondoe", "password").toString());

        // --> Asynchrone Suche des Users: UI wird NICHT blockiert, Ergebnis wird in der übergebenen Callback-Methode bearbeitet
        loginUser("jondoe", "password", new GetCallback<Driver>() {
            @Override
            public void done(Driver driver, ParseException e) {
                Log.e(TAG, "User Login: " + driver.toString());
            }
        });
        // -----------------------------------------------------------------------------------------

        Log.e(TAG, "Testing finished!");



        /*
        ParseObject testTrip = new ParseObject("Trip");
        testTrip.put("driver", "jondoe");
        testTrip.put("car", "FR-TEST1");
        testTrip.put("distance", 55);
        testTrip.put("weather", "cloudy");
        testTrip.put("feedback", 1);

        JSONArray points = new JSONArray();
        points.put(new ParseGeoPoint(47.273466, 11.241875));
        points.put(new ParseGeoPoint(47.270464, 11.256268));
        points.put(new ParseGeoPoint(47.265146, 11.274732));
        points.put(new ParseGeoPoint(47.263839, 11.315825));
        points.put(new ParseGeoPoint(47.254158, 11.359128));
        points.put(new ParseGeoPoint(47.256115, 11.381890));
        points.put(new ParseGeoPoint(47.262558, 11.384723));
        testTrip.put("geoPoints", points);

        testTrip.saveInBackground();
        */
    }

    /**
     * Synchronous Login (UI blocked)
     *
     * @param username username
     * @param password password
     * @return Driver object, null if not found
     */
    @Override
    public Driver loginUser(String username, String password) {
        ParseQuery<Driver> query = ParseQuery.getQuery(Driver.class);
        try {
            query.whereEqualTo("username", username);
            query.whereEqualTo("password", MD5.encrypt(password));
            return query.getFirst();
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Asynchronous Login
     *
     * @param username username
     * @param password password
     * @param callback method which handles the result
     */
    @Override
    public void loginUser(String username, String password, GetCallback<Driver> callback) {
        ParseQuery<Driver> query = ParseQuery.getQuery(Driver.class);

        query.whereEqualTo("username", username);
        query.whereEqualTo("password", MD5.encrypt(password));
        query.getFirstInBackground(callback);
    }
}
