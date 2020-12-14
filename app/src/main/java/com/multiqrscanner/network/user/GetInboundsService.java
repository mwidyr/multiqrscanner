package com.multiqrscanner.network.user;

import com.multiqrscanner.network.model.RetroInbounds;
import com.multiqrscanner.network.model.RetroLogin;
import com.multiqrscanner.network.model.RetroUser;
import com.multiqrscanner.network.model.RetroWarehouse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetInboundsService {
    @Headers({
            "Authorization:Bearer b5c1b8f3-a8d7-3055-a3eb-b451424958d4",
            "Content-Type:application/json",
            "Accept:application/json",
            "X-Client-ID:DD2DC0A311F64EF28FD06D4C970581E1",
    })
    @POST("inbounds")
    Call<RetroInbounds> getInbounds(@Body RetroWarehouse warehouse);
}