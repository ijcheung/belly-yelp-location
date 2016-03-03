package com.icheung.yelplocation.api;

import com.icheung.yelplocation.model.BusinessWrapper;
import com.icheung.yelplocation.model.Coordinate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YelpApi {
    //https://api.yelp.com/v2/search/?location=San Francisco, CA
    public static final String BASE_URL = "https://api.yelp.com";

    @GET("v2/search")
    public Call<BusinessWrapper> search(
            @Query("location") String location,
            @Query("cll")Coordinate coordinate
    );
}