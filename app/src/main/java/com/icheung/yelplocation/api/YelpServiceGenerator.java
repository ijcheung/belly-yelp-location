package com.icheung.yelplocation.api;

import com.icheung.yelplocation.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class YelpServiceGenerator {
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(YelpApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static YelpApi createService() {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET);
        consumer.setTokenWithSecret(BuildConfig.TOKEN, BuildConfig.TOKEN_SECRET);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(YelpApi.class);
    }
}