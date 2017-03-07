package net.teamc.aegis.mapservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import net.teamc.aegis.json.DirectionJSONParser;
import net.teamc.aegis.model.ColorCode;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides methods and async methods to calculate and draw routes on map between locations.
 */
public class DirectionService extends AsyncTask<LatLng, String, List<List<LatLng>>> {

    /**
     * Create and initialize an instance of class {@code MapDirection}
     *
     * @param map     The google map object
     * @param context The current application context. This parameter is for this service to get the resources
     */
    public DirectionService(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }

    /**
     * Get direction way-points between 2 locations
     *
     * @param src  The source
     * @param dest The destination
     * @return The way points
     */
    private List<List<LatLng>> getDirection(LatLng src, LatLng dest) {
        String url = ServiceUtils.getGoogleDirectionAPI(context, src, dest);
        DirectionJSONParser parser = new DirectionJSONParser(url);
        List<List<LatLng>> wp = parser.parse();
        northeast = parser.boundNortheast();
        southwest = parser.boundSouthwest();
        return wp;
    }

    /**
     * Draw one direction on the map
     *
     * @param wayPoints The way points
     * @param code      The color code
     */
    private void drawDirection(List<LatLng> wayPoints, ColorCode code) {
        // dark red
        int color = Color.rgb(85, 0, 0);
        switch (code) {
            case GREEN:
                color = Color.GREEN;
                break;
            case GRAY:
                color = Color.GRAY;
                break;
            case YELLOW:
                color = Color.YELLOW;
                break;
            case ORANGE:
                color = Color.rgb(255, 116, 0);
                break;
            case RED:
                color = Color.RED;
                break;
        }

        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(wayPoints)
                .width(14)
                .color(color)
                .geodesic(true)
                .clickable(true)
        );
    }

    /**
     * Override this method to perform a computation on a background thread. The specified parameters are the parameters
     * passed to {@link #execute} by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<List<LatLng>> doInBackground(LatLng... params) {
        if (params.length != 2) {
            Log.e(clazz, "Either the source or the destination location is not given");
            return null;
        }
        if (colors == null) colors = new ArrayList<>();
        if (colors.size() > 0) colors.clear();
        try {
            publishProgress("Calculating routes", "0");
            List<List<LatLng>> wayPoints = getDirection(params[0], params[1]);
            publishProgress("Downloading crime data", "10");
            for (int i = 0; i < wayPoints.size(); i++) {
                publishProgress("Assessing route " + i, String.valueOf(10 + 20 * (i + 1)));
                CredibilityWtdForecast forecast = new CredibilityWtdForecast(wayPoints.get(i), context);
                ColorCode code = forecast.getCrimeForecastColorCode();
                Log.d(clazz, code.toString());
                colors.add(code);
            }
            return wayPoints;
        } catch (Exception e) {
            Log.e(clazz, "Unexpected exception happened", e);
        }
        return null;
    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progress.setMax(100);
        progress.show();
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked. The specified values are the values passed to
     * {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        progress.setMessage(values[0]);
        progress.setProgress(Integer.valueOf(values[1]));
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified result is the value returned by {@link
     * #doInBackground}.</p> <p/> <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param wayPoints The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<List<LatLng>> wayPoints) {
        super.onPostExecute(wayPoints);
        if (wayPoints == null || wayPoints.size() == 0) {
            progress.dismiss();
            Toast.makeText(context, "No routes received", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < wayPoints.size(); i++) {
            progress.setMessage("Drawing route " + i);
            progress.setProgress(progress.getProgress() + (i + 1) * 10);
            drawDirection(wayPoints.get(i), colors.get(i));
        }
        ServiceUtils.moveCameraWithBounds(northeast, southwest, map);
        progress.dismiss();
    }


    private GoogleMap map;
    private Context context;
    private LatLng northeast;
    private LatLng southwest;
    private List<ColorCode> colors;
    private ProgressDialog progress;
    private final static String clazz = DirectionService.class.getSimpleName();
}
