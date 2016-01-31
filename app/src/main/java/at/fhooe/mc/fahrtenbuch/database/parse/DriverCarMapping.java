package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Database object class: DriverCarMapping
 */
@ParseClassName("DriverCarMapping")
public class DriverCarMapping extends ParseObject {
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
}