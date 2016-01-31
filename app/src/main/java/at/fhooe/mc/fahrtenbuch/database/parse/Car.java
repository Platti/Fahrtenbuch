package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Database object class: Car
 */
@ParseClassName("Car")
public class Car extends ParseObject {
    /**
     * License plate (primary key)
     *
     * @return license plate
     */
    public String getLicensePlate() {
        return getString("licensePlate");
    }

    /**
     * License plate (primary key)
     *
     * @param value License plate
     */
    public void setLicensePlate(String value) {
        put("licensePlate", value);
    }

    /**
     * Administrator (foreign key: Driver)
     *
     * @return username of administrator
     */
    public String getAdmin() {
        return getString("admin");
    }

    /**
     * Check if given user is the admin of this car
     *
     * @param driver user
     * @return true if user is the admin, false otherwise
     */
    public boolean isAdmin(Driver driver) {
        return driver.getUsername().equals(getAdmin());
    }

    /**
     * Administrator (foreign key: Driver)
     *
     * @param value username of administrator
     */
    public void setAdmin(String value) {
        put("admin", value);
    }

    /**
     * Administrator (foreign key: Driver)
     *
     * @param driver driver object of administrator
     */
    public void setAdmin(Driver driver) {
        put("admin", driver.getUsername());
    }

    /**
     * Check if car has NFC
     *
     * @return true if uses NFC, false otherwise
     */
    public boolean hasNFC() {
        return (getString("NFC") != null && !getString("NFC").equals(""));
    }

    /**
     * NFC-ID
     *
     * @return NFC-ID
     */
    public String getNFC() {
        return getString("NFC");
    }

    /**
     * NFC-ID
     *
     * @param value NFC-ID
     */
    public void setNFC(String value) {
        put("NFC", value);
    }

    /**
     * Make
     *
     * @return Make
     */
    public String getMake() {
        return getString("make");
    }

    /**
     * Make
     *
     * @param value Make
     */
    public void setMake(String value) {
        put("make", value);
    }

    /**
     * Model
     *
     * @return Model
     */
    public String getModel() {
        return getString("model");
    }

    /**
     * Model
     *
     * @param value Model
     */
    public void setModel(String value) {
        put("model", value);
    }

    /**
     * Mileage in km
     *
     * @return Mileage in km
     */
    public int getMileage() {
        return getInt("mileage");
    }

    /**
     * Mileage in km
     *
     * @param value Mileage in km
     */
    public void setMileage(int value) {
        put("mileage", value);
    }

    /**
     * Add distance to the mileage in km
     *
     * @param value distance to add in km
     */
    public void addMileage(int value) {
        put("mileage", getMileage() + value);
    }

    /**
     * Get string representation of this object
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return getLicensePlate() + ": " + getMake() + " " + getModel();
    }
}
