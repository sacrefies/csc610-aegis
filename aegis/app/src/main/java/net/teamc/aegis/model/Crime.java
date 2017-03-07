package net.teamc.aegis.model;

import android.util.Log;
import net.teamc.aegis.mapservice.ServiceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

/**
 * Created by Peter on 12/1/2016. Takes a JSONObject and makes a Crime object
 */

public class Crime extends Point {

    private JSONObject theCrime;
    private LocalDateTime dispatchDateTime;
    private int hour;
    private String address;
    private int ucr;
    private int district;
    private String key;

    /**
     * Construct an instance of class {@code Position} and initialize the properties.
     *
     * @param aCrime The JSONObject to be turned into a Crime Object
     */
    public Crime(JSONObject aCrime) throws JSONException {
        super();
        theCrime = aCrime;
        JSONArray shape = aCrime.getJSONObject("shape").getJSONArray("coordinates");
        lng = shape.getDouble(0);
        lat = shape.getDouble(1);

        setLabel(theCrime.has("text_general_code") ? theCrime.getString("text_general_code") : "All Other Offenses");
        dispatchDateTime = ServiceUtils.stringToDate(theCrime.getString("dispatch_date_time"));
        hour = theCrime.getInt("hour");
        address = theCrime.getString("location_block");
        ucr = theCrime.has("ucr_general") ? theCrime.getInt("ucr_general") : 2600;
        district = theCrime.getInt("dc_dist");
        key = theCrime.getString("dc_key");
    }

    /**
     * Get the raw JSON data of this crime incidence
     *
     * @return
     */
    public JSONObject getCrimeJSON() {
        return theCrime;
    }

    public LocalDateTime getDispatchDateTime() {
        return dispatchDateTime;
    }

    public int getDistrict() {
        return district;
    }

    public String getCrimeKey() {
        return key;
    }

    /**
     * Get the hour of the crime
     *
     * @return A {@code int} The hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * Get the address of the crime
     *
     * @return A {@code String} The address
     */
    public String getAddressCode() {
        return address;
    }

    /**
     * Get the UCR of the crime
     *
     * @return A {@code int} The UCR
     */
    public int getUcr() {
        return ucr;
    }

    /**
     * Tag for logging
     */
    private static final String CLAZZ = Crime.class.getSimpleName();
}
