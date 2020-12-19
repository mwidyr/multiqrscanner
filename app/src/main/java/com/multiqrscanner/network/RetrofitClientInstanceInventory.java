package com.multiqrscanner.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstanceInventory {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://centralnode.pancaran-group.co.id:8245/aware-inventory/";

    public static Retrofit getRetrofitInstanceInventory() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}