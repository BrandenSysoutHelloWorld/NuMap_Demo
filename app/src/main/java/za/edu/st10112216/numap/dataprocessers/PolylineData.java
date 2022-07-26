package za.edu.st10112216.numap.dataprocessers;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolylineData {

    private final Polyline polyline;
    private final DirectionsLeg leg;

    public PolylineData(Polyline polyline, DirectionsLeg leg) {
        this.polyline = polyline;
        this.leg = leg;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    @NonNull
    @Override
    public String toString() {
        return "PolylineData{" +
                "polyline=" + polyline +
                ", leg=" + leg +
                '}';
    }
}