package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Driver")
public class Driver extends ParseObject {
    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String value) {
        put("username", value);
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String value) {
        put("password", value);
    }

    public String getFirstName() {
        return getString("firstName");
    }

    public void setFirstName(String value) {
        put("firstName", value);
    }

    public String getLastName() {
        return getString("lastName");
    }

    public void setLastName(String value) {
        put("lastName", value);
    }

    public Date getBirthday() {
        return getDate("birthday");
    }

    public void setBirthday(Date value) {
        put("birthday", value);
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
