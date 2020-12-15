package com.multiqrscanner.network.user;

import com.multiqrscanner.network.model.RetroInboundId;
import com.multiqrscanner.network.model.RetroInboundVerifyRequest;
import com.multiqrscanner.network.model.RetroInbounds;
import com.multiqrscanner.network.model.RetroInboundsDetail;
import com.multiqrscanner.network.model.RetroInboundsVerifyResponse;
import com.multiqrscanner.network.model.RetroWarehouse;

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

    @Headers({
            "Authorization:Bearer b5c1b8f3-a8d7-3055-a3eb-b451424958d4",
            "Content-Type:application/json",
            "Accept:application/json",
            "X-Client-ID:DD2DC0A311F64EF28FD06D4C970581E1",
    })
    @POST("inbound-item")
    Call<RetroInboundsDetail> getInboundItemDetail(@Body RetroInboundId id);

    @Headers({
            "Authorization:Bearer b5c1b8f3-a8d7-3055-a3eb-b451424958d4",
            "Content-Type:application/json",
            "Accept:application/json",
            "X-Client-ID:DD2DC0A311F64EF28FD06D4C970581E1",
    })
    @POST("inbound-item")
    Call<RetroInboundsVerifyResponse> verifyInboundItemDetail(@Body RetroInboundVerifyRequest id);
}