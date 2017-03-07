package net.teamc.aegis.mapservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import net.teamc.aegis.R;
import net.teamc.aegis.json.CrimeJSONParser;
import net.teamc.aegis.model.Crime;
import org.threeten.bp.Instant;

import java.util.List;
import java.util.Random;

public class CrimeDataService extends AsyncTask<LatLng, Void, Crime[]> {

    private GoogleMap mMap;
    private Context context;
    private boolean recent;

    /**
     * Create an instance of class CrimeDataService
     *
     * @param mMap    The google map
     * @param context Current context (usually an Activity)
     * @param recent  To specify whether this process fetches only recent crime data.
     */
    public CrimeDataService(GoogleMap mMap, Context context, boolean recent) {
        this.mMap = mMap;
        this.context = context;
        this.recent = recent;
    }

    @Override
    protected Crime[] doInBackground(LatLng... params) {
        int yearsBack = Integer.valueOf(context.getString(R.string.crime_data_year_look_back));
        int weeksBack = Integer.valueOf(context.getString(R.string.crime_data_week_look_back));
        String url = (recent)?ServiceUtils.getEncodedURLWeeks(context, params[0].latitude, params[0].longitude, weeksBack)
                :ServiceUtils.getEncodedURLYears(context, params[0].latitude, params[0].longitude, yearsBack);
        Log.d(CLAZZ, url);
        CrimeJSONParser parser = new CrimeJSONParser(url);
        return parser.parseCrimes();
    }

    @Override
    protected void onPostExecute(Crime[] crimes) {
        super.onPostExecute(crimes);
        if (crimes == null || crimes.length == 0) {
            Log.d(CLAZZ, "No crime data received");
            return;
        }
        Random rand = new Random(Instant.now().toEpochMilli());
        MapMarker crimeMarker = new MapMarker(mMap, context);
        // List<Crime> list = crimes.toList();
        // call map mark to mark every crime instance on the map;
        for (int i = 0; i < crimes.length; i++) {
            double shift = ((double)rand.nextInt(3)) / 1E5;
            //double shift = 0;
            boolean onLng = rand.nextBoolean();
            LatLng point = (onLng) ? new LatLng(crimes[i].getLat(), crimes[i].getLng() + shift) :
                    new LatLng(crimes[i].getLat() + shift, crimes[i].getLng());
            crimeMarker.pinAddress(point, crimes[i].getLabel());
        }
    }

    private static final String CLAZZ = CrimeDataService.class.getSimpleName();

}
