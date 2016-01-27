package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Car")
public class Car extends ParseObject {
    public String getLicensePlate() {
        return getString("licensePlate");
    }

    public void setLicensePlate(String value) {
        put("licensePlate", value);
    }

    public String getAdmin() {
        return getString("admin");
    }

    public boolean isAdmin(Driver driver) {
        return driver.getUsername().equals(getAdmin());
    }

    public void setAdmin(String value) {
        put("admin", value);
    }

    public void setAdmin(Driver driver) {
        put("admin", driver.getUsername());
    }

    public boolean hasNFC() {
        return (getString("NFC") != null && !getString("NFC").equals(""));
    }

    public String getNFC() {
        return getString("NFC");
    }

    public void setNFC(String value) {
        put("NFC", value);
    }

    public String getMake() {
        return getString("make");
    }

    public void setMake(String value) {
        put("make", value);
    }

    public String getModel() {
        return getString("model");
    }

    public void setModel(String value) {
        put("model", value);
    }

    public int getMileage() {
        return getInt("mileage");
    }

    public void setMileage(int value) {
        put("mileage", value);
    }

    public void addMileage(int value) {
        put("mileage", getMileage() + value);
    }

    @Override
    public String toString() {
        return getLicensePlate() + ": " + getMake() + " " + getModel();
    }
}
