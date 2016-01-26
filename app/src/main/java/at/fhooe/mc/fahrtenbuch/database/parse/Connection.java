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
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import at.fhooe.mc.fahrtenbuch.database.MD5;
import at.fhooe.mc.fahrtenbuch.database.Weather;

public class Connection implements at.fhooe.mc.fahrtenbuch.database.Connection {
    @Override
    public void init(Application app) {
        try {
            // Enable Local Datastore.
            Parse.enableLocalDatastore(app);
            ParseObject.registerSubclass(Car.class);
            ParseObject.registerSubclass(Driver.class);
            ParseObject.registerSubclass(DriverCarMapping.class);
            ParseObject.registerSubclass(Trip.class);
            Parse.initialize(app, "U1UjN1fYUQhft7FVuLly4G4k0RGNY6oRfYhSqNhC", "4msv2gumPHXxQ2HmT8AqHMrXLQaONvHChw2eU951");
        } catch (IllegalStateException e) {
            // already initialized
        }
    }

    @Override
    public void test() {
        final String TAG = "TestingDatabase";

        Log.e(TAG, "Decrypted: password --- Encrypted: " + MD5.encrypt("password"));

        // -----------------------------------------------------------------------------------------
        // Beispiel User Login:

        // --> Synchrone Suche des Users: UI wird blockiert, gefundenes Driver-Objekt wird zurückgeliefert
        final Driver driver = loginUser("jondoe", MD5.encrypt("password"));
        if (driver != null) {
            Log.e(TAG, "User Login: " + driver.toString());
        }

        // --> Asynchrone Suche des Users: UI wird NICHT blockiert, Ergebnis wird in der übergebenen Callback-Methode bearbeitet
        loginUser("jondoe", MD5.encrypt("password"), new GetCallback<Driver>() {
            @Override
            public void done(Driver driver, ParseException e) {
                if (e == null) {
                    Log.e(TAG, "User Login: " + driver.toString());
                } else {
                    Log.e(TAG, "Login failed: " + e.getMessage());
                }
            }
        });
        // -----------------------------------------------------------------------------------------

        // Beispiel: Autos eines Fahrers suchen
        // Synchron:
//        List<Car> cars = getCars(driver);
//        Log.e(TAG, "Synchronous getCars-Query:");
//        for (Car car : cars) {
//            Log.e(TAG, driver.toString() + " - " + car.toString());
//        }

        // Asynchron:
        getCars(driver, new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Asynchronous getCars-Query:");
                    for (Car car : cars) {
                        Log.e(TAG, driver.toString() + " - " + car.toString());
                        Log.e(TAG, "Car has NFC: " + car.hasNFC());
                        if (car.hasNFC()) {
                            Log.e(TAG, "NFC-TAG: #" + car.getNFC() + "#");
                        }
                    }
                } else {
                    Log.e(TAG, "Query failed: " + e.getMessage());
                }
            }
        });

        // Beispiel: Fahrten eines Autos suchen
        // Synchron:
//        List<Trip> trips = getTrips(cars.get(0));
//        Log.e(TAG, "Synchronous getTrips-Query:");
//        for (Trip trip : trips) {
//            Log.e(TAG, trip.toString());
//        }

        // Asynchron:
//        getTrips(cars.get(0), new FindCallback<Trip>() {
//            @Override
//            public void done(List<Trip> trips, ParseException e) {
//                if (e == null) {
//                    for (Trip trip : trips) {
//                        Log.e(TAG, trip.toString());
//                    }
//                } else {
//                    Log.e(TAG, "Query failed: " + e.getMessage());
//                }
//            }
//        });

        // Beispiel: Neue Fahrt speichern
        Trip trip = new Trip();
        trip.setCar("FR-TEST1");
        trip.setDriver("jondoe");
        trip.setDistance(100);
        trip.setFeedback(100);
        trip.setWeather(new Weather("01d", "sunny"));
        JSONArray points = new JSONArray();
        points.put(new ParseGeoPoint(47.273466, 11.241875));
        points.put(new ParseGeoPoint(47.270464, 11.256268));
        points.put(new ParseGeoPoint(47.265146, 11.274732));
        points.put(new ParseGeoPoint(47.263839, 11.315825));
        points.put(new ParseGeoPoint(47.254158, 11.359128));
        points.put(new ParseGeoPoint(47.256115, 11.381890));
        points.put(new ParseGeoPoint(47.262558, 11.384723));
        trip.setGeoPoints(points);

        // Synchronous
