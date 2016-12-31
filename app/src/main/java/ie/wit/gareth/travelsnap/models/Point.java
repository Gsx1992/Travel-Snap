package ie.wit.gareth.travelsnap.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Gareth on 08/02/2015.
 */
public class Point implements ClusterItem  {

    private double latitude;
    private double longitude;
    private Bitmap icon;
    private final LatLng mPosition;

    public Point(double latitude, double longitude, Bitmap icon) {
        mPosition = new LatLng(latitude, longitude);
        this.icon = icon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
