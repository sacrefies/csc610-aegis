package net.teamc.aegis.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A proxy class that represents a 2D map position to bridge a JSON geo-location object to a Google Map's {@link LatLng}
 * or an {@link Address} object.
 */
public class Point implements Markable {
    /**
     * lat: y coordinate
     */
    protected double lat;
    /**
     * lng: x coordinate
     */
    protected double lng;
    /**
     * The label of this position
     */
    protected String label;

    /**
     * Construct an instance of class {@code Position} and initialize the properties.
     *
     * @param lat The latitude of a position.
     * @param lng The longitude of a position.
     */
    public Point(double lng, double lat) {
        this.lat = lat;
        this.lng = lng;
    }

    public Point() {
    }

    @Override
    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Get the latitude of this map position.
     *
     * @return A {@code double} which represents the latitude.
     */
    @Override
    public double getLat() {
        return lat;
    }

    /**
     * Get the longitude of this map position.
     *
     * @return A {@code double} wichi represents the longitude.
     */
    @Override
    public double getLng() {
        return lng;
    }

    /**
     * Translate this instance of class {@code Position} to its {@code String} representation: (x, y).
     *
     * @return A {@code String} in (x, y) format which represents this position.
     */
    @Override
    public String toString() {
        return String.format(Locale.US, "(%1$.2f, %2$.2f)", this.lng, this.lat);
    }

    /**
     * {@inheritDoc}
     *
     * @param other An instance of {@code Point2D}.
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        // same reference
        if (other == this) return true;
        if (!(other instanceof Point)) return false;
        Point p = (Point)other;
        int amplifier = 1000000;
        return ((p.getLat() - this.lat) * amplifier <= 1
                && (p.getLng() - this.lng) * amplifier <= 1);
    }

    /**
     * Get the label for this location/position
     *
     * @return A {@code String} which is the title/label of this location/position.
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * Set the label of this position
     *
     * @param label The descriptive label of this position.
     */
    public void setLabel(String label) {
        if (label != null && !label.trim().equals(""))
            this.label = label;
    }

    /**
     * Convert this instance to a {@link LatLng} instance.
     *
     * @return Returns an instance of {@link LatLng}
     * @see LatLng
     */
    @Override
    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }

    /**
     * Convert this instance to an {@link Address} instance. <p>Note: It's better to call this method from a thread
     * separate from the UI thread.</p>
     *
     * @param context The current context.
     * @return An instance of {@link Address}, or null if no specific address is found.
     * @throws IOException
     * @see Address
     * @see Context
     */
    @Override
    public Address toAddress(Context context) throws IOException {
        Geocoder coder = new Geocoder(context);
        List<Address> addresses = coder.getFromLocation(lat, lng, 1);
        return (addresses == null || addresses.isEmpty()) ?
                null : addresses.get(0);
    }
}