//        if(store(trip)){
//            Log.e(TAG, "Storing succeeded!");
//        } else {
//            Log.e(TAG, "Storing failed!");
//        }
//        Log.e(TAG, "Checkpoint after storing trip");

        //Asynchronous
        store(trip, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Storing succeeded!");
                } else {
                    Log.e(TAG, "Storing failed: " + e.getMessage());
                }
            }
        });



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
        query.whereEqualTo("username", username);
        query.whereEqualTo("password", password);
        try {
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
        query.whereEqualTo("password", password);
        query.getFirstInBackground(callback);
    }

    /**
     * Get all cars linked to a driver (synchonous)
     *
     * @param driver driver
     * @return list of all cars linked to the driver
     */
    @Override
    public List<Car> getCars(Driver driver) {
        ParseQuery<DriverCarMapping> queryMapping = ParseQuery.getQuery(DriverCarMapping.class);
        queryMapping.whereEqualTo("driver", driver.getUsername());
        ParseQuery<Car> queryCar = ParseQuery.getQuery(Car.class);
        queryCar.whereMatchesKeyInQuery("licensePlate", "car", queryMapping);

        try {
            return queryCar.find();
        } catch (ParseException e) {
            Log.e("getCars(Driver): ", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all cars linked to a driver (asynchonous)
     *
     * @param driver   driver
     * @param callback callback method to handle the result
     */
    @Override
    public void getCars(Driver driver, FindCallback<Car> callback) {
        ParseQuery<DriverCarMapping> queryMapping = ParseQuery.getQuery(DriverCarMapping.class);
        queryMapping.whereEqualTo("driver", driver.getUsername());
        ParseQuery<Car> queryCar = ParseQuery.getQuery(Car.class);
        queryCar.whereMatchesKeyInQuery("licensePlate", "car", queryMapping);

        queryCar.findInBackground(callback);
    }

    /**
     * Get all drivers linked to a car (asynchonous)
     *
     * @param car   car
     * @param callback callback method to handle the result
     */
    @Override
    public void getDrivers(Car car, FindCallback<Driver> callback) {
        ParseQuery<DriverCarMapping> queryMapping = ParseQuery.getQuery(DriverCarMapping.class);
        queryMapping.whereEqualTo("car", car.getLicensePlate());
        ParseQuery<Driver> queryDriver = ParseQuery.getQuery(Driver.class);
        queryDriver.whereMatchesKeyInQuery("username", "driver", queryMapping);

        queryDriver.findInBackground(callback);
    }

    /**
     * Get all trips of a car (synchronous)
     *
     * @param car car
     * @return List with all trips of the car
     */
    @Override
    public List<Trip> getTrips(Car car) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo("car", car.getLicensePlate());
        try {
            return query.find();
        } catch (ParseException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Get all trips of a car (asynchronous)
     *
     * @param car      car
     * @param callback callback method to handle the result
     */
    @Override
    public void getTrips(Car car, FindCallback<Trip> callback) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo("car", car.getLicensePlate());
        query.findInBackground(callback);
    }

    /**
     * Store new trip (synchronous)
     *
     * @param trip new trip
     * @return true ok, false error
     */
    @Override
    public boolean store(Trip trip) {
        try {
            trip.save();
            return true;
        } catch (ParseException e) {
            Log.e("store(Trip)", "Storing new trip failed!");
            return false;
        }
    }

    /**
     * Store new trip (asynchronous)
     *
     * @param trip     new trip
     * @param callback callback method to handle the result
     */
    @Override
    public void store(Trip trip, SaveCallback callback) {
        trip.saveInBackground(callback);
    }

    /**
     * Register new user (synchronous)
     *
     * @param driver new user
     * @return true ok, false error
     */
    @Override
    public boolean registerUser(Driver driver) {
        ParseQuery<Driver> query = ParseQuery.getQuery(Driver.class);
        query.whereEqualTo("username", driver.getUsername());
        try {
            if (query.getFirst() == null) {
                driver.save();
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public void registerUser(final Driver newDriver, final SaveCallback callback) {
        ParseQuery<Driver> query = ParseQuery.getQuery(Driver.class);
        query.whereEqualTo("username", newDriver.getUsername());
        query.getFirstInBackground(new GetCallback<Driver>() {
            @Override
            public void done(Driver driver, ParseException e) {
                if (e == null && driver != null) {
                    callback.done(new ParseException(ParseException.USERNAME_TAKEN, "username already taken"));
                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    newDriver.saveInBackground(callback);
                } else {
                    callback.done(e);
                }
            }
        });
    }

    /**
     * Add new car to the database
     *
     * @param newCar   car object to save, license plate and admin have to be set!
     * @param callback callback method: done(ParseException e)
     *                 possible ParseExceptions:   MISSING_OBJECT_ID (license plate or admin is missing)
     *                 DUPLICATE_VALUE (license plate already taken)
     *                 CONNECTION_FAILED (no internet connection)
     */
    @Override
    public void addCar(final Car newCar, final SaveCallback callback) {
        if (newCar.getLicensePlate() == null || newCar.getAdmin() == null) {
            callback.done(new ParseException(ParseException.MISSING_OBJECT_ID, "missing license plate or admin"));
        } else {
            ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
            query.whereEqualTo("licensePlate", newCar.getLicensePlate());
            query.getFirstInBackground(new GetCallback<Car>() {
                @Override
                public void done(Car car, ParseException e) {
                    if (e == null && car != null) {
                        callback.done(new ParseException(ParseException.DUPLICATE_VALUE, "license plate already taken"));
                    } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        newCar.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    DriverCarMapping mapping = new DriverCarMapping();
                                    mapping.setCar(newCar);
                                    mapping.setDriver(newCar.getAdmin());
                                    mapping.saveInBackground(callback);
                                } else {
                                    callback.done(e);
                                }
                            }
                        });
                    } else {
                        callback.done(e);
                    }
                }
            });
        }
    }

    /**
     * Link an existing driver to an existing car
     *
     * @param car      car object to save, license plate and admin have to be set!
     * @param driver   car object to save, license plate and admin have to be set!
     * @param callback callback method: done(ParseException e)
     *                 possible ParseExceptions:    OBJECT_NOT_FOUND (driver or car doesn't exist)
     *                 DUPLICATE_VALUE (mapping already exists)
     *                 CONNECTION_FAILED (no internet connection)
     */
    @Override
    public void linkDriverToCar(final String driver, final String car, final SaveCallback callback) {
        final ParseQuery<Driver> queryDriver = ParseQuery.getQuery(Driver.class);
        queryDriver.whereEqualTo("username", driver);
        queryDriver.getFirstInBackground(new GetCallback<Driver>() {
            @Override
            public void done(Driver d, ParseException e) {
                if (e == null) {
                    ParseQuery<Car> queryCar = ParseQuery.getQuery(Car.class);
                    queryCar.whereEqualTo("licensePlate", car);
                    queryCar.getFirstInBackground(new GetCallback<Car>() {
                        @Override
                        public void done(Car c, ParseException e) {
                            if (e == null) {
                                ParseQuery<DriverCarMapping> queryMapping = ParseQuery.getQuery(DriverCarMapping.class);
                                queryMapping.whereEqualTo("driver", driver);
                                queryMapping.whereEqualTo("car", car);
                                queryMapping.getFirstInBackground(new GetCallback<DriverCarMapping>() {
                                    @Override
                                    public void done(DriverCarMapping m, ParseException e) {
                                        if (e == null) {
                                            callback.done(new ParseException(ParseException.DUPLICATE_VALUE, "driver already linked to this car"));
                                        } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                            DriverCarMapping mapping = new DriverCarMapping();
                                            mapping.setDriver(driver);
                                            mapping.setCar(car);
                                            mapping.saveInBackground(callback);
                                        } else {
                                            callback.done(e);
                                        }
                                    }
                                });
                            } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                callback.done(new ParseException(ParseException.OBJECT_NOT_FOUND, "car not found"));
                            } else {
                                callback.done(e);
                            }
                        }
                    });

                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                    callback.done(new ParseException(ParseException.OBJECT_NOT_FOUND, "driver not found"));
                } else {
                    callback.done(e);
                }
            }
        });
    }
}
