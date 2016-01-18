package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Trip")
public class Trip extends ParseObject {
    public String getDriver() {
        return getString("driver");
    }

    public void setDriver(String value) {
        put("driver", value);
    }

    public void setDriver(Driver driver) {
        put("driver", driver.getUsername());
    }

    public String getCar() {
        return getString("car");
    }

    public void setCar(String value) {
        put("car", value);
    }

    public void setCar(Car car) {
        put("car", car.getLicensePlate());
    }

    public int getDistance() {
        return getInt("distance");
    }

    public void setDistance(int value) {
        put("distance", value);
    }

    public Date getStartTime() {
        return getDate("startTime");
    }

    public void setStartTime(Date value) {
        put("startTime", value);
    }

    public Date getStopTime() {
        return getDate("stopTime");
    }

    public void setStopTime(Date value) {
        put("stopTime", value);
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
