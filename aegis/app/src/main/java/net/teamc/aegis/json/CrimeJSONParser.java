package net.teamc.aegis.json;

import android.util.Log;
import net.teamc.aegis.model.Crime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * Class {@code CrimeJSONParser} provides a process to download and parse the raw crime instances to an array of {@link
 * Crime} objects.
 */
public class CrimeJSONParser {

    /**
     * Makes the string into a URL
     */
    private URL crimeAPI = null;

    /**
     * takes in a URL in string form and sets it as a URL and also parses through and creates array list of string json
     * objects
     *
     * @param crimeURL
     */
    public CrimeJSONParser(String crimeURL) {
        try {
            crimeAPI = new URL(crimeURL);
        } catch (MalformedURLException mue) {
            Log.e(CLAZZ, "Failed to construct the crime data API URL", mue);
            crimeAPI = null;
        }
    }

    /**
     * Download the raw JSON data from the given API url.
     *
     * @return A string which is the raw JSON data.
     * @throws IOException           When the http connection is bad.
     * @throws IllegalStateException When the crime API URI is bad.
     */
    private String download() throws IOException, IllegalStateException {
        if (crimeAPI == null)
            throw new IllegalStateException("Bad crime data API URI");
        HttpsURLConnection conn = (HttpsURLConnection)crimeAPI.openConnection();
        int rc = conn.getResponseCode();
        // no need to continue
        if (rc != 200) return null;
        StringBuilder buff = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) buff.append(line);
        br.close();
        conn.disconnect();
        return buff.toString();
    }

    /**
     * Parse the downloaded JSON raw data to an array of {@link Crime} objects
     *
     * @return An array of {@link Crime} instances.
     */
    public Crime[] parseCrimes() {
        try {
            // download it again
            String raw = download();
            if (raw == null || raw.trim().equals(""))
                throw new IllegalStateException("Failed to download crime data");

            // test whether the api returned some bad news
            if (raw.startsWith("{")) {
                // error codes
                JSONObject error = new JSONObject(raw);
                Log.e(CLAZZ, error.getString("errorCode"));
                Log.d(CLAZZ, "The crime API returned error:\n" + error.getString("message"));
                return null;
            }
            // no error, continue
            JSONArray rawCrimes = new JSONArray(raw);
            // the query returns nothing, no need to continue
            if (rawCrimes.length() == 0) {
                Log.d(CLAZZ, "The raw crime JSON array is empty");
                return null;
            }
            Crime[] crimes = new Crime[rawCrimes.length()];
            convert(rawCrimes, 0, crimes.length - 1, crimes);
            return crimes;
        } catch (IOException ioe) {
            Log.e(CLAZZ, "Failed to download crime data", ioe);
        } catch (IllegalStateException stateException) {
            Log.e(CLAZZ, "Failed to proceed with downloading", stateException);
        } catch (JSONException je) {
            Log.e(CLAZZ, "Failed to process raw crime data", je);
        } catch (Exception others) {
            Log.e(CLAZZ, "Unexpected exception happened", others);
        }
        return null;
    }

    /**
     * A binary searching algorithm to speed up the conversion from {@link JSONArray} to an array of {@link Crime}.
     *
     * @param rawData A {@link JSONArray} to convert
     * @param start   The start index
     * @param end     The end index
     * @param out     The array of {@link Crime} as the conversion result
     * @throws JSONException
     */
    private void convert(JSONArray rawData, int start, int end, Crime[] out) throws JSONException {
        if (start == end) {
            out[start] = new Crime(rawData.getJSONObject(start));
            return;
        }
        int med = (start + end) >> 1;
        convert(rawData, start, med, out);
        convert(rawData, med + 1, end, out);
    }

    private static final String CLAZZ = CrimeJSONParser.class.getSimpleName();
}
