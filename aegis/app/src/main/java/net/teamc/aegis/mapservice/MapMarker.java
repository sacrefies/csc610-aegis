package net.teamc.aegis.mapservice;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import net.teamc.aegis.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides methods to mark and display locations on a map. And this class also provides async implementation
 * for background map update.
 */
public class MapMarker extends AsyncTask<String, Void, List<LatLng>> {


    public MapMarker(GoogleMap mMap, Context context) {
        map = mMap;
        this.context = context;
    }


    public List<LatLng> getLocations() {
        return locations;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * Translate a string to an object of {@link Address}.
     *
     * @param address
     * @return
     */
    private Address getAddress(String address) {
        Geocoder coder = new Geocoder(context);
        try {
            List<Address> addresses = coder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() != 0)
                return addresses.get(0);
        } catch (IOException ioe) {
            Log.e(clazz, "Failed to get the location's geo information", ioe);
        } catch (RuntimeException re) {
            Log.e(clazz, "Unexpected exception", re);
        }
        return null;
    }

    /**
     * Create and pin a marker on the map.
     *
     * @param loc
     * @param title
     */
    void pinAddress(LatLng loc, String title) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(loc);
        marker.title(title);
        map.addMarker(marker);
    }

    /**
     * Move map's camera to a certain location.
     *
     * @param loc
     * @param zoomIn
     */
    private void moveCameraToMarkers(LatLng loc, final double zoomIn) {
        CameraPosition.Builder camPosBuilder = CameraPosition.builder();
        camPosBuilder.target(loc);
        camPosBuilder.zoom((float)zoomIn);
        CameraPosition pos = camPosBuilder.build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    /**
     * Override this method to perform a computation on a background thread. The specified parameters are the parameters
     * passed to {@link #execute} by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param addresses The parameters of the task. This method actually accept multiple addresses
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<LatLng> doInBackground(String... addresses) {
        Log.d(clazz, "Background job: Building map locations");
        if (addresses.length == 0)
            Log.d(clazz, "No address specified");
        if (this.addresses == null)
            this.addresses = new ArrayList<>();
        if (this.locations == null)
            this.locations = new ArrayList<>();
        this.addresses.clear();
        this.locations.clear();
        for (String addr : addresses) {
            Log.d(clazz, "Building location object for: " + addr);
            Address ad = getAddress(addr);

            if (ad == null) {
                Log.d(clazz, "Location not found: " + addr);
                return null;
            }

            LatLng loc = new LatLng(ad.getLatitude(), ad.getLongitude());
            this.addresses.add(ad);
            this.locations.add(loc);
        }

        return this.locations;
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

        Log.d(clazz, "Start making markers");
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified result is the value returned by {@link
     * #doInBackground}.</p> <p/> <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param locations The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<LatLng> locations) {
        super.onPostExecute(locations);
        if (locations == null || locations.size() == 0) {
            Log.d(clazz, "Post exec: No locations received");
            return;
        }

        LatLng southwest = null;
        LatLng northeast = null;
        for (int i = 0; i < locations.size(); i++) {
            LatLng loc = locations.get(i);
            String title = addresses.get(i).getAddressLine(0);
            Log.d(clazz, "Pin address " + i + ": " + title);
            pinAddress(loc, title);
            if (i == 0)
                southwest = loc;
            if (i == locations.size())
                northeast = loc;
        }

        double zoomIn = (locations.size() > 5) ? 15.0 : 18.0;
        LatLng center = southwest;
        if (locations.size() > 1) {
            Log.d(clazz, "Set the bounds of the map view");
            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
            center = bounds.getCenter();
        }
        moveCameraToMarkers(center, zoomIn);
        Log.d(clazz, "Done with pin markers");
        //delegate.finish(locations);
        HomeActivity home = (HomeActivity)this.context;
        home.setDestination(locations.get(0));
    }

    private final String clazz = getClass().getSimpleName();
    private GoogleMap map;
    private Context context;
    private List<LatLng> locations;
    private List<Address> addresses;

}
