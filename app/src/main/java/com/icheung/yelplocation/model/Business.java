package com.icheung.yelplocation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Business implements Parcelable {
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

    protected Business(Parcel in) {
        name = in.readString();
        thumb = in.readString();
        rating = in.readDouble();
        isClosed = in.readByte() != 0x00;
        location = (Location) in.readValue(Location.class.getClassLoader());
        if (in.readByte() == 0x01) {
            categories = new ArrayList<List<String>>();
            in.readList(categories, ArrayList.class.getClassLoader());
        } else {
            categories = null;
        }
        distance = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(thumb);
        dest.writeDouble(rating);
        dest.writeByte((byte) (isClosed ? 0x01 : 0x00));
        dest.writeValue(location);
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
        dest.writeDouble(distance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };
}
