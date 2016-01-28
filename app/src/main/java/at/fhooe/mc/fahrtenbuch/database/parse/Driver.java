package at.fhooe.mc.fahrtenbuch.database.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Database object class: Driver
 */
@ParseClassName("Driver")
public class Driver extends ParseObject {
    /**
     * Username (primary key)
     *
     * @return Username
     */
    public String getUsername() {
        return getString("username");
    }

    /**
     * Username (primary key)
     *
     * @param value Username
     */
    public void setUsername(String value) {
        put("username", value);
    }

    /**
     * encrypted password (MD5)
     *
     * @return encrypted password (MD5)
     */
    public String getPassword() {
        return getString("password");
    }

    /**
     * encrypted password (MD5)
     *
     * @param value encrypted password (MD5)
     */
    public void setPassword(String value) {
        put("password", value);
    }

    /**
     * first name
     *
     * @return first name
     */
    public String getFirstName() {
        return getString("firstName");
    }

    /**
     * first name
     *
     * @param value first name
     */
    public void setFirstName(String value) {
        put("firstName", value);
    }

    /**
     * last name
     *
     * @return last name
     */
    public String getLastName() {
        return getString("lastName");
    }

    /**
     * last name
     *
     * @param value last name
     */
    public void setLastName(String value) {
        put("lastName", value);
    }

    /**
     * birthday
     *
     * @return birthday
     */
    public Date getBirthday() {
        return getDate("birthday");
    }

    /**
     * birthday
     *
     * @param value birthday
     */
    public void setBirthday(Date value) {
        put("birthday", value);
    }

    /**
     * Get string representation of this object
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}
