package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

@ParseClassName("Trip")
public class Trip extends ParseObject {
    public String getDriver() {
        return getString("driver");
    }

    public void setDriver(String value) {
        put("driver", value);
    }

    public String getCar() {
        return getString("car");
    }

    public void setCar(String value) {
        put("car", value);
    }

    public int getDistance() {
        return getInt("distance");
    }

    public void setDistance(int value) {
        put("distance", value);
    }

    public String getWeather() {
        return getString("weather");
    }

    public void setWeather(String value) {
        put("weather", value);
    }

    public int getFeedback() {
        return getInt("feedback");
    }

    public void setFeedback(int value) {
        put("feedback", value);
    }

    public JSONArray getGeoPoints() {
        return getJSONArray("geoPoints");
    }

    public void setGeoPoints(JSONArray value) {
        put("geoPoints", value);
    }
}
