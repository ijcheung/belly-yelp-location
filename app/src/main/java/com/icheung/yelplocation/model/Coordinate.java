package com.icheung.yelplocation.model;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private double latitude;
    private double longitude;

    public Coordinate() { }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override public String toString() {
        return String.format("%.1f,%.1f", latitude, longitude);
    }
}
