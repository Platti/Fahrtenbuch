package at.fhooe.mc.fahrtenbuch.database.parse;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.fhooe.mc.fahrtenbuch.database.Weather;

@ParseClassName("Trip")
public class Trip extends ParseObject {
    private String firstCity, lastCity;
    private List<ParseGeoPoint> geoPoints;
    private Weather weather;

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

    public Weather getWeather() {
        if (weather == null) {
            weather = new Weather(getString("weatherCode"), getString("weatherDescription"));
        }
        return weather;
    }

    public void setWeather(Weather value) {
        weather = value;
        put("weatherCode", value.getCode());
        put("weatherDescription", value.getDescription());
    }

    public int getFeedback() {
        return getInt("feedback");
    }

    public void setFeedback(int value) {
        put("feedback", value);
    }

    public List<ParseGeoPoint> getGeoPoints() {
        if (geoPoints == null) {
            geoPoints = new ArrayList<>();
            try {
                JSONArray array = getJSONArray("geoPoints");
                for (int i = 0; i < array.length(); i++) {
                    geoPoints.add(new ParseGeoPoint(array.getJSONObject(i).getDouble("latitude"), array.getJSONObject(i).getDouble("longitude")));
                }
            } catch (JSONException e) {
                Log.e("Trip.getGeoPoints", e.getMessage());
            } catch (NullPointerException e) {
                Log.e("Trip.getGeoPoints", "no GeoPoints stored for this trip");
            }
        }
        return geoPoints;
    }

    public String getFirstCity(Context _context) {
        if (firstCity == null) {
            int i = 0;
            while (firstCity == null && i < getGeoPoints().size()) {
                firstCity = getCity(getGeoPoints().get(i), _context);
                i++;
            }
        }
        return firstCity == null ? "---" : firstCity;
    }

    public String getLastCity(Context _context) {
        if (lastCity == null) {
            int i = getGeoPoints().size() - 1;
            while (lastCity == null && i >= 0) {
                lastCity = getCity(getGeoPoints().get(i), _context);
                i--;
            }
        }
        return lastCity == null ? "---" : lastCity;
    }

    public void setGeoPoints(JSONArray value) {
        put("geoPoints", value);
    }

    private String getCity(ParseGeoPoint geoPoint, Context _context) {
        try {
            Geocoder gcd = new Geocoder(_context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
