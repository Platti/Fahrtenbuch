package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DriverCarMapping")
public class DriverCarMapping extends ParseObject {
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
}