package net.teamc.aegis.mapservice;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GoogleMapLocationService extends AsyncTask<String, Void, List<LatLng>>{

    public GoogleMapLocationService(Context context) {
        this.context = context;
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
        Toast.makeText(context, "Start decoding the address", Toast.LENGTH_LONG).show();
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified result is the value returned by {@link
     * #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param latLngs The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<LatLng> latLngs) {
        super.onPostExecute(latLngs);
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
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Override this method to perform a computation on a background thread. The specified parameters are the parameters
     * passed to {@link #execute} by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<LatLng> doInBackground(String... params) {
        return null;
    }

    private Context context;

    //public interface
}
