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

/**
 * Database object class: Trip
 */
@ParseClassName("Trip")
public class Trip extends ParseObject {
    /**
     * first city of trip (just local variable)
     */
    private String firstCity;

    /**
     * last city of trip (just local variable)
     */
    private String lastCity;

    /**
     * list of geo points (just local variable)
     */
    private List<ParseGeoPoint> geoPoints;

    /**
     * weather (just local variable)
     */
    private Weather weather;

    /**
     * driver (foreign key)
     *
     * @return username of driver
     */
    public String getDriver() {
        return getString("driver");
    }


    /**
     * driver (foreign key)
     *
     * @param value username of driver
     */
    public void setDriver(String value) {
        put("driver", value);
    }

    /**
     * driver (foreign key)
     *
     * @param driver driver object
     */
    public void setDriver(Driver driver) {
        put("driver", driver.getUsername());
    }

    /**
     * car (foreign key)
     *
     * @return license plate of car
     */
    public String getCar() {
        return getString("car");
    }

    /**
     * car (foreign key)
     *
     * @param value license plate of car
     */
    public void setCar(String value) {
        put("car", value);
    }

    /**
     * car (foreign key)
     *
     * @param car car object
     */
    public void setCar(Car car) {
        put("car", car.getLicensePlate());
    }

    /**
     * distance in km
     *
     * @return distance in km
     */
    public int getDistance() {
        return getInt("distance");
    }

    /**
     * distance in km
     *
     * @param value distance in km
     */
    public void setDistance(int value) {
        put("distance", value);
    }

    /**
     * set distance in km according to the geo points
     */
    public void setDistance() {
        double distance = 0;
        List<ParseGeoPoint> geoPoints = getGeoPoints();
        for (int i = 0; i < geoPoints.size() - 1; i++) {
            distance += geoPoints.get(i).distanceInKilometersTo(geoPoints.get(i + 1));
        }
        setDistance((int) distance);
    }

    /**
     * time of departure
     *
     * @return time of departure
     */
    public Date getStartTime() {
        return getDate("startTime");
    }

    /**
     * time of departure
     *
     * @param value time of departure
     */
    public void setStartTime(Date value) {
        put("startTime", value);
    }

    /**
     * time of arrival
     *
     * @return time of arrival
     */
    public Date getStopTime() {
        return getDate("stopTime");
    }

    /**
     * time of arrival
     *
     * @param value time of arrival
     */
    public void setStopTime(Date value) {
        put("stopTime", value);
    }

    /**
     * weather
     *
     * @return weather object
     */
    public Weather getWeather() {
        if (weather == null) {
            weather = new Weather(getString("weatherCode"), getString("weatherDescription"));
        }
        return weather;
    }

    /**
     * weather
     *
     * @param value weather object
     */
    public void setWeather(Weather value) {
        weather = value;
        put("weatherCode", value.getCode());
        put("weatherDescription", value.getDescription());
    }

    /**
     * weather
     *
     * @param value weather description
     */
    public void setWeather(String value) {
        put("weatherDescription", value);
    }

    /**
     * feedback (1-5)
     *
     * @return feedback (1-5)
     */
    public int getFeedback() {
        return getInt("feedback");
    }

    /**
     * feedback (1-5)
     *
     * @param value feedback (1-5)
     */
    public void setFeedback(int value) {
        put("feedback", value);
    }

    /**
     * geo points
     *
     * @return list of go points
     */
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

    /**
     * first city of trip
     *
     * @param _context context to get city online
     * @return first city of trip or "---" if not found
     */
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

    /**
     * last city of trip
     *
     * @param _context context to get city online
     * @return last city of trip or "---" if not found
     */
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

    /**
     * geo points
     *
     * @param value JSON array of geo points
     */
    public void setGeoPoints(JSONArray value) {
        put("geoPoints", value);
    }

    /**
     * get city name of geo point
     *
     * @param geoPoint geo point
     * @param _context context
     * @return name of the city or null if not found
     */
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

    /**
     * description
     *
     * @param value description
     */
    public void setDescription(String value) {
        put("description", value);
    }

    /**
     * description
     *
     * @return description
     */
    public String getDescription() {
        return getString("description");
    }
}
