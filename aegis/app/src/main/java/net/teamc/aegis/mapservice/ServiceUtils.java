package net.teamc.aegis.mapservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import net.teamc.aegis.R;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * This class provides some util functions for other services
 */
public class ServiceUtils {

    /**
     * The datetime formatter for the crime data API
     */
    public static final DateTimeFormatter CRIME_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convert a date-time's string value to a {@link LocalDateTime} object
     *
     * @param date The date-time's string value
     * @return A {@link LocalDateTime} object
     * @see LocalDateTime
     */
    public static LocalDateTime stringToDate(String date) {
        return LocalDateTime.from(CRIME_DATE_TIME_FORMATTER.parse(date.trim()));
    }

    /**
     * Move and zoom the map camera to the region
     *
     * @param northeast The northeast corner
     * @param southwest The southwest corner
     * @param map       The google map instance
     */
    public static void moveCameraWithBounds(LatLng northeast, LatLng southwest, GoogleMap map) {
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        map.animateCamera(camUpdate);
    }

    /**
     * Build the Google Map Direction API address
     *
     * @param context
     * @param src
     * @param dest
     * @return
     */
    public static String getGoogleDirectionAPI(Context context, LatLng src, LatLng dest) {
        Log.d(CLAZZ, "Building Google Maps Direction API URL");
        StringBuilder url = new StringBuilder();
        url.append(context.getString(R.string.dir_api_base_url));
        url.append("?origin=");
        url.append(String.valueOf(src.latitude));
        url.append(",");
        url.append(String.valueOf(src.longitude));
        url.append("&destination=");
        url.append(String.valueOf(dest.latitude));
        url.append(",");
        url.append(String.valueOf(dest.longitude));
        url.append("&sensor=false");
        url.append("&units=imperial");
        url.append("&mode=");
        url.append(context.getString(R.string.dir_route_mode));
        url.append("&alternatives=true");
        url.append("&key=");
        url.append(context.getString(R.string.google_maps_key));
        Log.d(CLAZZ, "URL: " + url.toString());
        return url.toString();
    }

    /**
     * Constructs String of URL for the shape
     *
     * @param lat       Latitude
     * @param lng       Longitude
     * @param context   The context
     * @param yearsBack To specify the date range. It uses this value to construct a date back to (current year - {@code
     *                  yearsBack})
     */
    public static String getEncodedURLYears(Context context, double lat, double lng, int yearsBack) {
        return getEncodedURL(context, lat, lng, LocalDateTime.now().minusYears(yearsBack), LocalDateTime.now());
    }

    public static String getEncodedURLWeeks(Context context, double lat, double lng, int weeksBack) {
        return getEncodedURL(context, lat, lng, LocalDateTime.now().minusWeeks(weeksBack), LocalDateTime.now());
    }

    private static String getEncodedURL(Context context, double lat, double lng, LocalDateTime startDate,
                                        LocalDateTime endDate) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        int radius = sp.getInt(context.getResources().getString(R.string.pref_crime_search_radius), 300);
        String dateToday = ServiceUtils.CRIME_DATE_TIME_FORMATTER.format(endDate);
        String date4YearsAgo = ServiceUtils.CRIME_DATE_TIME_FORMATTER.format(startDate);
        StringBuilder where = new StringBuilder();
        where.append(String.format(Locale.US, "within_circle(shape,%1$f,%2$f,%3$d)", lat, lng, radius));
        where.append(" AND ");
        where.append(String.format(Locale.US, "(dispatch_date_time between '%1$s' and '%2$s')",
                date4YearsAgo, dateToday));
        where.append(" ORDER BY dispatch_date_time DESC LIMIT 10000");
        StringBuilder uri = new StringBuilder();
        uri.append(context.getString(R.string.crime_api_base_url));
        uri.append("?");
        uri.append(context.getString(R.string.crime_data_query_key_where));
        uri.append("=");
        try {
            return uri.append(URLEncoder.encode(where.toString(), "UTF-8")).toString();
        } catch (UnsupportedEncodingException e) {
            Log.e(CLAZZ, "Failed to encode url", e);
            return null;
        }
    }

    /**
     * Seal this class initialization
     */
    private ServiceUtils() {
    }

    private static final String CLAZZ = ServiceUtils.class.getSimpleName();
}
