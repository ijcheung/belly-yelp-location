package com.icheung.yelplocation.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.icheung.yelplocation.model.Coordinate;

public class Utilities {
    public static void showSnackbar(View parent, int errorCode) {
        Snackbar.make(parent, errorCode, Snackbar.LENGTH_INDEFINITE)
                .show();
    }

    public static void showSnackbar(View parent, int errorCode, View.OnClickListener listener) {
        Snackbar.make(parent, errorCode, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", listener)
                .show();
    }

    public static double calculateHaversineDistance(Coordinate c1, Coordinate c2) {
        double radius = 3961; //Radius of Earth in mi
        double lat1 = toRadians(c1.getLatitude());
        double lon1 = toRadians(c1.getLongitude());
        double lat2 = toRadians(c2.getLatitude());
        double lon2 = toRadians(c2.getLongitude());
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius * c;
    }

    private static double toRadians(double degrees) {
        return degrees * Math.PI/180;
    }
}
