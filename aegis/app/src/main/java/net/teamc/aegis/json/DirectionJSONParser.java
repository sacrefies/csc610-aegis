package net.teamc.aegis.json;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which downloads and parse JSON data to way points. <p>Note: Make sure use this class in a separate thread
 * other than the main UI thread</p>
 */
public class DirectionJSONParser {

    /**
     * Initialize an instance of class {@code DirectionJSONParser}
     *
     * @param url The API url
     */
    public DirectionJSONParser(String url) {
        try {
            mapAPI = new URL(url);
        } catch (MalformedURLException mue) {
            Log.e(clazz, "Cannot resolve the given url", mue);
        }
    }

    /**
     * Download the direction way point data from google map direction API.
     *
     * @return A JSON string
     */
    private String download() {
        if (mapAPI == null) return null;
        try {
            Log.d(clazz, "Downloading the direction JSON data");
            HttpURLConnection conn = (HttpURLConnection)mapAPI.openConnection();
            int rc = conn.getResponseCode();
            Log.d(clazz, "HTTP Response Code: " + rc);
            // no need to continue
            if (rc != 200) return null;
            StringBuilder buff = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null)
                buff.append(line);
            br.close();
            conn.disconnect();
            return buff.toString();
        } catch (IOException ioe) {
            Log.e(clazz, "Failed to download the raw direction data", ioe);
        } catch (SecurityException se) {
            Log.e(clazz, "Lack of permissions", se);
        }
        return null;
    }

    /**
     * Get the northeast corner of the region that covers all routes.
     *
     * @return A {@link LatLng} object which is the location of the northeast corner.
     */
    public LatLng boundNortheast() {
        return northeast;
    }

    /**
     * Get the southwest corner of the region that covers all routes.
     *
     * @return A {@link LatLng} object which is the location of the southwest corner.
     */
    public LatLng boundSouthwest() {
        return southwest;
    }

    /**
     * Parse and converts a raw JSON string to a {@link List} of {@link LatLng} way point objects
     *
     * @return A {@link List} of {@link LatLng} objects
     */
    public List<List<LatLng>> parse() {
        // Do not set the raw string as a parameter like this parse(String jasonContext).
        // The parameter may destroy the stack completely
        String jsonContext = download();
        if (jsonContext == null || jsonContext.trim().equals(""))
            return null;
        try {
            final JSONObject jo = new JSONObject(jsonContext);

            // return null if something went wrong
            if (!jo.getString("status").equals("OK")) return null;

            final JSONArray routes = jo.getJSONArray("routes");
            int routeCount = routes.length();
            List<List<LatLng>> routeWayPoints = new ArrayList<>(routeCount);
            Log.d(clazz, "Google Direction API returned " + routeCount + " routes");
            for (int i = 0; i < routeCount; i++) {
                JSONObject route = routes.getJSONObject(i);

                Log.d(clazz, "Reading the overview way point data");
                String poly = route.getJSONObject("overview_polyline").getString("points");

                Log.d(clazz, "Fetching the bounds");
                final JSONObject bounds = route.getJSONObject("bounds");
                // calc max northeast, min southwest
                double lat = bounds.getJSONObject("northeast").getDouble("lat");
                double lng = bounds.getJSONObject("northeast").getDouble("lng");
                northeast = (northeast == null) ? new LatLng(lat, lng) :
                        new LatLng(Math.max(northeast.latitude, lat), Math.max(northeast.longitude, lng));
                lat = bounds.getJSONObject("southwest").getDouble("lat");
                lng = bounds.getJSONObject("southwest").getDouble("lng");
                southwest = (southwest == null) ? new LatLng(lat, lng) :
                        new LatLng(Math.min(southwest.latitude, lat), Math.min(southwest.longitude, lng));
                routeWayPoints.add(decodeWayPoints(poly));
            }
            return routeWayPoints;
        } catch (JSONException je) {
            Log.e(clazz, "Failed to parse JSON data", je);
        }
        return null;
    }

    /**
     * Decode and translate a polyline(Google Maps) into a list of locations. Special thanks to
     * http://stackoverflow.com/questions/14702621/answer-draw-path-between-two-points-using-google-maps-android-api-v2
     *
     * @param polyEncoded
     * @return
     */
    private static List<LatLng> decodeWayPoints(String polyEncoded) {
        Log.d(clazz, "Decoding way points");
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = polyEncoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double)lat / 1E5),
                    ((double)lng / 1E5));
            poly.add(p);
        }
        Log.d(clazz, "Total way points: " + poly.size());
        return poly;
    }

    /**
     * The tag for logging
     */
    private final static String clazz = DirectionJSONParser.class.getSimpleName();
    /**
     * The URL of the google direction API
     */
    private URL mapAPI;
    /**
     * The northeast corner of the region which has all routes returned by the Google Direction API
     */
    private LatLng northeast;
    /**
     * The southwest corner of the region which has all routes returned by the Google Direction API
     */
    private LatLng southwest;
}
