package com.icheung.yelplocation.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Business implements Serializable {
    private String name;
    @SerializedName("image_url") private String thumb;
    private double rating;
    @SerializedName("is_closed") private boolean isClosed;
    private Location location;
    private List<List<String>> categories;
    private double distance;

    public Business() { }

    public Business(String name, String thumb, double rating, boolean isClosed, Location location, List<List<String>> categories) {
        this.name = name;
        this.thumb = thumb;
        this.rating = rating;
        this.isClosed = isClosed;
        this.location = location;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<List<String>> getCategories() {
        return categories;
    }

    public void setCategories(List<List<String>> categories) {
        this.categories = categories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
