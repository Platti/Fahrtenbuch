package at.fhooe.mc.fahrtenbuch.database;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseGeoPoint;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import at.fhooe.mc.fahrtenbuch.R;

/**
 * container class for information about weather
 */
public class Weather {
    /**
     * icon code (OpenWeatherMap)
     */
    private String code;

    /**
     * description
     */
    private String description;

    /**
     * constructor
     *
     * @param _code        icon code
     * @param _description description
     */
    public Weather(String _code, String _description) {
        code = _code;
        description = _description;
    }

    /**
     * get drawable id from the icon code of this object
     *
     * @return drawable id
     */
    public int getIconID() {

        switch (code.substring(0, 2)) {
            case "01":
                return R.drawable.weather_clear;
            case "02":
                return R.drawable.weather_few_clouds;
            case "03":
                return R.drawable.weather_scattered_clouds;
            case "04":
                return R.drawable.weather_broken_clouds;
            case "09":
                return R.drawable.weather_shower_rain;
            case "10":
                return R.drawable.weather_rain;
            case "11":
                return R.drawable.weather_thunder;
            case "13":
                return R.drawable.weather_snow;
            case "50":
                return R.drawable.weather_fog;
            default:
                return 0;
        }
    }

    /**
     * weather description
     *
     * @return weather description
     */
    public String getDescription() {
        return description;
    }

    /**
     * icon code
     *
     * @return icon code
     */
    public String getCode() {
        return code;
    }

    /**
     * template for api request
     */
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric";

    /**
     * get current weather asynchronous
     *
     * @param geoPoint coordinates of the city
     * @param callback method to handle the result
     */
    public static void get(final ParseGeoPoint geoPoint, final Weather.Callback callback) {

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    URL url = new URL(String.format(OPEN_WEATHER_MAP_API, geoPoint.getLatitude(), geoPoint.getLongitude()));
                    Log.e("Weather", "URL: " + url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.addRequestProperty("x-api-key", "a844e4d30bfd2ca3a6ef77f231ac34de");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null) {
                        json.append(tmp).append("\n");
                    }
                    reader.close();

                    JSONObject data = new JSONObject(json.toString());

                    Log.e("Weather", "Code: " + data.getInt("cod"));

                    // This value will be 404 if the request was not successful
                    if (data.getInt("cod") != 200) {
                        return null;
                    }

                    JSONObject weather = data.getJSONArray("weather").getJSONObject(0);
                    String code = weather.getString("icon");
                    String description = weather.getString("description");

                    callback.done(new Weather(code, description), null);

                } catch (Exception e) {
                    callback.done(null, e);
                }
                return null;
            }
        };
        task.execute();
    }

    /**
     * callback interface for weather requests
     */
    public interface Callback {
        public void done(Weather weather, Exception e);
    }

//    Tutorial:
//    ---------
//    Weather.get(geoPoints.get(geoPoint, new Weather.Callback() {
//        @Override
//        public void done(Weather weather, Exception e) {
//            if (e == null) {
//                // Use weather here
//            } else {
//                e.printStackTrace();
//            }
//        }
//    });

}


