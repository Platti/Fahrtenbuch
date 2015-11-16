package at.fhooe.mc.fahrtenbuch.database.parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;

public class Connection implements at.fhooe.mc.fahrtenbuch.database.Connection {
    @Override
    public void init(Application app) {
        try {
            // Enable Local Datastore.
            Parse.enableLocalDatastore(app);
            Parse.initialize(app, "U1UjN1fYUQhft7FVuLly4G4k0RGNY6oRfYhSqNhC", "4msv2gumPHXxQ2HmT8AqHMrXLQaONvHChw2eU951");
        } catch (IllegalStateException e) {
            // already initialized
        }
    }

    @Override
    public void test() {
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
    }
}
