package com.example.multiqrscanner.network.user;

import com.example.multiqrscanner.model.RetroLogin;
import com.example.multiqrscanner.model.RetroUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetLoginService {
    @Headers({
            "Content-type: application/json"
    })
    @POST("/authentication/login")
    Call<RetroLogin> loginUser(@Body RetroUser user);
}
