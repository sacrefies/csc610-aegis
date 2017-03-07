//------------------------------------------------------------------------------
//  File       : Markable.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 12/13/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.teamc.aegis.model;


import android.content.Context;
import android.location.Address;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

/**
 * An interface which defines common behaviors of a location/position object.
 */
public interface Markable {
    /**
     * Get the longitude value.
     *
     * @return A {@code double} which represents the longitude.
     */
    double getLng();

    /**
     * Get the latitude value
     *
     * @return A {@code double} which represents the latitude.
     */
    double getLat();

    void setLng(double lng);

    void setLat(double lat);

    void setLabel(String label);

    /**
     * Get the label for this location/position
     *
     * @return A {@code String} which is the title/label of this
     * location/position.
     */
    String getLabel();

    /**
     * Convert this instance to a {@link LatLng} instance.
     *
     * @return An instance of {@link LatLng}.
     * @see LatLng
     */
    LatLng toLatLng();

    /**
     * Convert this instance to an {@link Address} instance
     *
     * @param context The current context.
     * @return An instance of {@link Address}.
     * @throws IOException
     * @see Address
     * @see Context
     */
    Address toAddress(Context context) throws IOException;
}
